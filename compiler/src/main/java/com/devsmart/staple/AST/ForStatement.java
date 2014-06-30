package com.devsmart.staple.AST;

public class ForStatement extends ASTNode {

    private final ASTNode init;
    private final ASTNode condition;
    private final ASTNode increment;
    private final ASTNode loopBlock;

    public ForStatement(ASTNode init, ASTNode condition, ASTNode increment, ASTNode loopBlock) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.loopBlock = loopBlock;
    }
}
