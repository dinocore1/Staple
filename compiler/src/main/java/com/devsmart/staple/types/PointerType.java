package com.devsmart.staple.types;

public class PointerType extends StapleType {

	public final StapleType baseType;

	public PointerType(StapleType baseType) {
		this.baseType = baseType;
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = false;
		if(obj instanceof PointerType){
			retval = baseType.equals(((PointerType) obj).baseType);
		}
		
		return retval;
	}

	@Override
	public int hashCode() {
		return baseType.hashCode();
	}

	@Override
	public String toString() {
		return baseType.toString() + "*";
	}
	
	
}
