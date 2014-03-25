package com.devsmart.staple.ir;


public class ReturnInst extends SSAInst {

    public final Operand value;

    public ReturnInst(Operand retval) {
        this.value = retval;
    }

    @Override
    public String toString() {
        return String.format("return %s", value.toString());
    }
}
