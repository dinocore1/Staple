package com.devsmart.staple.types;

public class ArrayType implements StapleType {

	public final StapleType baseType;
	public final Object arrayLength;

	public ArrayType(StapleType baseType, int arrayLength){
		this.baseType = baseType;
		this.arrayLength = arrayLength;
	}
}
