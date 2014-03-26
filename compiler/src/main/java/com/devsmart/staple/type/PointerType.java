package com.devsmart.staple.type;


public class PointerType extends Type {

    public static Type baseType;

    public PointerType(Type baseType) {
        super(String.format("*%s", baseType));
        this.baseType = baseType;
    }

}
