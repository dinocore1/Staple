package com.devsmart.staple;


import com.devsmart.staple.type.ClassType;

import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.net.URL;

public class CCodeGen extends StapleBaseVisitor<Void> {

    private static final STGroupFile codegentemplate;

    static {
        URL codeOutputStringTemplate = CCodeGen.class.getResource("C.stg");
        //URL codeOutputStringTemplate = ClassLoader.getSystemResource("C.stg");
        codegentemplate = new STGroupFile(codeOutputStringTemplate, "UTF-8", '<', '>');
    }


    private final CompilerContext compilerContext;

    public CCodeGen(CompilerContext ctx) {
        compilerContext = ctx;
    }


    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassType classType = (ClassType) compilerContext.symbols.get(ctx);

        ST classObj = codegentemplate.getInstanceOf("classObjDecl");
        classObj.add("name", classType.name);
        classObj.add("parent", classType.parent != null ? classType.parent.name : "");
        classObj.add("fields", classType.fields);

        try {
            String code = classObj.render();
            compilerContext.output.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
