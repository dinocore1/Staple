package com.devsmart.staple.AST;


import com.devsmart.staple.type.VoidType;

public class Assignment extends ASTNode {

    public final ASTNode left;
    public final ASTNode right;

    public Assignment(ASTNode left, ASTNode right) {
        type = VoidType.VOID;
        this.left = left;
        this.right = right;
    }
}
