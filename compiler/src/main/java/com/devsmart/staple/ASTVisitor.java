package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.Block;
import com.devsmart.staple.AST.IntLiteral;
import com.devsmart.staple.AST.MathOp;
import com.devsmart.staple.AST.Relation;
import com.devsmart.staple.AST.TypeNode;
import com.devsmart.staple.AST.VarDecl;
import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends StapleBaseVisitor<ASTNode> {

    private Scope currentScope;

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
        retval.mParserRuleContext = ctx;
        retval.scope = currentScope;
        for(StapleParser.StmtContext stmt : ctx.stmt()){
            retval.statements.add(visit(stmt));
        }
        popScope();
        return retval;
    }

    @Override
    public ASTNode visitLocalVarDecl(@NotNull StapleParser.LocalVarDeclContext ctx) {
        TypeNode type = visitType(ctx.t);
        String name = ctx.id.getText();

        Symbol var = new Symbol(name, type.type);
        currentScope.define(var);

        VarDecl retval = new VarDecl(var);
        retval.mParserRuleContext = ctx;
        return retval;
    }

    @Override
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        return new MathOp(op, visit(ctx.l), visit(ctx.r));
    }

    @Override
    public ASTNode visitRelation(@NotNull StapleParser.RelationContext ctx) {
        Relation.Operator op = Relation.Operator.getOperation(ctx.op.getText());
        Relation retval = new Relation(op, visit(ctx.l), visit(ctx.r));
        retval.mParserRuleContext = ctx;
        return retval;
    }

    @Override
    public ASTNode visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        IntLiteral retval = new IntLiteral(ctx.v.getText());
        retval.mParserRuleContext = ctx;
        return retval;
    }

    @Override
    public TypeNode visitType(@NotNull StapleParser.TypeContext ctx) {
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

        TypeNode retval = new TypeNode(type);
        retval.mParserRuleContext = ctx;
        return retval;
    }
}
