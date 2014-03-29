package com.devsmart.staple.type;


public class FunctionType extends Type {

    public final Type returnType;
    public final Type[] args;

    public FunctionType(Type returnType, Type[] args) {
        super(String.format("%s (%s)", returnType, args.toString()));
        this.returnType = returnType;
        this.args = args;
    }

    @Override
    public boolean isAssignableTo(Type t) {
        boolean retval = false;
        if(t instanceof FunctionType){
            FunctionType other = (FunctionType)t;
            retval = returnType.equals(other.returnType);
            if(retval) {
            retval = retval && args.length == other.args.length;
                if(retval) {
                    for(int i=0;i<args.length;i++){
                        retval = retval && args[i].equals(other.args[i]);
                    }
                }
            }

        }
        return retval;
    }
}
