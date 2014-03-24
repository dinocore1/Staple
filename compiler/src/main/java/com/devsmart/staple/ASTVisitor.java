package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.AST.IntLiteral;
import com.devsmart.staple.AST.MathOp;
import com.devsmart.staple.AST.Relation;
import com.devsmart.staple.AST.TypeNode;
import com.devsmart.staple.type.Type;

import org.antlr.v4.runtime.misc.NotNull;

public class ASTVisitor extends StapleBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitMathOp(@NotNull StapleParser.MathOpContext ctx) {
        MathOp.Operation op = MathOp.Operation.getOperation(ctx.op.getText());
        return new MathOp(op, visit(ctx.l), visit(ctx.r));
    }

    @Override
    public ASTNode visitRelation(@NotNull StapleParser.RelationContext ctx) {
        Relation.Operator op = Relation.Operator.getOperation(ctx.op.getText());
        return new Relation(op, visit(ctx.l), visit(ctx.r));
    }

    @Override
    public ASTNode visitIntLiteral(@NotNull StapleParser.IntLiteralContext ctx) {
        return new IntLiteral(ctx.v.getText());
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

        return new TypeNode(type);
    }
}
