package com.devsmart.staple.types;

import com.devsmart.staple.CompileContext;
import com.devsmart.staple.Scope;
import com.devsmart.staple.symbols.StapleSymbol;

public class TypeFactory {

	public static StapleType getType(String typeStr, CompileContext context) {
		
		StapleType retval = null;
		if("bool".equals(typeStr)){
			retval = PrimitiveType.BOOL;
		} else if("int".equals(typeStr)){
			retval = PrimitiveType.INT;
		} else if("void".equals(typeStr)){
			retval = PrimitiveType.VOID;
		} else if("...".equals(typeStr)){
			retval = PrimitiveType.ELIPSE;
		} else if("byte".equals(typeStr)){
			retval = PrimitiveType.BYTE;
		} else {
			for(StapleType type : context.types){
				if(type instanceof ClassType && ((ClassType)type).mName.equals(typeStr)){
					retval = type;
					break;
				}
			}
		}
		
		return retval;
	}
	

}
