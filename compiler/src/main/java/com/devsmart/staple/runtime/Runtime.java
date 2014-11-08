package com.devsmart.staple.runtime;


import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.symbols.Field;
import com.devsmart.staple.type.IntType;

public class Runtime {

    public static final ClassType BaseObject = new ClassType("Object");

    static {
        Field refCount = new Field(IntType.UInt32, "refCount");
        BaseObject.fields.add(refCount);
    }
}
