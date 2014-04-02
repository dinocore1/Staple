package com.devsmart.staple;

import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.MemberAccess;
import com.devsmart.staple.symbol.ClassSymbol;
import org.antlr.v4.runtime.misc.NotNull;


public class ObjectAccessVisitor extends StapleBaseVisitor<ASTNode> {

    ASTNode left;
    ClassSymbol baseClassSymbol;
    ASTNode right;

    public ObjectAccessVisitor(ASTNode left, ClassSymbol baseClassSymbol) {
        this.left = left;
        this.baseClassSymbol = baseClassSymbol;
    }

    @Override
    public ASTNode visitSymbolReference(@NotNull StapleParser.SymbolReferenceContext ctx) {
        String memberName = ctx.v.getText();
        MemberAccess memberAccess = new MemberAccess(left, baseClassSymbol, memberName);
        right = memberAccess;
        return memberAccess;
    }

    @Override
    public ASTNode visitFunctionCall(@NotNull StapleParser.FunctionCallContext ctx) {

        return null;
    }
}
