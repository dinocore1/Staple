package com.devsmart.staple;


import com.devsmart.staple.AST.ClassDecl;
import com.devsmart.staple.symbol.ClassSymbol;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class SemPass1 extends StapleBaseVisitor<Void> {


    private final CompilerContext mCompilerContext;
    private Scope currentScope;

    public SemPass1(CompilerContext ctx) {
        mCompilerContext = ctx;
        currentScope = mCompilerContext.rootScope;
    }

    private void pushScope() {
        currentScope = new Scope(currentScope);
    }

    private void popScope() {
        if(currentScope != null){
            currentScope = currentScope.mParent;
        }
    }

    @Override
    public Void visitCompileUnit(@NotNull StapleParser.CompileUnitContext ctx) {
        pushScope();
        visitChildren(ctx);
        popScope();

        return null;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassType classType = new ClassType(ctx.n.getText());
        ClassSymbol classSymbol = new ClassSymbol(ctx.n.getText());
        currentScope.define(classSymbol);

        ClassDecl node = new ClassDecl(classSymbol);
        mCompilerContext.astTreeProperties.put(ctx, node);

        return null;
    }
}
