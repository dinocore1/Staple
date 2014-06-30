package com.devsmart.staple.ir;

import com.devsmart.staple.type.Type;

public class StackAllocInst extends AssignmentInst {

    private final Type type;

    public StackAllocInst(Var var, Type type) {
        this.result = var;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s = alloc %s", result.name, type.toString());
    }
}
