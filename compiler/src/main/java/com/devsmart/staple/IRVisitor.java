package com.devsmart.staple;

import com.devsmart.staple.AST.*;
import com.devsmart.staple.ir.*;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashMap;


public class IRVisitor extends StapleBaseVisitor<Operand> {
    private final CompilerContext mCompilerContext;
    private int mTempCount;
    private HashMap<Symbol, Operand> mSymbolMap = new HashMap<Symbol, Operand>();

    public IRVisitor(CompilerContext ctx) {
        mCompilerContext = ctx;
    }

    private Symbol lvalue(ParserRuleContext ctx) {
        Symbol retval = null;
        ASTNode node = mCompilerContext.astTreeProperties.get(ctx);
        if(node instanceof SymbolRef){
            SymbolRef symbolRef = (SymbolRef)node;
            retval = symbolRef.symbol;
        }

        return retval;
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
    public Operand visitBlock(@NotNull StapleParser.BlockContext ctx) {
        mTempCount = 0;
        visitChildren(ctx);
        return null;
    }

    @Override
    public Operand visitReturnStmt(@NotNull StapleParser.ReturnStmtContext ctx) {
        Operand retval = visit(ctx.e);
        ReturnInst returnInst = new ReturnInst(retval);
        return null;
    }

    @Override
    public Operand visitAssign(@NotNull StapleParser.AssignContext ctx) {

        Symbol left = lvalue(ctx.l);
        Operand right = rvalue(ctx.r);
        mSymbolMap.put(left, right);
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
    public Operand visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        IntLiteral intliteral = (IntLiteral) mCompilerContext.astTreeProperties.get(ctx);
        return new IntLiteralOperand(intliteral.value);
    }
}
