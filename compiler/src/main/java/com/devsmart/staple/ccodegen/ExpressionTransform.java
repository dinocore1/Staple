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
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Iterator;

public class ExpressionTransform extends StapleBaseVisitor<String> {

    private final CompilerContext compilerContext;
    private Type baseType;

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
            retval = "self";
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

        return retval;
    }

    @Override
    public String visitSelector(@NotNull StapleParser.SelectorContext ctx) {
        return super.visitSelector(ctx);
    }
}
