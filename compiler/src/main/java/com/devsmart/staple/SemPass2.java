package com.devsmart.staple;


import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.symbols.LocalVariable;
import com.devsmart.staple.type.*;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SemPass2 extends StapleBaseVisitor<Void> {

    private final CompilerContext compilerContext;
    private Scope currentScope;
    private ClassType currentClass;

    public SemPass2(CompilerContext ctx) {
        compilerContext = ctx;
        currentScope = compilerContext.rootScope;
    }

    private void pushScope() {
        currentScope = new Scope(currentScope);
    }

    private void popScope() {
        if(currentScope != null){
            currentScope = currentScope.mParent;
        }
    }

    private TypeVisitor createTypeVisitor() {
        return new TypeVisitor(compilerContext, currentScope, currentClass);
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        ClassType lastClass = currentClass;
        currentClass = (ClassType) compilerContext.symbols.get(ctx);

        StapleParser.ExtendsDeclContext extendDecl = ctx.extendsDecl();
        if(extendDecl != null) {
            visit(ctx.extendsDecl());
        } else {
            currentClass.parent = com.devsmart.staple.runtime.Runtime.BaseObject;
        }

        Scope parentScope = currentScope;
        currentScope = currentClass.scope;

        //add init function
        {
            FunctionType initFunction = FunctionType.memberFunction("init", PrimitiveType.Void, new Argument[]{new Argument(new PointerType(PrimitiveType.Void), "self")});
            currentClass.functions.add(initFunction);
        }

        for(StapleParser.ClassDeclContext internalClass : ctx.classDecl()){
            visit(internalClass);
        }

        for(StapleParser.ClassMemberDeclContext memberDecl : ctx.classMemberDecl()){
            visit(memberDecl);
        }

        for(StapleParser.ClassFunctionDeclContext functionDecl : ctx.classFunctionDecl()){
            visit(functionDecl);
        }

        currentScope = parentScope;
        currentClass = lastClass;

        return null;
    }

    @Override
    public Void visitExtendsDecl(@NotNull StapleParser.ExtendsDeclContext ctx) {

        final String className = ctx.Identifier().getText();
        Symbol symbol = currentScope.get(className);
        if(symbol == null){
            compilerContext.errorStream.error("could not find class: " + className, ctx);
        } else if(!(symbol instanceof ClassType)){
            compilerContext.errorStream.error("symbol is not of class type: " + className, ctx);
        } else {
            currentClass.parent = (ClassType) symbol;
            compilerContext.symbols.put(ctx, symbol);
        }
        return null;
    }

    @Override
    public Void visitClassMemberDecl(@NotNull StapleParser.ClassMemberDeclContext ctx) {

        final StapleParser.TypeContext typeCtx = ctx.type();
        Type type = createTypeVisitor().visit(typeCtx);
        if(type == null){
            compilerContext.errorStream.error("Could not determine type: " + typeCtx.getText(), typeCtx);
        } else {
            final String name = ctx.Identifier().getText();
            if(currentClass.getField(name) != null){
                compilerContext.errorStream.error("redefinition of field: " + name, ctx);
            } else {
                Field field = new Field(type, name);
                currentClass.fields.add(field);
                compilerContext.symbols.put(ctx, field);

                currentScope.put(name, field);
            }

        }

        return null;
    }

    @Override
    public Void visitClassFunctionDecl(@NotNull StapleParser.ClassFunctionDeclContext ctx) {

        final String name = ctx.Identifier().getText();

        Type returnType = createTypeVisitor().visit(ctx.type());

        visit(ctx.argList());

        ArrayList<Argument> arguments = (ArrayList<Argument>) compilerContext.symbols.get(ctx.argList());

        Argument[] argArray = arguments.toArray(new Argument[arguments.size()]);
        FunctionType functionType = FunctionType.memberFunction(name, returnType, argArray);

        currentClass.functions.add(functionType);
        compilerContext.symbols.put(ctx, functionType);

        Scope blockScope = new Scope(currentScope);
        StapleParser.BlockContext blockCtx = ctx.block();
        compilerContext.scope.put(blockCtx, blockScope);

        blockScope.put("thiz", new Argument(currentClass, "thiz"));

        for(Argument arg : arguments){
            blockScope.put(arg.name, arg);
        }

        visit(blockCtx);

        return null;
    }

    @Override
    public Void visitArgList(@NotNull StapleParser.ArgListContext ctx) {
        List<StapleParser.TypeContext> types = ctx.type();
        List<TerminalNode> names = ctx.Identifier();
        ArrayList<Argument> args = new ArrayList<Argument>(names.size());
        for(int i=0;i<names.size();i++){
            Type type = createTypeVisitor().visit(types.get(i));
            Argument arg = new Argument(type, names.get(i).getText());
            args.add(arg);
        }

        compilerContext.symbols.put(ctx, args);

        return null;
    }

    @Override
    public Void visitBlock(@NotNull StapleParser.BlockContext ctx) {

        //check to see if the scope already exists for this block
        Scope blockScope = compilerContext.scope.get(ctx);
        if(blockScope == null){
            blockScope = new Scope(currentScope);
            compilerContext.scope.put(ctx, blockScope);
        } else {
            currentScope = blockScope;
        }

        visitChildren(ctx);
        popScope();

        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(@NotNull StapleParser.LocalVariableDeclarationContext ctx) {

        Type type = createTypeVisitor().visit(ctx.type());

        final String name = ctx.Identifier().getText();
        Symbol conflictSymbol = currentScope.get(name);
        if(conflictSymbol != null){
            compilerContext.errorStream.error("redefinition of " + name, ctx);
        } else {

            LocalVariable localVariable = new LocalVariable(type, name);
            currentScope.put(name, localVariable);
            compilerContext.symbols.put(ctx, localVariable);
        }

        return null;
    }

    @Override
    public Void visitExpression(@NotNull StapleParser.ExpressionContext ctx) {

        StapleParser.AssignmentOperatorContext assign = ctx.assignmentOperator();
        if(assign != null && "=".equals(assign.getText())){

            StapleParser.ConditionalExpressionContext lvalue = ctx.conditionalExpression();
            Type lvalueType = createTypeVisitor().visit(lvalue);
            compilerContext.symbols.put(lvalue, lvalueType);

            StapleParser.ExpressionContext rvalue = ctx.expression();
            Type rvalueType = createTypeVisitor().visit(rvalue);
            compilerContext.symbols.put(rvalue, rvalueType);

            if(!rvalueType.isAssignableTo(lvalueType)) {
                compilerContext.errorStream.error(String.format("'%s' cannot be assigned to '%s'", rvalueType, lvalueType), ctx);
            } else {
                visit(lvalue);
                visit(rvalue);
            }

        } else {
            createTypeVisitor().visit(ctx);
        }

        return null;
    }
}
