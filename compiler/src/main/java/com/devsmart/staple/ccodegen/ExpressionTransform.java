package com.devsmart.staple.ccodegen;


import com.devsmart.staple.CompilerContext;
import com.devsmart.staple.StapleBaseVisitor;
import com.devsmart.staple.StapleParser;
import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.symbols.Variable;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.PointerType;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Iterator;

public class ExpressionTransform extends StapleBaseVisitor<String> {

    private final CompilerContext compilerContext;
    private Type baseType;
    //StringBuilder builder = new StringBuilder();

    public ExpressionTransform(CompilerContext compilerContext){
        this.compilerContext = compilerContext;
    }

    @Override
    public String visitPrimary(@NotNull StapleParser.PrimaryContext ctx) {
        String retval = "";

        final String first = ctx.getChild(0).getText();
        if("new".equals(first)){
            PointerType ptr = (PointerType) compilerContext.symbols.get(ctx);
            retval = String.format("CREATE_OBJ(%s)", ptr.baseType);

            baseType = ptr;

        } else if("this".equals(first)){
            //builder = new StringBuilder();
            baseType = (PointerType)compilerContext.symbols.get(ctx);
            retval = "self";
        } else if(ctx.literal() != null){
            retval = visit(ctx.literal());
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

            retval = buf.toString();
        }

        //builder.append(retval);

        return retval;
    }

    @Override
    public String visitSelector(@NotNull StapleParser.SelectorContext ctx) {

        StringBuilder builder = new StringBuilder();

        if(baseType instanceof PointerType){
            builder.append("->");
        } else {
            builder.append(".");
        }

        Object symbol = compilerContext.symbols.get(ctx);

        if(symbol instanceof Field){
            builder.append(((Field) symbol).name);
            baseType = ((Field) symbol).type;
        } else if(symbol instanceof FunctionType) {
            builder.append(((FunctionType) symbol).name);
            builder.append("()");
            baseType = ((FunctionType) symbol).returnType;
        }

        return builder.toString();

    }

    @Override
    public String visitLiteral(@NotNull StapleParser.LiteralContext ctx) {
        final String value = ctx.getText();
        if("null".equals(value)){
            return "NULL";
        } else {
            return value;
        }
    }

    @Override
    public String visitTerminal(@NotNull TerminalNode node) {
        return node.getText();
    }

    @Override
    protected String defaultResult() {
        return "";
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        return aggregate + nextResult;
    }
}
