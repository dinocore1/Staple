package com.devsmart.staple.type;


public class IntType implements PrimitiveType {

    int bitWidth;
    boolean isUnsigned;

    public IntType(int width, boolean isUnsigned) {
        this.bitWidth = width;
        this.isUnsigned = isUnsigned;
    }

    @Override
    public String getTypeName() {
        return (isUnsigned ? "u" : "") + String.format("int%d", bitWidth);
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
