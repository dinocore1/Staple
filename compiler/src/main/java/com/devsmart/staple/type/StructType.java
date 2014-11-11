package com.devsmart.staple.type;


import com.devsmart.staple.symbols.Field;

import java.util.Arrays;

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
    public boolean isAssignableTo(Type dest) {
        boolean retval = false;
        if(dest instanceof StructType){
            StructType destStruc = (StructType)dest;
            if(fields.length >= destStruc.fields.length) {
                for (int i = 0; i < destStruc.fields.length; i++) {
                    if(!fields[i].type.equals(destStruc.fields[i].type)){
                        return false;
                    }
                }
                retval = true;
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        return String.format("struct %s", name);
    }
}
