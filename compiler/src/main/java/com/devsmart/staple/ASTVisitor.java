package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.IntLiteral;
import com.devsmart.staple.AST.MathOp;
import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends StapleBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        return new MathOp(op, visit(ctx.l), visit(ctx.r));
    }

    @Override
    public ASTNode visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        return new IntLiteral(ctx.v.getText());
    }
}
