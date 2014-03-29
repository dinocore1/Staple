package com.devsmart.staple.type;


public class PointerType extends Type {

    public Type baseType;

    public PointerType(Type baseType) {
        super(String.format("*%s", baseType));
        this.baseType = baseType;
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;
        if(t instanceof IntType) {
            retval = ((IntType) t).precision >= 32;
        }
        return retval;
    }
}
