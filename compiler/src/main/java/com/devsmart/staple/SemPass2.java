package com.devsmart.staple;


import com.devsmart.staple.AST.*;
import com.devsmart.staple.symbol.*;
import com.devsmart.staple.type.BoolType;
import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.FloatType;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.IntType;
import com.devsmart.staple.type.Type;
import com.devsmart.staple.type.VoidType;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemPass2 extends StapleBaseVisitor<ASTNode> {

    private final CompilerContext mCompilerContext;
    private Scope currentScope;
    private ClassType currentClass;

    public SemPass2(CompilerContext ctx) {
        mCompilerContext = ctx;
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
    public ASTNode visitClassDecl(@NotNull StapleParser.ClassDeclContext ctx) {
        ClassDecl classDecl = (ClassDecl) mCompilerContext.astTreeProperties.get(ctx);
        currentClass = (ClassType) classDecl.type;
        currentScope = classDecl.scope;

        for(StapleParser.MemberVarDeclContext m : ctx.m){
            classDecl.members.add((ClassMember) visit(m));
        }

        for(StapleParser.MemberFunctionDeclContext f : ctx.f) {
            classDecl.functions.add((ClassFunction) visit(f));
        }


        return classDecl;
    }

    @Override
    public ASTNode visitMemberVarDecl(@NotNull StapleParser.MemberVarDeclContext ctx) {
        ClassMember retval = new ClassMember();
        retval.scope = currentScope;

        ASTNode typeNode = visit(ctx.t);
        final String varName = ctx.n.getText();

        if(currentClass.members.containsKey(varName)){
            mCompilerContext.errorStream.error(String.format("redefinition of class member '%s'", varName), ctx);
        }
        currentClass.members.put(varName, typeNode.type);

        retval.type = typeNode.type;
        Symbol symbol = new MemberSymbol(varName, typeNode.type, currentClass);
        currentScope.define(symbol);

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitMemberFunctionDecl(@NotNull StapleParser.MemberFunctionDeclContext ctx) {
        ClassFunction retval = new ClassFunction();

        retval.returnType = visit(ctx.r).type;
        retval.name = ctx.n.getText();
        for(ParserRuleContext arg : ctx.args){
            retval.args.add((VarDecl) visit(arg));
        }

        MemberFunctionSymbol symbol = new MemberFunctionSymbol(retval.name,
                new FunctionType(retval.returnType, retval.getArgTypes()));

        currentScope.define(symbol);

        pushScope();
        //define the args in the new scope
        for(VarDecl arg : retval.args){
            currentScope.define(arg.symbol);
        }

        retval.block = (Block) visit(ctx.block());
        popScope();

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitArg(@NotNull StapleParser.ArgContext ctx) {
        ASTNode type = visit(ctx.t);
        Symbol symbol = new Symbol(ctx.n.getText(), type.type);
        VarDecl retval = new VarDecl(symbol);

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitObjectAccess(@NotNull StapleParser.ObjectAccessContext ctx) {
        ASTNode retval = null;
        ClassType baseType = null;
        ClassSymbol baseClassSymbol = null;

        ASTNode left = visit(ctx.l);
        if(left == null){
            return null;
        }

        if(!(left.type instanceof ClassType)){
            mCompilerContext.errorStream.error("Not a object type", ctx.l);
            return null;
        } else {
            baseType = (ClassType)left.type;
            Symbol symbol = currentScope.get(baseType.name);
            if(!(symbol instanceof ClassSymbol)){
                mCompilerContext.errorStream.error("wft", ctx);
            } else {
                baseClassSymbol = (ClassSymbol)symbol;
            }

        }

        if(ctx.r instanceof StapleParser.SymbolReferenceContext) {
            String memberName = ctx.r.getText();

            Type t = baseClassSymbol.getType().members.get(memberName);
            if(t == null){
                mCompilerContext.errorStream.error(String.format("Undefined member '%s' in class '%s'", memberName, baseClassSymbol.name), ctx.r);
            }
            MemberAccess memberAccess = new MemberAccess(left, baseClassSymbol, memberName);
            retval = memberAccess;
        } else if(ctx.r instanceof StapleParser.FunctionCallContext) {
            StapleParser.FunctionCallContext fncCtx = (StapleParser.FunctionCallContext) ctx.r;
            String functionName = fncCtx.n.getText();


            Type m = baseClassSymbol.getType().getMemberFunction(functionName);
            if(m == null){
                mCompilerContext.errorStream.error(String.format("Undefined function '%s' in class '%s'", functionName, baseClassSymbol.name), ctx.r);
            }

            ArrayList<ASTNode> args = new ArrayList<ASTNode>(fncCtx.args.size());
            for(ParserRuleContext arg : fncCtx.args){
                args.add(visit(arg));
            }
            MemberFunctionCall memberFunctionCall = new MemberFunctionCall(left, baseClassSymbol, functionName, args);
            retval = memberFunctionCall;
        } else {
            mCompilerContext.errorStream.error("right side of object access is not a member or function call", ctx.r);
        }

        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitBlock(@NotNull StapleParser.BlockContext ctx) {
        pushScope();
        Block block = new Block();
        block.scope = currentScope;
        for(ParserRuleContext stmt : ctx.stmt()){
            block.statements.add(visit(stmt));
        }
        popScope();

        mCompilerContext.astTreeProperties.put(ctx, block);
        return block;
    }

    @Override
    public ASTNode visitLocalVarDecl(@NotNull StapleParser.LocalVarDeclContext ctx) {
        ASTNode type = visitType(ctx.t);
        String name = ctx.id.getText();

        Symbol var = new Symbol(name, type.type);
        currentScope.define(var);

        VarDecl retval = new VarDecl(var);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitAssign(@NotNull StapleParser.AssignContext ctx) {

        ASTNode left = visit(ctx.l);
        ASTNode right = visit(ctx.r);

       if(!right.type.isAssignableTo(left.type)) {
           String errStr = String.format("'%s' is not assignable to '%s'",
                   right.type.name, left.type.name);
           mCompilerContext.errorStream.error(errStr, ctx.l.start);
       }

        Assignment retval = new Assignment(left, right);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        MathOp retval = new MathOp(op, visit(ctx.l), visit(ctx.r));
        retval.type = retval.left.type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitLogicOp(@NotNull StapleParser.LogicOpContext ctx) {

        ASTNode left = visit(ctx.l);
        if(left.type != BoolType.BOOL){
            mCompilerContext.errorStream.error(String.format("'%s' must be a boolean operator", ctx.l.getText()), ctx.l);
        }

        ASTNode right = visit(ctx.r);
        if(right.type != BoolType.BOOL){
            mCompilerContext.errorStream.error(String.format("'%s' must be a boolean operator", ctx.r.getText()), ctx.r);
        }

        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        MathOp retval = new MathOp(op, left, right);
        retval.type = BoolType.BOOL;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitRelation(@NotNull StapleParser.RelationContext ctx) {
        Relation.Operator op = Relation.Operator.getOperation(ctx.op.getText());
        Relation retval = new Relation(op, visit(ctx.l), visit(ctx.r));
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitSymbolReference(@NotNull StapleParser.SymbolReferenceContext ctx) {
        Symbol symbol = currentScope.get(ctx.v.getText());
        if(symbol == null){
            mCompilerContext.errorStream.error(String.format("undefined symbol '%s'", ctx.v.getText()), ctx.v);
            return null;
        }
        SymbolRef retval = new SymbolRef(symbol);
        mCompilerContext.astTreeProperties.put(ctx, retval);

        return retval;
    }

    @Override
    public ASTNode visitFunctionCall(@NotNull StapleParser.FunctionCallContext ctx) {
        final String name = ctx.n.getText();
        FunctionSymbol functionSymbol = null;
        {
            Symbol symbol = currentScope.get(name);
            if (symbol == null) {
                mCompilerContext.errorStream.error(String.format("undefined function '%s'", name), ctx.n);
                return null;
            }

            if (!(symbol instanceof FunctionSymbol)) {
                mCompilerContext.errorStream.error(String.format("'%s' is not a function type", name), ctx.n);
                return null;
            }
            functionSymbol = (FunctionSymbol) symbol;
        }

        FunctionCall retval = new FunctionCall(functionSymbol);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        FunctionType functionType = (FunctionType) functionSymbol.type;


        int argCount = 0;
        for(StapleParser.ExprContext argctx : ctx.args){
            final Type expectedArg = functionType.args[argCount];
            final ASTNode argnode = visit(argctx);

            if(argnode != null){
                if(!argnode.type.equals(expectedArg) || !argnode.type.isAssignableTo(expectedArg)){
                    mCompilerContext.errorStream.error(String.format("argument num %d is not assignable to type '%s", argCount, expectedArg.name), argctx);
                } else {
                    retval.args.add(argnode);
                }
            }
            argCount++;
        }


        return retval;
    }

    @Override
    public ASTNode visitReturnStmt(@NotNull StapleParser.ReturnStmtContext ctx) {
        ASTNode expr = visit(ctx.e);
        return new Return(expr);
    }

    @Override
    public ASTNode visitIfStmt(@NotNull StapleParser.IfStmtContext ctx) {
        ASTNode condition = visit(ctx.c);

        if(condition.type != BoolType.BOOL){
            mCompilerContext.errorStream.error("if condition is not a bool type", ctx.c);
        }

        ASTNode positiveBlock = visit(ctx.t);
        ASTNode negitiveBlock = null;
        if(ctx.e != null){
            negitiveBlock = visit(ctx.e);
        }

        IfStatement retval = new IfStatement(condition, positiveBlock, negitiveBlock);
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitForStmt(@NotNull StapleParser.ForStmtContext ctx) {

        ASTNode init = null;
        if(ctx.i != null) {
            init = visit(ctx.i);
        }

        ASTNode condition = null;
        condition = visit(ctx.c);

        ASTNode increment = null;
        if(ctx.n != null){
            increment = visit(ctx.n);
        }

        ASTNode loopBlock = visit(ctx.block());

        ForStatement retval = new ForStatement(init, condition, increment, loopBlock);
        mCompilerContext.astTreeProperties.put(ctx, retval);

        return retval;
    }

    @Override
    public ASTNode visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        IntLiteral retval = new IntLiteral(ctx.v.getText());
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }

    @Override
    public ASTNode visitArrayAccess(@NotNull StapleParser.ArrayAccessContext ctx) {
        Symbol symbol = currentScope.get(ctx.a.getText());
        ArrayList<ASTNode> dim = new ArrayList<ASTNode>(ctx.dim.size());
        for(ParserRuleContext d : ctx.dim) {
            dim.add(visit(d));
        }
        ArrayAccess arrayAccess = new ArrayAccess(symbol, dim);
        mCompilerContext.astTreeProperties.put(ctx, arrayAccess);
        return arrayAccess;
    }

    public static final Pattern INT_REGEX = Pattern.compile("int([0-9]*)");
    public static final Pattern FLOAT_REGEX = Pattern.compile("float([0-9]*)");

    @Override
    public ASTNode visitType(@NotNull StapleParser.TypeContext ctx) {
        final String text = ctx.getText();
        Type type = null;

        Matcher m = null;
        if((m = INT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new IntType(Integer.parseInt(m.group(1)));
            } else {
                type = IntType.INT32;
            }
        } else if((m = FLOAT_REGEX.matcher(text)).find()){
            if(m.groupCount() == 2){
                type = new FloatType(Integer.parseInt(m.group(1)));
            } else {
                type = FloatType.FLOAT32;
            }
        } else if("bool".equals(text)) {
            type = BoolType.BOOL;
        } else if("void".equals(text)) {
            type = VoidType.VOID;
        } else {
            Symbol classSymbol = currentScope.get(text);
            if(classSymbol == null || !(classSymbol instanceof ClassSymbol)){
                mCompilerContext.errorStream.error(String.format("unknown class: '%s'", text), ctx.getStart());
            } else {
                type = classSymbol.type;
            }
        }

        ASTNode retval = new ASTNode();
        retval.type = type;
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
    }
}
