package com.devsmart.staple.type;


import com.devsmart.staple.Symbol;

public class Field implements Symbol {

    public final Type type;
    public final String name;

    public Field(Type type, String name){
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s %s", type.getTypeName(), name);
    }

}
