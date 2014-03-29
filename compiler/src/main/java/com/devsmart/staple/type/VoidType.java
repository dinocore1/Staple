package com.devsmart.staple.type;


public class VoidType extends Type {

    public static final VoidType VOID = new VoidType();

    public VoidType() {
        super("void");
    }

    @Override
    public boolean isAssignableTo(Type t) {
        return false;
    }
}
