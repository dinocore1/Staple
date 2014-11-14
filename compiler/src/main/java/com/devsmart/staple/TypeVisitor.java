package com.devsmart.staple;


import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.symbols.Variable;
import com.devsmart.staple.type.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Iterator;

public class TypeVisitor extends StapleBaseVisitor<Type> {

    private final CompilerContext compilerContext;
    private Scope currentScope;
    private ClassType currentClass;
    private Type returnType = null;

    public TypeVisitor(CompilerContext ctx, Scope scope, ClassType classType) {
        this.compilerContext = ctx;
        this.currentScope = scope;
        this.currentClass = classType;
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
    public Type visitPrimary(@NotNull StapleParser.PrimaryContext ctx) {

        final String first = ctx.getChild(0).getText();

        if(ctx.parExpression() != null){
            returnType = visit(ctx.parExpression());
        } else if(ctx.literal() != null){
            returnType = visit(ctx.literal());
        } else if(ctx.primitiveType() != null) {
            returnType = visit(ctx.primitiveType());
        } else if("this".equals(first)){
            returnType = new PointerType(currentClass);
            if(ctx.arguments() != null) {
                returnType = visit(ctx.arguments());
            }
        } else if("new".equals(first)) {
            Symbol classSymbol = currentScope.get(ctx.c.getText());
            if(classSymbol == null || !(classSymbol instanceof ClassType)){
                compilerContext.errorStream.error("undefined class: " + ctx.c.getText(), ctx.c);
            } else {
                returnType = new PointerType((ClassType) classSymbol);
            }
        } else if("super".equals(first)){
            returnType = new PointerType(currentClass.parent);
        } else {
            Iterator<TerminalNode> it = ctx.Identifier().iterator();
            TerminalNode id = it.next();
            Symbol localVar = currentScope.get(id.getText());
            if(localVar == null){
                compilerContext.errorStream.error("undefined symbol: " + id.getText(), id.getSymbol());
            } else {
                if(localVar instanceof Variable) {
                    Variable variable = (Variable) localVar;
                    returnType = variable.type;
                    compilerContext.symbols.put(id, variable);

                    ClassType baseType = null;
                    if(returnType instanceof ClassType){
                        baseType = (ClassType) returnType;
                    } else if(returnType instanceof PointerType && ((PointerType) returnType).baseType instanceof ClassType){
                        baseType = (ClassType) ((PointerType) returnType).baseType;
                    }

                    while(it.hasNext()){
                        id = it.next();

                        if(!it.hasNext() && ctx.identifierSuffix() != null){
                            FunctionType function = baseType.getFunction(id.getText());
                            compilerContext.symbols.put(id, function);
                            returnType = function.returnType;
                        } else {
                            Field field = baseType.getField(id.getText());
                            compilerContext.symbols.put(id, field);
                            returnType = field.type;
                        }
                    }
                }
            }
        }

        compilerContext.symbols.put(ctx, returnType);

        return returnType;

    }

    @Override
    public Type visitSelector(@NotNull StapleParser.SelectorContext ctx) {

        ClassType classType = null;

        if(returnType != null){
            if(returnType instanceof ClassType){
                classType = (ClassType)returnType;
            } else if(returnType instanceof PointerType) {
                if(!(((PointerType) returnType).baseType instanceof ClassType)){
                    compilerContext.errorStream.error("not a class pointer", ctx);
                    return null;
                } else {
                    classType = (ClassType) ((PointerType) returnType).baseType;
                }
            }

            if(ctx.Identifier() != null) {
                if(ctx.arguments() != null) {
                    FunctionType function = classType.getFunction(ctx.Identifier().getText());
                    returnType = function.returnType;
                } else {
                    Field field = classType.getField(ctx.Identifier().getText());
                    returnType = field.type;
                }
            }
        }

        compilerContext.symbols.put(ctx, returnType);

        return returnType;
    }

    private Type getMember(Type base, TerminalNode memberName) {
        Type retval = null;
        if(base instanceof ClassType){
            Field field = ((ClassType)base).getField(memberName.getText());
            if(field == null){
                compilerContext.errorStream.error(String.format("class type: '%s' does not have member: '%s'", ((ClassType) base).name, memberName.getText()), memberName.getSymbol());
            } else {
                retval = field.type;
            }
        } else if(base instanceof PointerType) {
            PointerType ptr = (PointerType)base;
            retval = getMember(ptr.baseType, memberName);
        } else {
            compilerContext.errorStream.error("not a class or pointer type", memberName.getSymbol());
        }

        return retval;
    }

    @Override
    public Type visitType(@NotNull StapleParser.TypeContext ctx) {
        String basetypeStr = ctx.primitiveType() != null ? ctx.primitiveType().getText() : ctx.Identifier().getText();
        returnType = getType(basetypeStr);

        if(returnType == null){
            compilerContext.errorStream.error("unknown type: " + basetypeStr, ctx);
        }

        if(ctx.POINTER() != null) {
            returnType = new PointerType(returnType);
        }
        compilerContext.symbols.put(ctx, returnType);

        return returnType;
    }

    public static HashMap<String, PrimitiveType> PrimitiveTypes = new HashMap<String, PrimitiveType>();
    static {
        PrimitiveTypes.put("void", PrimitiveType.Void);
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
