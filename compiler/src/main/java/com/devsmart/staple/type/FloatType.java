package com.devsmart.staple.type;


public class FloatType extends Type {

    public static final Type FLOAT32 = new FloatType(32);
    private final int precision;

    public FloatType(int precision) {
        super(String.format("float%d", precision));
        this.precision = precision;
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;
        if(t instanceof FloatType){
            retval = true;
        }
        return retval;
    }
}
