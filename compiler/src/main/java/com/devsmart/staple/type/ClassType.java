package com.devsmart.staple.type;


public class ClassType extends Type {

    public ClassType(String name) {
        super(name);
    }

    @Override
    public boolean isAssignableTo(Type t) {
        return false;
    }
}
