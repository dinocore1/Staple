package com.devsmart.staple;


import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.Field;
import com.devsmart.staple.type.PrimitiveType;
import com.devsmart.staple.type.Type;

import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;

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

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        currentClass = (ClassType) compilerContext.symbols.get(ctx);

        StapleParser.ExtendsDeclContext extendDecl = ctx.extendsDecl();
        if(extendDecl != null) {
            visit(ctx.extendsDecl());
        }

        Scope parentScope = currentScope;
        currentScope = currentClass.scope;

        for(StapleParser.ClassMemberDeclContext memberDecl : ctx.classMemberDecl()){
            visit(memberDecl);
        }

        for(StapleParser.ClassFunctionDeclContext functionDecl : ctx.classFunctionDecl()){
            visit(functionDecl);
        }

        currentScope = parentScope;

        return null;
    }

    @Override
    public Void visitExtendsDecl(@NotNull StapleParser.ExtendsDeclContext ctx) {

        final String className = ctx.ID().getText();
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

        final String typeStr = ctx.type().getText();
        Type type = getType(typeStr);
        if(type == null){
            compilerContext.errorStream.error("Could not determine type of: " + typeStr, ctx.type());
        } else {
            final String name = ctx.ID().getText();
            if(currentClass.getField(name) != null){
                compilerContext.errorStream.error("redefinition of field: " + name, ctx);
            } else {
                Field field = new Field(type, name);
                currentClass.fields.add(field);
                compilerContext.symbols.put(ctx, field);
            }

        }

        return null;
    }

    public static HashMap<String, PrimitiveType> PrimitiveTypes = new HashMap<String, PrimitiveType>();
    static {
        PrimitiveTypes.put("int", PrimitiveType.Int32);
        PrimitiveTypes.put("uint", PrimitiveType.UInt32);

        PrimitiveTypes.put("int8", PrimitiveType.Int8);
        PrimitiveTypes.put("int16", PrimitiveType.Int16);
        PrimitiveTypes.put("int32", PrimitiveType.Int32);
        PrimitiveTypes.put("int64", PrimitiveType.Int64);

        PrimitiveTypes.put("bool", PrimitiveType.Bool);
    }

    private Type getType(String typeStr) {

        Type retval = PrimitiveTypes.get(typeStr);
        if(retval == null){
            Symbol symbol = currentScope.get(typeStr);
            if(symbol != null && symbol instanceof ClassType){
                retval = (ClassType)symbol;
            }
        }

        return retval;
    }
}
