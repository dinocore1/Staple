package com.devsmart.staple.AST;


public class IntLiteral extends ASTNode {

    public final int value;

    public IntLiteral(String intstr) {
        value = Integer.parseInt(intstr);
    }
}
