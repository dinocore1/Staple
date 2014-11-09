package com.devsmart.staple.ccodegen;


import com.devsmart.staple.CompilerContext;
import com.devsmart.staple.StapleBaseVisitor;
import com.devsmart.staple.StapleParser;
import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.type.*;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;

public class CCodeGen extends StapleBaseVisitor<Void> {

    static final STGroupFile codegentemplate;

    static {
        URL codeOutputStringTemplate = CCodeGen.class.getResource("C.stg");
        //URL codeOutputStringTemplate = ClassLoader.getSystemResource("C.stg");
        codegentemplate = new STGroupFile(codeOutputStringTemplate, "UTF-8", '<', '>');
    }


    private final CompilerContext compilerContext;
    private ClassType currentClassType;
    private OutputStreamWriter headerOutput;
    private OutputStreamWriter codeOutput;

    public CCodeGen(CompilerContext ctx, OutputStreamWriter headerOutput, OutputStreamWriter codeOutput) {
        compilerContext = ctx;
        this.headerOutput = headerOutput;
        this.codeOutput = codeOutput;
    }

    @Override
    public Void visitCompileUnit(@NotNull StapleParser.CompileUnitContext ctx) {

        StapleParser.ClassDeclContext mainClass = ctx.classDecl();
        ClassHeaderGen mainClassHeaderGen = new ClassHeaderGen(compilerContext, headerOutput);
        mainClassHeaderGen.visit(mainClass);

        ClassHeaderGen internalClassHeaderGen = new ClassHeaderGen(compilerContext, codeOutput);
        for(StapleParser.ClassDeclContext intClass : mainClass.classDecl()){
            internalClassHeaderGen.visit(intClass);
        }

        return null;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        currentClassType = (ClassType)compilerContext.symbols.get(ctx);

        ST instanceTmp = codegentemplate.getInstanceOf("classTypeInstance");
        instanceTmp.add("name", currentClassType.name);
        instanceTmp.add("parent", currentClassType.parent != null ? currentClassType.parent.name : "NULL");
        instanceTmp.add("functions", Collections2.transform(currentClassType.functions, new Function<FunctionType, String>() {
            @Override
            public String apply(FunctionType input) {
                return input.name;
            }
        }));

        try {
            codeOutput.write(instanceTmp.render());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    static String renderType(Type type) {
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
