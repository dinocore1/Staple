package com.devsmart.staple;

import com.devsmart.staple.AST.*;
import com.devsmart.staple.ir.*;
import com.devsmart.staple.symbol.MemberFunctionSymbol;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.FunctionType;
import com.devsmart.staple.type.PointerType;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.HashMap;


public class IRVisitor extends StapleBaseVisitor<Operand> {
    private final CompilerContext mCompilerContext;
    private int mTempCount;
    private HashMap<Symbol, Operand> mSymbolMap = new HashMap<Symbol, Operand>();

    public IRVisitor(CompilerContext ctx) {
        mCompilerContext = ctx;
    }

    private Operand rvalue(ParserRuleContext ctx) {
        Operand retval = null;
        ASTNode node = mCompilerContext.astTreeProperties.get(ctx);
        if(node instanceof SymbolRef) {
            SymbolRef symbolRef = (SymbolRef)node;
            retval = mSymbolMap.get(symbolRef.symbol);
        } else if(node instanceof IntLiteral) {
            IntLiteral intliteral = (IntLiteral)node;
            retval = new IntLiteralOperand(intliteral.value);
        } else if(node instanceof Assignment) {
            StapleParser.AssignContext assignContext = (StapleParser.AssignContext) ctx;
            Operand o1 = rvalue(assignContext.r);
        } else {
            retval = visit(ctx);
        }


        return retval;
    }

    private Var createTemporaty(Type type) {
        return new Var(type, String.format("t%d", ++mTempCount));
    }


    private void emit(SSAInst inst) {
        mCompilerContext.code.add(inst);
    }

    @Override
    public Operand visitMemberFunctionDecl(@NotNull StapleParser.MemberFunctionDeclContext ctx) {
        ClassFunction memberSymbol = (ClassFunction) mCompilerContext.astTreeProperties.get(ctx);

        emit(new FunctionDeclaration(memberSymbol));

        for(VarDecl arg : memberSymbol.args){
            mSymbolMap.put(arg.symbol, new Var(arg.symbol.type, arg.symbol.name));
        }

        visitChildren(ctx);

        return null;
    }

    @Override
    public Operand visitAssign(@NotNull StapleParser.AssignContext ctx) {

        ASTNode left = mCompilerContext.astTreeProperties.get(ctx.l);
        Operand right = rvalue(ctx.r);

        if(left instanceof SymbolRef){
            SymbolRef symbolRef = (SymbolRef)left;
            mSymbolMap.put(symbolRef.symbol, right);
        } else if(left instanceof ArrayAccess){
            ArrayAccess arrayAccessAst = (ArrayAccess)left;
            StapleParser.ArrayAccessContext arrayAccess = (StapleParser.ArrayAccessContext)ctx.l;
            Operand[] indexes = new Operand[arrayAccess.dim.size()];
            for(int i=0;i<indexes.length;i++){
                indexes[i] = rvalue(arrayAccess.dim.get(i));
            }
            Var ptr = createTemporaty(new PointerType(arrayAccessAst.var.type));
            Operand base = mSymbolMap.get(arrayAccessAst.var);
            emit(new GetPointerInst(ptr, base, indexes));
            emit(new StoreInst(ptr, right));
        }

        return null;
    }

    @Override
    public Operand visitLocalVarDecl(@NotNull StapleParser.LocalVarDeclContext ctx) {
        VarDecl varDecl = (VarDecl) mCompilerContext.astTreeProperties.get(ctx);

        Var var = new Var(new PointerType(varDecl.type), varDecl.symbol.name);

        emit(new StackAllocInst(var, varDecl.type));
        mSymbolMap.put(varDecl.symbol, var);
        return null;
    }

    @Override
    public Operand visitBlock(@NotNull StapleParser.BlockContext ctx) {
        mTempCount = 0;
        visitChildren(ctx);
        return null;
    }

