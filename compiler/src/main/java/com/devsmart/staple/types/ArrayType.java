package com.devsmart.staple.types;

public class ArrayType extends StapleType {

	public final StapleType baseType;
	public final int arrayLength;

	public ArrayType(StapleType baseType, int arrayLength){
		this.baseType = baseType;
		this.arrayLength = arrayLength;
	}

	@Override
	public boolean equals(Object o) {
		boolean retval = false;
		if(o instanceof ArrayType){
			retval = baseType.equals(((ArrayType) o).baseType);
		}
		
		return retval;
	}
}
