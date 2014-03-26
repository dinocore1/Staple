package com.devsmart.staple.ir;

import com.devsmart.staple.type.Type;

public class StackAllocInst extends SSAInst {

    private final Var var;
    private final Type type;

    public StackAllocInst(Var var, Type type) {
        this.var = var;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s = alloc %s", var.toString(), type.toString());
    }
}
