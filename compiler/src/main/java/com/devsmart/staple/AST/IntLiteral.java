package com.devsmart.staple.AST;


import com.devsmart.staple.type.IntType;

public class IntLiteral extends ASTNode {

    public final int value;

    public IntLiteral(String intstr) {
        this.type = IntType.INT32;
        value = Integer.parseInt(intstr);
    }
}
