package com.devsmart.staple.type;


public abstract class Type {

    public final String name;

    public Type(String name) {
        this.name = name;
    }

    public abstract boolean isAssignableTo(Type t);

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        boolean retval = false;
        if(obj instanceof Type) {
            retval = name.equals(((Type) obj).name);
        }
        return retval;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
