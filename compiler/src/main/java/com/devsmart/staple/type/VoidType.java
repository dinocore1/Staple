package com.devsmart.staple.type;

public class VoidType implements PrimitiveType {
    @Override
    public String getTypeName() {
        return "void";
    }

    @Override
    public boolean isAssignableTo(Type dest) {
        return dest instanceof VoidType;
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
