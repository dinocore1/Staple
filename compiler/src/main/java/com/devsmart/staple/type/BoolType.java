package com.devsmart.staple.type;


public class BoolType extends Type {

    public static final BoolType BOOL = new BoolType();

    public BoolType() {
        super("bool");
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;
        if(t instanceof BoolType){
            retval = true;
        }
        return retval;
    }
}
