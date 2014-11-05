package com.devsmart.staple;


import com.devsmart.staple.type.ClassType;

import org.antlr.v4.runtime.misc.NotNull;

public class SemPass1 extends StapleBaseVisitor<Void> {

    private final CompilerContext compilerContext;
    private Scope currentScope;

    public SemPass1(CompilerContext ctx) {
        compilerContext = ctx;
        currentScope = compilerContext.rootScope;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        ClassType classType = new ClassType(ctx.ID().getText());
        classType.scope = new Scope(currentScope);

        currentScope.put(classType.name, classType);
        compilerContext.symbols.put(ctx, classType);

        return null;
    }
}
