package com.devsmart.staple.ir;


import java.util.ArrayList;
import java.util.Arrays;

public class GetPointerInst extends SSAInst {

    public final Var result;
    public final Operand base;
    public final Operand[] indexes;


    public GetPointerInst(Var result, Operand base, Operand[] indexes) {
        this.result = result;
        this.base = base;
        this.indexes = indexes;
    }

    @Override
    public String toString() {
        return String.format("%s = %s->%s", result.toString(), base.toString(), Arrays.toString(indexes));
    }
}
