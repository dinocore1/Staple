package com.devsmart.staple.type;


import com.devsmart.staple.symbols.Field;

public class StructType implements Type {

    public final String name;
    public Field[] fields;

    public StructType(String name) {
        this.name = name;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("struct %s", name);
    }
}
