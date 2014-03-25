package com.devsmart.staple.ir;

import com.devsmart.staple.type.IntType;


public class IntLiteralOperand extends Operand {

    public final int value;

    public IntLiteralOperand(int value) {
        super(IntType.INT32);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%d", value);
    }
}
