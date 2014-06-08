package com.devsmart.staple.AST;


public class Return extends ASTNode {

    public Return(ASTNode expr) {
        this.type = expr.type;
    }
}
