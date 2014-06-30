package com.devsmart.staple.ir;


import com.devsmart.staple.type.Type;

public class Var extends Operand {

    public String nameFormat = "t%s";
    public String name;

    //for nameless temps
    public Var(Type type) {
        super(type);
    }

    @Override
    public String toString() {
        return name;
    }
}