    @Override
    public Operand visitFunctionCall(@NotNull StapleParser.FunctionCallContext ctx) {
        FunctionCall functionCall = (FunctionCall) mCompilerContext.astTreeProperties.get(ctx);
        FunctionType functionType = (FunctionType) functionCall.functionSymbol.type;

        ArrayList<Operand> argOperands = new ArrayList<Operand>();
        for(StapleParser.ExprContext argnode : ctx.args){
            Operand argoperand = rvalue(argnode);
            argOperands.add(argoperand);
        }

        Var result = createTemporaty(functionType.returnType);
        FunctionCallInst inst = new FunctionCallInst(functionCall.functionSymbol, result, argOperands);
        emit(inst);

        return result;
    }

    @Override
    public Operand visitReturnStmt(@NotNull StapleParser.ReturnStmtContext ctx) {
        Operand retval = visit(ctx.e);
        ReturnInst returnInst = new ReturnInst(retval);
        emit(returnInst);
        return null;
    }

    @Override
    public Operand visitRelation(@NotNull StapleParser.RelationContext ctx) {
        Relation relation = (Relation)mCompilerContext.astTreeProperties.get(ctx);

        Operand left = rvalue(ctx.l);
        Operand right = rvalue(ctx.r);
        Var result = createTemporaty(relation.type);

        Compare compare = new Compare(relation.operator, result, left, right);

        emit(compare);

        return result;
    }

    @Override
    public Operand visitIfStmt(@NotNull StapleParser.IfStmtContext ctx) {
        Operand condition = rvalue(ctx.c);
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        Branch branch = new Branch(condition, trueLabel, falseLabel);
        emit(branch);
        emit(trueLabel);
        visit(ctx.t);
        emit(falseLabel);
        visit(ctx.e);

        return null;
    }

    @Override
    public Operand visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp mathOp = (MathOp) mCompilerContext.astTreeProperties.get(ctx);

        Operand left = visit(ctx.l);
        Operand right = visit(ctx.r);
        Var result = createTemporaty(mathOp.type);

        MathOpInst inst = new MathOpInst(mathOp.operation, result, left, right);
        emit(inst);

        return result;
    }

    @Override
    public Operand visitLogicOp(@NotNull StapleParser.LogicOpContext ctx) {
        MathOp mathOp = (MathOp) mCompilerContext.astTreeProperties.get(ctx);

        Operand left = visit(ctx.l);
        Operand right = visit(ctx.r);
        Var result = createTemporaty(mathOp.type);

        MathOpInst inst = new MathOpInst(mathOp.operation, result, left, right);
        emit(inst);

        return result;
    }

    @Override
    public Operand visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        IntLiteral intliteral = (IntLiteral) mCompilerContext.astTreeProperties.get(ctx);
        return new IntLiteralOperand(intliteral.value);
    }

    @Override
    public Operand visitSymbolReference(@NotNull StapleParser.SymbolReferenceContext ctx) {
        SymbolRef symbolRef = (SymbolRef)mCompilerContext.astTreeProperties.get(ctx);
        Operand retval = mSymbolMap.get(symbolRef.symbol);
        return retval;
    }

    @Override
    public Operand visitArrayAccess(@NotNull StapleParser.ArrayAccessContext ctx) {
        ArrayAccess arrayAccess = (ArrayAccess) mCompilerContext.astTreeProperties.get(ctx);

        Operand[] indexes = new Operand[arrayAccess.indexes.size()];
        for(int i=0;i<indexes.length;i++){
            indexes[i] = visit(ctx.dim.get(i));
        }
        Var ptr = createTemporaty(new PointerType(arrayAccess.var.type));
        Operand base = mSymbolMap.get(arrayAccess.var);
        emit(new GetPointerInst(ptr, base, indexes));
        Var retval = createTemporaty(arrayAccess.var.type);
        emit(new LoadInst(retval, ptr));

        return retval;
    }
}
