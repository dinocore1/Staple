package com.devsmart.staple.ir;

import com.devsmart.staple.AST.Relation;

public class Compare extends AssignmentInst {

    public final Relation.Operator operator;
    public final Operand left;
    public final Operand right;

    public Compare(Relation.Operator operator, Var result, Operand left, Operand right) {
        this.operator = operator;
        this.result = result;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("%s = cmp %s %s %s", result, left, operator, right);
    }
}
