package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.ClassDecl;
import com.devsmart.staple.AST.ClassFunction;
import com.devsmart.staple.AST.ClassMember;
import com.devsmart.staple.symbol.ClassSymbol;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.BoolType;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.FloatType;
import com.devsmart.staple.type.IntType;
import com.devsmart.staple.type.Type;
import com.devsmart.staple.type.VoidType;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ClassMember retval = new ClassMember();

        retval.scope = currentScope;


        ASTNode typeNode = visit(ctx.t);
        final String varName = ctx.n.getText();

        if(typeNode.type instanceof ClassType){
            ClassType classType = (ClassType)typeNode.type;
            Symbol classSymbol = currentScope.get(classType.name);

        }

        retval.type = typeNode.type;
        Symbol symbol = new Symbol(varName, typeNode.type);
        currentScope.define(symbol);

        return retval;
    }

    public static final Pattern INT_REGEX = Pattern.compile("int([0-9]*)");
    public static final Pattern FLOAT_REGEX = Pattern.compile("float([0-9]*)");

    @Override
    public ASTNode visitType(@NotNull StapleParser.TypeContext ctx) {
        final String text = ctx.getText();
        Type type = null;

        Matcher m = null;
        if((m = INT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new IntType(Integer.parseInt(m.group(1)));
            } else {
                type = IntType.INT32;
            }
        } else if((m = FLOAT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new FloatType(Integer.parseInt(m.group(1)));
            } else {
                type = FloatType.FLOAT32;
            }
        } else if("bool".equals(text)) {
            type = BoolType.BOOL;
        } else if("void".equals(text)) {
            type = VoidType.VOID;
        } else {
            Symbol classSymbol = currentScope.get(text);
            if(classSymbol == null || !(classSymbol instanceof ClassSymbol)){
                mCompilerContext.errorStream.error(String.format("unknown class: '%s'", text), ctx.getStart()));
            } else {
                type = classSymbol.type;
            }
        }

        ASTNode retval = new ASTNode();
        retval.type = type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }
}
