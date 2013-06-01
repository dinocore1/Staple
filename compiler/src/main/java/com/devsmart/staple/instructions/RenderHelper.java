package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class RenderHelper {
	
	public static String render(STGroup codegentemplate, Object obj) {
		String retval = null;
		
		if(obj instanceof TempLocation) {
			retval = renderLocalVar(codegentemplate, ((TempLocation) obj).name);
		} else if(obj instanceof IntLiteral) {
			retval = renderIntLiteral(codegentemplate, ((IntLiteral) obj).value);
		}
		
		return retval;
	}
	
	public static String renderLocalVar(STGroup codegentemplate, String name){
		ST st = codegentemplate.getInstanceOf("localid");
		st.add("name", name);
		
		return st.render();
	}
	
	public static String renderIntLiteral(STGroup codegentemplate, int value){
		ST st = codegentemplate.getInstanceOf("intliteral");
		st.add("value", value);
		
		return st.render();
	}

	public static String renderType(STGroup codegentemplate, StapleType type) {
		ST st = null;
		String retval = "";
		if(type == PrimitiveType.INT){
			st = codegentemplate.getInstanceOf("int32type");
		}
		
		retval = st.render();
		
		return retval;
	}
	
	
}
