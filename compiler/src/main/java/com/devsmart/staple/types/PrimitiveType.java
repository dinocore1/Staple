package com.devsmart.staple.types;

public class PrimitiveType implements StapleType {
	
	public static final PrimitiveType VOID = new PrimitiveType("void");
	public static final PrimitiveType BOOL = new PrimitiveType("bool");
	public static final PrimitiveType INT = new PrimitiveType("int");
	
	private String mName;
	
	public PrimitiveType(String string) {
		mName = string;
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
}
