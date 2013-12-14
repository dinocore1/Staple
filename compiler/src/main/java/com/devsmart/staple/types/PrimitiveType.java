package com.devsmart.staple.types;

public class PrimitiveType extends StapleType {
	
	public static final PrimitiveType VOID = new PrimitiveType("void");
	public static final PrimitiveType BOOL = new PrimitiveType("bool");
	public static final PrimitiveType INT = new PrimitiveType("int");
	public static final PrimitiveType BYTE = new PrimitiveType("byte");
	public static final PrimitiveType ELIPSE = new PrimitiveType("...");
	
	private String mName;
	
	public PrimitiveType(String string) {
		mName = string;
	}
	
	@Override
	public String toString() {
		return mName;
	}

	
}
