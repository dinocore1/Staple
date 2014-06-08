package com.devsmart.staple.ir;


public class Branch extends SSAInst {

    public final Operand condition;
    public final Label trueLabel;
    public final Label falseLabel;

    public Branch(Operand condition, Label trueLabel, Label falseLabel) {
        this.condition = condition;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    @Override
    public String toString() {
        return String.format("br %s, label %s, label %s", condition, trueLabel, falseLabel);
    }
}
