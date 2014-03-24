package com.devsmart.staple.type;


public class FloatType extends Type {

    private final int precision;

    public FloatType(int precision) {
        super(String.format("float%d", precision));
        this.precision = precision;
    }
}
