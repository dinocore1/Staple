package com.devsmart.staple;


import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.PointerType;
import com.devsmart.staple.type.PrimitiveType;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;

public class TypeVisitor extends StapleBaseVisitor<Type> {

    private final CompilerContext compilerContext;
    private Scope currentScope;

    public TypeVisitor(CompilerContext ctx, Scope scope) {
        this.compilerContext = ctx;
        this.currentScope = scope;
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
    public Type visitType(@NotNull StapleParser.TypeContext ctx) {
        String basetypeStr = ctx.primitiveType() != null ? ctx.primitiveType().getText() : ctx.Identifier().getText();
        Type baseType = getType(basetypeStr);

        if(baseType == null){
            compilerContext.errorStream.error("unknown type: " + basetypeStr, ctx);
        }

        Type theType = baseType;
        if(ctx.POINTER() != null) {
            theType = new PointerType(baseType);
        }
        compilerContext.symbols.put(ctx, theType);

        return theType;
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
