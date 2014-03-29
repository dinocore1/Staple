package com.devsmart.staple.type;


public class IntType extends Type {

    public static final IntType INT8 = new IntType(8);
    public static final IntType INT16 = new IntType(16);
    public static final IntType INT32 = new IntType(32);
    public static final IntType INT64 = new IntType(64);

    public final int precision;

    public IntType(int precision) {
        super(String.format("int%d", precision));
        this.precision = precision;
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;

        if(t instanceof IntType){
            retval = true;
        } else if(t instanceof FloatType){
            retval = true;
        }

        return retval;
    }
}
