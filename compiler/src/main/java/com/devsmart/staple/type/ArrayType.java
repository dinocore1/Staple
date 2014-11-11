package com.devsmart.staple.type;


public class ArrayType implements Type {

    Type baseType;
    int dimention;

    @Override
    public String getTypeName() {
        return String.format("%s[]", baseType.getTypeName());
    }

    @Override
    public boolean isAssignableTo(Type dest) {
        boolean retval = false;
        if(dest instanceof ArrayType){
            retval = baseType.equals(((ArrayType) dest).baseType);
        }
        return retval;
    }
}
