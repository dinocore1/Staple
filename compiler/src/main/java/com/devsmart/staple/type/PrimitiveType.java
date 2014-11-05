package com.devsmart.staple.type;


public interface PrimitiveType extends Type {

    public static final IntType Int8 = new IntType(8, false);
    public static final IntType Int16 = new IntType(16, false);
    public static final IntType Int32 = new IntType(32, false);
    public static final IntType Int64 = new IntType(64, false);
    public static final IntType UInt8 = new IntType(8, true);
    public static final IntType UInt16 = new IntType(16, true);
    public static final IntType UInt32 = new IntType(32, true);
    public static final IntType UInt64 = new IntType(64, true);

    public static final BoolType Bool = new BoolType();
}
