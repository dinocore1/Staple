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

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public class IRVisitor extends StapleBaseVisitor<Operand> {

    DirectedGraph<BasicBlock, DefaultEdge> cfg;
    HashMap<Label, BasicBlock> labelTable = new HashMap<Label, BasicBlock>();

    private static class SymbolScope {
        private HashMap<Symbol, Operand> mSymbolTable = new HashMap<Symbol, Operand>();
        public SymbolScope mParent;

        public void put(Symbol symbol, Operand op) {
            mSymbolTable.put(symbol, op);
        }

        public Operand get(Symbol symbol) {
            Operand retval = null;
            for(SymbolScope s = this; s != null; s = s.mParent) {
                retval = s.mSymbolTable.get(symbol);
                if(retval != null){
                    break;
                }
            }
            return  retval;
        }
    }

    private final CompilerContext mCompilerContext;
    private int mTempCount;
    private SymbolScope mCurrentSymbolScope = new SymbolScope();
    private BasicBlock mCurrentBasicBlock;
    private final LinkedList<InsertPhi> mInsertPhi = new LinkedList<InsertPhi>();

    public void pushScope() {
        SymbolScope newScope = new SymbolScope();
        newScope.mParent = mCurrentSymbolScope;
        mCurrentSymbolScope = newScope;

    }

    public void popScope() {
        mCurrentSymbolScope = mCurrentSymbolScope.mParent;
    }

    private BasicBlock createBasicBlock(Label label) {
        BasicBlock retval = new BasicBlock(label);
        cfg.addVertex(retval);
        return retval;
    }

    public IRVisitor(CompilerContext ctx) {
        mCompilerContext = ctx;
    }

    private Operand rvalue(ParserRuleContext ctx) {
        Operand retval = null;
        ASTNode node = mCompilerContext.astTreeProperties.get(ctx);
        if(node instanceof SymbolRef) {
            SymbolRef symbolRef = (SymbolRef)node;
            retval = mCurrentSymbolScope.get(symbolRef.symbol);
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


    private void emit(SSAInst inst) {
        mCurrentBasicBlock.code.add(inst);
    }

    @Override
    public Operand visitMemberFunctionDecl(@NotNull StapleParser.MemberFunctionDeclContext ctx) {
        ClassFunction memberSymbol = (ClassFunction) mCompilerContext.astTreeProperties.get(ctx);

        labelTable.clear();
        mInsertPhi.clear();
        cfg = new DefaultDirectedGraph<BasicBlock, DefaultEdge>(DefaultEdge.class);
        Label label = new Label();
        BasicBlock rootBlock = createBasicBlock(label);
        mCurrentBasicBlock = rootBlock;
        pushScope();
        emit(new FunctionDeclaration(memberSymbol));

        for(VarDecl arg : memberSymbol.args){
            Var var = new Var(arg.symbol.type, arg.symbol.name);
            mCurrentSymbolScope.put(arg.symbol, var);
            mInsertPhi.add(new InsertPhi(cfg, rootBlock, arg.symbol));
        }

        visitChildren(ctx);
        popScope();

        for(InsertPhi phi : mInsertPhi){
            phi.run();
        }
        mInsertPhi.clear();

        CodeEmitter emitter = new CodeEmitter(cfg, rootBlock, mCompilerContext.code);
        emitter.doIt();

        return null;
    }

    @Override
    public Operand visitAssign(@NotNull StapleParser.AssignContext ctx) {

        ASTNode left = mCompilerContext.astTreeProperties.get(ctx.l);
        Operand right = rvalue(ctx.r);

        if(left instanceof SymbolRef){
            SymbolRef symbolRef = (SymbolRef)left;
            right.tag = symbolRef.symbol;
            mCurrentSymbolScope.put(symbolRef.symbol, right);
        } else if(left instanceof ArrayAccess){
            ArrayAccess arrayAccessAst = (ArrayAccess)left;
            StapleParser.ArrayAccessContext arrayAccess = (StapleParser.ArrayAccessContext)ctx.l;
            Operand[] indexes = new Operand[arrayAccess.dim.size()];
            for(int i=0;i<indexes.length;i++){
                indexes[i] = rvalue(arrayAccess.dim.get(i));
            }
            Var ptr = new Var(new PointerType(arrayAccessAst.var.type));
            Operand base = mCurrentSymbolScope.get(arrayAccessAst.var);
            emit(new GetPointerInst(ptr, base, indexes));
            emit(new StoreInst(ptr, right));
        }

        return null;
    }

    @Override
    public Operand visitLocalVarDecl(@NotNull StapleParser.LocalVarDeclContext ctx) {
        VarDecl varDecl = (VarDecl) mCompilerContext.astTreeProperties.get(ctx);

        Var var = new Var(new PointerType(varDecl.type), varDecl.symbol.name);
        var.tag = varDecl.symbol;

        emit(new StackAllocInst(var, varDecl.type));
        mCurrentSymbolScope.put(varDecl.symbol, var);
        return null;
    }

    @Override
    public Operand visitBlock(@NotNull StapleParser.BlockContext ctx) {
        pushScope();
        visitChildren(ctx);
        popScope();
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

        Var result = new Var(functionType.returnType);
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
        Var result = new Var(relation.type);

        Compare compare = new Compare(relation.operator, result, left, right);

        emit(compare);

        return result;
    }

    @Override
    public Operand visitIfStmt(@NotNull StapleParser.IfStmtContext ctx) {

        BasicBlock finishBlock = createBasicBlock(new Label());

        Operand condition = rvalue(ctx.c);
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        Branch branch = new Branch(condition, trueLabel, falseLabel);
        emit(branch);

        BasicBlock thisBB = mCurrentBasicBlock;

        pushScope();
        mCurrentBasicBlock = createBasicBlock(trueLabel);
        cfg.addEdge(thisBB, mCurrentBasicBlock);
        cfg.addEdge(mCurrentBasicBlock, finishBlock);
        emit(trueLabel);
        visit(ctx.t);
        popScope();

        pushScope();
        mCurrentBasicBlock = createBasicBlock(falseLabel);
        cfg.addEdge(thisBB, mCurrentBasicBlock);
        cfg.addEdge(mCurrentBasicBlock, finishBlock);
        emit(falseLabel);
        visit(ctx.e);
        popScope();

        mCurrentBasicBlock = finishBlock;


        return null;
    }

    @Override
    public Operand visitForStmt(@NotNull StapleParser.ForStmtContext ctx) {

        Label trueLabel = new Label();
        Label loop = new Label();
        BasicBlock loopBlock = createBasicBlock(loop);
        cfg.addVertex(loopBlock);
        cfg.addEdge(mCurrentBasicBlock, loopBlock);
        cfg.addEdge(loopBlock, loopBlock);

        Label falseLabel = new Label();
        BasicBlock finishBlock = createBasicBlock(falseLabel);
        cfg.addVertex(finishBlock);
        cfg.addEdge(loopBlock, finishBlock);


        //initial condition
        if(ctx.i != null){
            visit(ctx.i);
        }

        pushScope();
        mCurrentBasicBlock = loopBlock;
        emit(loop);

        Branch branch = new Branch(rvalue(ctx.c), trueLabel, falseLabel);
        emit(branch);
        emit(trueLabel);
        visit(ctx.block());
        if(ctx.n != null){
            //increment expression
            visit(ctx.n);
        }
        emit(new JumpTo(loop));
        popScope();

        mCurrentBasicBlock = finishBlock;
        emit(falseLabel);

        return null;
    }

    @Override
    public Operand visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp mathOp = (MathOp) mCompilerContext.astTreeProperties.get(ctx);

        Operand left = visit(ctx.l);
        Operand right = visit(ctx.r);
        Var result = new Var(mathOp.type);

        MathOpInst inst = new MathOpInst(mathOp.operation, result, left, right);
        emit(inst);

        return result;
    }

    @Override
    public Operand visitLogicOp(@NotNull StapleParser.LogicOpContext ctx) {
        MathOp mathOp = (MathOp) mCompilerContext.astTreeProperties.get(ctx);

        Operand left = visit(ctx.l);
        Operand right = visit(ctx.r);
        Var result = new Var(mathOp.type);

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
        Operand retval = mCurrentSymbolScope.get(symbolRef.symbol);
        return retval;
    }

    @Override
    public Operand visitArrayAccess(@NotNull StapleParser.ArrayAccessContext ctx) {
        ArrayAccess arrayAccess = (ArrayAccess) mCompilerContext.astTreeProperties.get(ctx);

        Operand[] indexes = new Operand[arrayAccess.indexes.size()];
        for(int i=0;i<indexes.length;i++){
            indexes[i] = visit(ctx.dim.get(i));
        }
        Var ptr = new Var(new PointerType(arrayAccess.var.type));
        Operand base = mCurrentSymbolScope.get(arrayAccess.var);
        emit(new GetPointerInst(ptr, base, indexes));
        Var retval = new Var(arrayAccess.var.type);
        emit(new LoadInst(retval, ptr));

        return retval;
    }
}
