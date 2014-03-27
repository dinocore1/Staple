package com.devsmart.staple.ir;


public class StoreInst extends SSAInst {

    public final Operand ptr;
    public final Operand value;

    public StoreInst(Operand ptr, Operand value) {
        this.ptr = ptr;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("store %s <= %s", ptr, value);
    }
}
