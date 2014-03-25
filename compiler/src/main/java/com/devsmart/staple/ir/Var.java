package com.devsmart.staple.ir;


import com.devsmart.staple.AST.MathOp;
import com.devsmart.staple.type.Type;

public class Var extends Operand {

    public final String name;

    public Var(Type type, String name) {
        super(type);
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
