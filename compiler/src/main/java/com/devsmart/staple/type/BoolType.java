package com.devsmart.staple.type;


public class BoolType implements PrimitiveType {
    @Override
    public String getTypeName() {
        return "bool";
    }

    @Override
    public boolean isAssignableTo(Type dest) {
        return dest instanceof BoolType;
    }
}
