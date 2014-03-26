package com.devsmart.staple;


import com.devsmart.staple.AST.*;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;

public class ASTVisitor extends StapleBaseVisitor<ASTNode> {


    private final CompilerContext mCompilerContext;
    private Scope currentScope;

    public ASTVisitor(CompilerContext ctx) {
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
    public ASTNode visitBlock(@NotNull StapleParser.BlockContext ctx) {
        pushScope();
        Block retval = new Block();
        mCompilerContext.astTreeProperties.put(ctx, retval);
        retval.scope = currentScope;
        for(StapleParser.StmtContext stmt : ctx.stmt()){
            retval.statements.add(visit(stmt));
        }
        popScope();
        return retval;
    }

    @Override
    public ASTNode visitAssign(@NotNull StapleParser.AssignContext ctx) {
        Assignment retval = new Assignment(visit(ctx.l), visit(ctx.r));
        mCompilerContext.astTreeProperties.put(ctx, retval);
        return retval;
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
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        MathOp retval = new MathOp(op, visit(ctx.l), visit(ctx.r));
        retval.type = retval.left.type;
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
        SymbolRef retval = new SymbolRef(symbol);
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
    public ASTNode visitType(@NotNull StapleParser.TypeContext ctx) {
        final String text = ctx.getText();
        Type type = null;
        if("int".equals(text)){
            type = new Type("int32");
        } else if("float".equals(text)){
            type = new Type("float32");
        }
        else {
            type = new Type(text);
        }

        ASTNode retval = new ASTNode();
        retval.type = type;
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
}
