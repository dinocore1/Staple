package com.devsmart.staple.ccodegen;


import com.devsmart.staple.CompilerContext;
import com.devsmart.staple.StapleBaseVisitor;
import com.devsmart.staple.StapleParser;
import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.symbols.Variable;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.PointerType;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Iterator;
import java.util.LinkedList;

public class ExpressionTransform extends StapleBaseVisitor<Void> {

    private final CompilerContext compilerContext;
    private Type baseType;
    private LinkedList<String> stack = new LinkedList<String>();

    public ExpressionTransform(CompilerContext compilerContext){
        this.compilerContext = compilerContext;
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        while(!stack.isEmpty()){
            builder.append(stack.removeLast());
        }
        return builder.toString();
    }

    @Override
    public Void visitPrimary(@NotNull StapleParser.PrimaryContext ctx) {

        final String first = ctx.getChild(0).getText();

        if(ctx.parExpression() != null){
            stack.push(String.format("(%s)", visit(ctx.parExpression())));
        } else if("new".equals(first)){
            PointerType ptr = (PointerType) compilerContext.symbols.get(ctx);
            baseType = ptr;

            final String fullClassName = CCodeGen.fullClassName((ClassType) ptr.baseType);


            //visit(ctx.arguments());
            //stack.pop();
            //stack.pop();

            String stmt = String.format("%1$s_init(CREATE_OBJ(%1$s))", fullClassName);
            stack.push(stmt);

        } else if("this".equals(first)){
            baseType = (PointerType)compilerContext.symbols.get(ctx);
            String stmt = String.format("CAST_%s(self)", CCodeGen.fullClassName((ClassType)((PointerType)baseType).baseType));
            stack.push(stmt);

            if(ctx.arguments() != null){
                visit(ctx.arguments());
            }

        } else if("super".equals(first)){
            baseType = (PointerType)compilerContext.symbols.get(ctx.getChild(0));
            String stmt = String.format("CAST_%s(self)", CCodeGen.fullClassName((ClassType)((PointerType)baseType).baseType));
            stack.push(stmt);

            if(ctx.superSuffix() != null){
                visit(ctx.superSuffix());
            }

        } else if(ctx.literal() != null){
            visit(ctx.literal());
        } else {
            StringBuilder buf = new StringBuilder();
            Iterator<TerminalNode> it = ctx.Identifier().iterator();
            TerminalNode id = it.next();

            Variable var = (Variable) compilerContext.symbols.get(id);
            Type baseType = var.type;
            buf.append(var.name);

            while(it.hasNext()){
                id = it.next();
                if(baseType instanceof PointerType){
                    buf.append("->");
                } else {
                    buf.append(".");
                }

                buf.append(id.getText());

                Object symbol = compilerContext.symbols.get(id);
                if(symbol instanceof Field){
                    baseType = ((Field) symbol).type;
                } else if(symbol instanceof FunctionType){
                    baseType = ((FunctionType) symbol).returnType;
                }
            }

            stack.push(buf.toString());
        }
        return null;
    }

    @Override
    public Void visitSelector(@NotNull StapleParser.SelectorContext ctx) {

        StringBuilder builder = new StringBuilder();



        Object symbol = compilerContext.symbols.get(ctx);

        if(symbol instanceof Field){
            if(baseType instanceof PointerType){
                builder.append("->");
            } else {
                builder.append(".");
            }

            builder.append(((Field) symbol).name);
            baseType = ((Field) symbol).type;

            String stmt = stack.pop() + builder.toString();
            stack.push(stmt);

        } else if(symbol instanceof FunctionType) {
            String stmt = String.format("%s_%s(%s",
                    CCodeGen.fullClassName((ClassType)baseType),
                    ((FunctionType) symbol).name,
                    stack.pop());

            ExpressionTransform tx = new ExpressionTransform(compilerContext);
            tx.visit(ctx.arguments());
            stmt += tx.render() + ")";
            stack.push(stmt);

            baseType = ((FunctionType) symbol).returnType;
        }

        return null;
    }

    @Override
    public Void visitSuperSuffix(@NotNull StapleParser.SuperSuffixContext ctx) {

        StringBuilder builder = new StringBuilder();

        Object symbol = compilerContext.symbols.get(ctx);

        if(symbol instanceof Field){
            if(baseType instanceof PointerType){
                builder.append("->");
            } else {
                builder.append(".");
            }

            builder.append(((Field) symbol).name);
            baseType = ((Field) symbol).type;

            String stmt = stack.pop() + builder.toString();
            stack.push(stmt);

        } else if(symbol instanceof FunctionType) {

            ClassType baseClass = null;
            if(baseType instanceof PointerType){
                baseClass = (ClassType) ((PointerType) baseType).baseType;
            } else {
                baseClass = (ClassType) baseType;
            }

            stack.pop();

            String stmt = String.format("%s_%s",
                    CCodeGen.fullClassName(baseClass),
                    ((FunctionType) symbol).name);

            ExpressionTransform tx = new ExpressionTransform(compilerContext);
            tx.visit(ctx.arguments());
            stmt += tx.render();
            stack.push(stmt);

            baseType = ((FunctionType) symbol).returnType;
        }

        return null;
    }

    @Override
    public Void visitLiteral(@NotNull StapleParser.LiteralContext ctx) {
        String value = ctx.getText();
        if("null".equals(value)){
            value = "NULL";
        }

        stack.push(value);

        return null;
    }

    @Override
    public Void visitTerminal(@NotNull TerminalNode node) {
        stack.push(node.getText());

        return null;
    }



}
