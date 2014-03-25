package com.devsmart.staple;

import com.devsmart.staple.AST.*;
import com.devsmart.staple.ir.*;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;


public class IRVisitor extends StapleBaseVisitor<Operand> {
    private final CompilerContext mCompilerContext;
    private int mTempCount;

    public IRVisitor(CompilerContext ctx) {
        mCompilerContext = ctx;
    }

    private Operand lvalue(ParserRuleContext ctx) {
        Operand retval = null;
        ASTNode node = mCompilerContext.astTreeProperties.get(ctx);
        if(node instanceof SymbolRef){
            SymbolRef symbolRef = (SymbolRef)node;
            retval = new Var(symbolRef.type, symbolRef.symbol.name);
        }

        return retval;
    }

    private Operand rvalue(ParserRuleContext ctx) {
        Operand retval = null;
        ASTNode node = mCompilerContext.astTreeProperties.get(ctx);
        if(node instanceof SymbolRef) {
            SymbolRef symbolRef = (SymbolRef)node;
            retval = new Var(symbolRef.type, symbolRef.symbol.name);
        } else if(node instanceof IntLiteral) {
            IntLiteral intliteral = (IntLiteral)node;
            retval = new IntLiteralOperand(intliteral.value);
        } else if(node instanceof Assignment) {
            StapleParser.AssignContext assignContext = (StapleParser.AssignContext) ctx;
            Operand o1 = rvalue(assignContext.r);

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
    public Operand visitAssign(@NotNull StapleParser.AssignContext ctx) {

        Operand right = rvalue(ctx.r);
        Operand left = lvalue(ctx.l);


        Assignment assignment = (Assignment)mCompilerContext.astTreeProperties.get(ctx);
        assignment.left
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
