package com.devsmart.staple.type;


public class PointerType implements Type {

    public final Type baseType;

    public PointerType(Type baseType){
        this.baseType = baseType;
    }

    @Override
    public String getTypeName() {
        return baseType.toString() + "*";
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
