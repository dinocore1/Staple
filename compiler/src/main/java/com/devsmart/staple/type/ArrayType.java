package com.devsmart.staple.type;


public class ArrayType implements Type {

    Type baseType;
    int dimention;

    @Override
    public String getTypeName() {
        return String.format("%s[]", baseType.getTypeName());
    }
}
