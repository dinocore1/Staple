package com.devsmart.type;

public class TypeFactory {

	public static final int TYPE_VOID = 0;
	public static final int TYPE_BOOL = 1;
	public static final int TYPE_INT = 2;
	public static final int TYPE_CLASS = 3;
	
	public static int getType(AbstractType t) {
		int retval = -1;
		if(t.equals(PrimitiveType.VOID)){
			retval = TYPE_VOID;
		} else if(t.equals(PrimitiveType.BOOL)){
			retval = TYPE_BOOL;
		} else if(t.equals(PrimitiveType.INT)){
			retval = TYPE_INT;
		} else if(t instanceof ClassType) {
			retval = TYPE_CLASS;
		}
		
		return retval;
	}


}
