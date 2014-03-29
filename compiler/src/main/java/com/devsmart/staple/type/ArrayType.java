package com.devsmart.staple.type;

public class ArrayType extends Type {

    public Type baseType;

    public ArrayType(Type baseType) {
        super(String.format("%s[]", baseType.name));
        this.baseType = baseType;
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;
        if(t instanceof ArrayType){
            retval = true;
        }
        return retval;
    }
}
