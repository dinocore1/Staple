package com.devsmart.staple;


import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.type.ClassType;

import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.MemberFunctionType;
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
            ST functionTmp = codegentemplate.getInstanceOf("functionTypedef");
            functionTmp.add("return", function.returnType.getTypeName());
            functionTmp.add("name", function.name);
            if(function instanceof MemberFunctionType){
                Argument[] args = new Argument[function.arguments.length+1];
                args[0] = new Argument(currentClassType, "self");
                System.arraycopy(function.arguments, 0, args, 1, function.arguments.length);
                functionTmp.add("args", args);
            } else {
                functionTmp.add("args", function.arguments);
            }
            retval[i++] = functionTmp.render() + ";";
        }
        return retval;
    }

    private String[] fields(Collection<Field> fields) {
        String[] retval = new String[fields.size()];
        int i = 0;
        for(Field field : fields){
            retval[i++] = String.format("%s %s;",
                    field.type.getTypeName() + (field.type instanceof ClassType ? "*" : ""),
                    field.name);
        }
        return retval;
    }


}
