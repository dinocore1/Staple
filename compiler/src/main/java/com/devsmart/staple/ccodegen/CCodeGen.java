package com.devsmart.staple.ccodegen;


import com.devsmart.staple.CompilerContext;
import com.devsmart.staple.StapleBaseVisitor;
import com.devsmart.staple.StapleParser;
import com.devsmart.staple.ccodegen.instruction.*;
import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.symbols.LocalVariable;
import com.devsmart.staple.type.*;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class CCodeGen extends StapleBaseVisitor<Void> {

    public static final STGroupFile codegentemplate;

    static {
        URL codeOutputStringTemplate = CCodeGen.class.getResource("C.stg");
        //URL codeOutputStringTemplate = ClassLoader.getSystemResource("C.stg");
        codegentemplate = new STGroupFile(codeOutputStringTemplate, "UTF-8", '<', '>');
    }


    private final CompilerContext compilerContext;
    private ClassType currentClassType;
    private OutputStreamWriter headerOutput;
    private OutputStreamWriter codeOutput;
    private CodeBlock code;

    public CCodeGen(CompilerContext ctx, OutputStreamWriter headerOutput, OutputStreamWriter codeOutput) {
        compilerContext = ctx;
        this.headerOutput = headerOutput;
        this.codeOutput = codeOutput;
    }

    private void pushCodeBlock() {
        CodeBlock newBlock = new CodeBlock();
        newBlock.parent = code;
        code = newBlock;
    }

    private void popCodeBlock() {
        code = code.parent;
    }

    private ExpressionTransform createTransform() {
        return new ExpressionTransform(compilerContext);
    }

    private String transform(ParserRuleContext ctx) {
        ExpressionTransform tx = createTransform();
        tx.visit(ctx);
        return tx.render();
    }

    @Override
    public Void visitCompileUnit(@NotNull StapleParser.CompileUnitContext ctx) {

        StapleParser.ClassDeclContext mainClass = ctx.classDecl();
        ClassHeaderGen mainClassHeaderGen = new ClassHeaderGen(compilerContext, headerOutput);
        mainClassHeaderGen.visit(mainClass);

        ClassType mainClassType = (ClassType) compilerContext.symbols.get(mainClass);
        try {
            headerOutput.write(String.format("extern %sClass %sClassObj;\n", CCodeGen.fullClassName(mainClassType), mainClassType.name));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClassHeaderGen internalClassHeaderGen = new ClassHeaderGen(compilerContext, codeOutput);
        for(StapleParser.ClassDeclContext intClass : mainClass.classDecl()){
            internalClassHeaderGen.visit(intClass);
        }

        visitChildren(ctx);

        return null;
    }

    @Override
    public Void visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {

        ClassType lastClass = currentClassType;
        currentClassType = (ClassType)compilerContext.symbols.get(ctx);

        ST instanceTmp = codegentemplate.getInstanceOf("classTypeInstance");
        instanceTmp.add("name", fullClassName(currentClassType));
        instanceTmp.add("parent", currentClassType.parent != null ? fullClassName(currentClassType.parent) : "NULL");
        instanceTmp.add("functions", functionOverrides(currentClassType));

        try {
            codeOutput.write(instanceTmp.render());
        } catch (IOException e) {
            e.printStackTrace();
        }

        visitChildren(ctx);
        currentClassType = lastClass;

        return null;
    }

    private Collection<String> functionOverrides(ClassType classType) {
        LinkedList<String> renderFunctions = new LinkedList<String>();
        LinkedList<ClassType> superClasses = new LinkedList<ClassType>();
        superClasses.add(classType);
        functionOverride(renderFunctions, superClasses, new HashSet<String>());
        return renderFunctions;
    }

    private void functionOverride(Collection<String> renderFunctions, LinkedList<ClassType> superClasses,
                                  Set<String> doneSet) {

        final ClassType firstClass = superClasses.peek();
        if(firstClass.parent != null){
            superClasses.push(firstClass.parent);
            functionOverride(renderFunctions, superClasses, doneSet);
        }

        for(FunctionType function : firstClass.functions) {
            ClassType implementingClass = null;
            for(ClassType superClass : superClasses) {
                if(!doneSet.contains(function.name) && superClass.hasFunction(function.name)){
                    implementingClass = superClass;
                }
            }
            if(implementingClass != null) {
                renderFunctions.add(fullClassName(implementingClass) + "_" + function.name);
                doneSet.add(function.name);
            }
        }

        superClasses.pop();
    }

    @Override
    public Void visitClassFunctionDecl(@NotNull StapleParser.ClassFunctionDeclContext ctx) {
        FunctionType functionSymbol = (FunctionType) compilerContext.symbols.get(ctx);

        pushCodeBlock();

        visitChildren(ctx);

        ST functionBodyTmp = codegentemplate.getInstanceOf("functionBody");
        functionBodyTmp.add("return", renderType(functionSymbol.returnType));
        functionBodyTmp.add("name", fullClassName(currentClassType) + "_" + functionSymbol.name);
        functionBodyTmp.add("args", renderFunctionArgs(functionSymbol));
        functionBodyTmp.add("code", code.render());

        popCodeBlock();

        try {
            codeOutput.write(functionBodyTmp.render());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visitBlock(@NotNull StapleParser.BlockContext ctx) {

        pushCodeBlock();

        for(StapleParser.LocalVariableDeclarationStatementContext localVar : ctx.localVariableDeclarationStatement()){
            visit(localVar);
        }

        for(StapleParser.StatementContext statement : ctx.statement()){
            visit(statement);
        }

        CodeBlock block = code;
        popCodeBlock();

        code.code.add(block);

        return null;
    }

    @Override
    public Void visitLocalVariableDeclaration(@NotNull StapleParser.LocalVariableDeclarationContext ctx) {

        LocalVariable localVariableSymbol = (LocalVariable) compilerContext.symbols.get(ctx);

        code.code.add(new LocalVarableInst(localVariableSymbol));

        return null;
    }

    @Override
    public Void visitExpression(@NotNull StapleParser.ExpressionContext ctx) {

        if(ctx.assignmentOperator() != null){

            StapleParser.ConditionalExpressionContext lvalueCtx = ctx.conditionalExpression();

            String lvalueTransfor = transform(lvalueCtx);;
            Type lvalueType = (Type) compilerContext.symbols.get(lvalueCtx);

            StapleParser.ExpressionContext rvalueCtx = ctx.expression();
            String rvalueTransform = transform(rvalueCtx);;

            if(lvalueType instanceof PointerType && ((PointerType) lvalueType).baseType instanceof ClassType){

                code.code.add(new ObjectAssignInst(lvalueTransfor, rvalueTransform));
            }
        } else {
            visitChildren(ctx);
        }

        return null;
    }

    @Override
    public Void visitUnaryExpression(@NotNull StapleParser.UnaryExpressionContext ctx) {

        String retval = transform(ctx);
        code.code.add(new CTextInst(retval));

        return null;
    }

    @Override
    public Void visitStatement(@NotNull StapleParser.StatementContext ctx) {

        final String first = ctx.getChild(0).getText();
        if("if".equals(first)){
            IfInst ifinst = new IfInst();
            ifinst.condition = transform(ctx.parExpression());

            visit(ctx.statement(0));
            ifinst.thenInst = code.code.remove();

            if(ctx.statement(1) != null){
                visit(ctx.statement(1));
                ifinst.elseInst = code.code.remove();
            }
            code.code.add(ifinst);

        } else if(ctx.statementExpression() != null){
            String str = transform(ctx.statementExpression());
            code.code.add(new CTextInst(str));
        } else {
            visitChildren(ctx);
        }

        return null;
    }

    public static String renderType(Type type) {
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

            String[] argsStr = renderFunctionArgs(functionType);
            functionTypeTmp.add("args", argsStr);
            retval = functionTypeTmp.render();
        } else if(type instanceof PointerType){
            final PointerType pointerType = (PointerType) type;
            retval = renderType(pointerType.baseType) + "*";
        } else if(type instanceof ClassType){
            retval = CCodeGen.fullClassName((ClassType) type);
        } else {
            retval = type.toString();
        }

        return retval;
    }

    public static String[] renderFunctionArgs(final FunctionType functionType) {
        Argument[] args = null;
        if(functionType.isMember) {
            args = new Argument[functionType.arguments.length + 1];
            args[0] = new Argument(PointerType.VoidPtr, "self");
            System.arraycopy(functionType.arguments, 0, args, 1, functionType.arguments.length);
        } else {
            args = functionType.arguments;
        }

        String[] argsStr = new String[args.length];
        for(int i=0;i<argsStr.length;i++){
            argsStr[i] = renderType(args[i].type) + " " + args[i].name;
        }
        return argsStr;
    }

    public static String fullClassName(ClassType classType) {
        String retval = "";
        String[] paths = classType.namespace.getPaths();
        if(paths.length > 0) {
            retval = Joiner.on("_").join(classType.namespace.getPaths());
            retval += "_" + classType.name;
        } else {
            retval = classType.name;
        }
        return retval;
    }


}
