package com.devsmart.staple.ir;


import com.devsmart.staple.AST.MathOp;

public class MathOpInst extends AssignmentInst {

    MathOp.Operation operation;
    Operand left;
    Operand right;

    public MathOpInst(MathOp.Operation op, Var result, Operand left, Operand right) {
        this.operation = op;
        this.result = result;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s", result.name, left.toString(), operation.symbol, right.toString());
    }
}
