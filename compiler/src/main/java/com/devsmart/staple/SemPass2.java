package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.ClassDecl;
import com.devsmart.staple.AST.ClassFunction;
import com.devsmart.staple.AST.ClassMember;
import com.devsmart.staple.type.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class SemPass2 extends StapleBaseVisitor<ASTNode> {

    private final CompilerContext mCompilerContext;
    private Scope currentScope;

    public SemPass2(CompilerContext ctx) {
        mCompilerContext = ctx;
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
    public ASTNode visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassDecl classDecl = (ClassDecl) mCompilerContext.astTreeProperties.get(ctx);
        currentScope = classDecl.scope;

        for(StapleParser.MemberVarDeclContext m : ctx.m){
            classDecl.members.add((ClassMember) visit(m));
        }

        for(StapleParser.MemberFunctionDeclContext f : ctx.f) {
            classDecl.functions.add((ClassFunction) visit(f));
        }

        return classDecl;
    }

    @Override
    public ASTNode visitMemberVarDecl(@NotNull StapleParser.MemberVarDeclContext ctx) {
        ASTNode type = visit(ctx.t);

        ClassMember retval = new ClassMember();
        retval.type = type.type;
        retval.scope = currentScope;

        return retval;
    }

    @Override
    public ASTNode visitType(@NotNull StapleParser.TypeContext ctx) {
        final String text = ctx.getText();
        Type type = null;
        if("int".equals(text)){
            type = new Type("int32");
        } else if("float".equals(text)){
            type = new Type("float32");
        }
        else {
            type = new Type(text);
        }

        ASTNode retval = new ASTNode();
        retval.type = type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }
}
