package com.devsmart.staple;


import com.devsmart.staple.runtime.*;
import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.type.*;

import com.devsmart.staple.symbols.Field;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class CCodeGen extends StapleBaseVisitor<Void> {

    private static final STGroupFile codegentemplate;

    static {
        URL codeOutputStringTemplate = CCodeGen.class.getResource("C.stg");
        //URL codeOutputStringTemplate = ClassLoader.getSystemResource("C.stg");
        codegentemplate = new STGroupFile(codeOutputStringTemplate, "UTF-8", '<', '>');
    }


    private final CompilerContext compilerContext;
    private ClassType currentClassType;

    public CCodeGen(CompilerContext ctx) {
        compilerContext = ctx;
    }


    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        currentClassType = (ClassType) compilerContext.symbols.get(ctx);

        ST classTypeTmp = codegentemplate.getInstanceOf("classTypeDecl");
        classTypeTmp.add("name", currentClassType.name);
        classTypeTmp.add("parent", (currentClassType.parent != null ? currentClassType.parent.name + "Class parent;"  : ""));
        classTypeTmp.add("functions", functions(currentClassType.functions));


        ST classObj = codegentemplate.getInstanceOf("classObjDecl");
        classObj.add("name", currentClassType.name);
        classObj.add("parent", (currentClassType.parent != null ? currentClassType.parent.name + " parent;"  : ""));
        classObj.add("fields", fields(currentClassType.fields));

        try {
            String code = classTypeTmp.render();
            compilerContext.output.write(code);

            code = classObj.render();
            compilerContext.output.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String[] functions(Collection<FunctionType> functions) {
        String[] retval = new String[functions.size()];
        int i = 0;
        for(FunctionType function : functions){
            retval[i++] = renderType(function) + ";";
        }
        return retval;
    }

    private String[] fields(Collection<Field> fields) {
        String[] retval = new String[fields.size()];
        int i = 0;
        for(Field field : fields){
            retval[i++] = String.format("%s %s;",
                    renderType(field.type),
                    field.name);
        }
        return retval;
    }

    private String renderType(Type type) {
        String retval = null;
        if(type instanceof PrimitiveType){
            retval = type.toString();
        } else if(type instanceof FunctionType){
            final FunctionType functionType = (FunctionType)type;
            ST functionTypeTmp = codegentemplate.getInstanceOf("functionType");
            functionTypeTmp.add("return", renderType(functionType.returnType));
            if(!functionType.isAnonomus) {
                functionTypeTmp.add("name", functionType.name);
            }

            Argument[] args = null;
            if(functionType.isMember) {
                args = new Argument[functionType.arguments.length + 1];
                args[0] = new Argument(new PointerType(com.devsmart.staple.runtime.Runtime.BaseObject), "self");
                System.arraycopy(functionType.arguments, 0, args, 1, functionType.arguments.length);
            } else {
                args = functionType.arguments;
            }

            String[] argsStr = new String[args.length];
            for(int i=0;i<argsStr.length;i++){
                argsStr[i] = renderType(args[i].type) + " " + args[i].name;
            }
            functionTypeTmp.add("args", argsStr);
            retval = functionTypeTmp.render();
        } else if(type instanceof PointerType){
            final PointerType pointerType = (PointerType) type;
            retval = renderType(pointerType.baseType) + "*";
        } else {
            retval = type.toString();
        }

        return retval;
    }


}
