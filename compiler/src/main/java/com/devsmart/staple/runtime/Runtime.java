package com.devsmart.staple.runtime;


import com.devsmart.staple.symbols.Argument;
import com.devsmart.staple.type.*;
import com.devsmart.staple.symbols.Field;

import java.util.Arrays;

public class Runtime {

    public static final StructType BaseObjectClass = new StructType("stp_objectClass");
    public static final ClassType BaseObject = new ClassType("stp_object");

    static {

        BaseObjectClass.fields = new Field[]{
                new Field(new PointerType(PrimitiveType.UInt8), "name"),
                new Field(new PointerType(BaseObjectClass), "parent"),
                new Field(new PointerType(FunctionType.anomousFunction(PrimitiveType.Void, new Argument[]{ new Argument(new PointerType(PrimitiveType.Void), "self")})), "init"),
                new Field(new PointerType(FunctionType.anomousFunction(PrimitiveType.Void, new Argument[]{ new Argument(new PointerType(PrimitiveType.Void), "self")})), "dest"),
                //new Field(new PointerType(FunctionType.anomousFunction(new PointerType(PrimitiveType.UInt8), null)), "toString")
        };

        BaseObject.fields = Arrays.asList(new Field[] {
                new Field(new PointerType(BaseObjectClass), "classType"),
                new Field(IntType.UInt32, "refCount")
        });
    }
}
