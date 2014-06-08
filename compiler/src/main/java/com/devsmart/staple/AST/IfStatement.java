package com.devsmart.staple.AST;

public class IfStatement extends ASTNode {


    public final ASTNode condition;
    public final ASTNode positiveBlock;
    public final ASTNode negitiveBlock;

    public IfStatement(ASTNode condition, ASTNode positiveBlock, ASTNode negitiveBlock) {
        this.condition = condition;
        this.positiveBlock = positiveBlock;
        this.negitiveBlock = negitiveBlock;
    }
}
