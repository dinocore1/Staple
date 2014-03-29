package com.devsmart.staple.type;


public class FunctionType extends Type {

    public final Type returnType;
    public final Type[] args;

    public FunctionType(Type returnType, Type[] args) {
        super(String.format("%s (%s)", returnType, args.toString()));
        this.returnType = returnType;
        this.args = args;
    }
}
