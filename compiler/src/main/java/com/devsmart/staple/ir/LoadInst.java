package com.devsmart.staple.ir;


public class LoadInst extends SSAInst {
    private final Var result;
    private final Var ptr;

    public LoadInst(Var retval, Var ptr) {
        this.result = retval;
        this.ptr = ptr;
    }

    @Override
    public String toString() {
        return String.format("%s = load %s", result, ptr);
    }
}
