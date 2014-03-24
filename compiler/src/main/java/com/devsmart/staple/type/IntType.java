package com.devsmart.staple.type;


public class IntType extends Type {

    public final int precision;

    public IntType(int precision) {
        super(String.format("int%d", precision));
        this.precision = precision;
    }
}
