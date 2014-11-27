package com.devsmart.staple.ccodegen;


import com.devsmart.staple.CompilerContext;
import com.devsmart.staple.StapleBaseVisitor;
import com.devsmart.staple.StapleParser;
import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.FunctionType;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ClassHeaderGen extends StapleBaseVisitor<Void> {

    private final CompilerContext compilerContext;
    private final OutputStreamWriter output;

    public ClassHeaderGen(CompilerContext ctx, OutputStreamWriter output){
        this.compilerContext = ctx;
        this.output = output;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassType currentClassType = (ClassType) compilerContext.symbols.get(ctx);

        try {
            ST classTypeTmp = CCodeGen.codegentemplate.getInstanceOf("classTypeDecl");
            classTypeTmp.add("name", CCodeGen.fullClassName(currentClassType));
            classTypeTmp.add("parent", (currentClassType.parent != null ? currentClassType.parent.name + "Class parent;"  : ""));
            classTypeTmp.add("functions", functions(currentClassType));
            String code = classTypeTmp.render();
            output.write(code);


            ST classObj = CCodeGen.codegentemplate.getInstanceOf("classObjDecl");
            classObj.add("name", CCodeGen.fullClassName(currentClassType));
            classObj.add("parent", (currentClassType.parent != null ? currentClassType.parent.name + " parent;"  : ""));
            classObj.add("fields", fields(currentClassType.fields));
            code = classObj.render();
            output.write(code);

            for(FunctionType function : currentClassType.functions){
                ST initFuncTmp = CCodeGen.codegentemplate.getInstanceOf("functionDec");

                initFuncTmp.add("name", Joiner.on("_").join(CCodeGen.fullClassName(currentClassType), function.name));
                initFuncTmp.add("return", CCodeGen.renderType(function.returnType));
                initFuncTmp.add("args", CCodeGen.renderFunctionArgs(function));
                output.write(initFuncTmp.render() + ";\n");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String[] fields(Collection<Field> fields) {
        String[] retval = new String[fields.size()];
        int i = 0;
        for(Field field : fields){
            retval[i++] = String.format("%s %s;",
                    CCodeGen.renderType(field.type),
                    field.name);
        }
        return retval;
    }

    static Collection<String> functions(final ClassType currentClassType) {
        ArrayList<String> renderList = new ArrayList<String>();
        for(FunctionType function : currentClassType.functions){
            if(currentClassType.parent.getFunction(function.name) == null){
                renderList.add(CCodeGen.renderType(function) + ";");
            }
        }

        return renderList;
    }
}
