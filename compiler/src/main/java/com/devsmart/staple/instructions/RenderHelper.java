package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.FunctionSymbol.Type;
import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class RenderHelper {
	
	public static String render(STGroup codegentemplate, Object obj) {
		String retval = null;
		
		if(obj instanceof TempLocation) {
			retval = renderLocalVar(codegentemplate, ((TempLocation) obj).name);
		} else if(obj instanceof IntLiteral) {
			retval = renderIntLiteral(codegentemplate, ((IntLiteral) obj).value);
		} else if(obj instanceof SymbolReference){
			retval = renderLocalVar(codegentemplate, ((SymbolReference) obj).symbol.getName());
		} else if(obj instanceof LabelInstruction){
			retval = renderLocalVar(codegentemplate, ((LabelInstruction) obj).name);
		} else if(obj instanceof FunctionSymbol){
			FunctionSymbol fun = (FunctionSymbol) obj;
			if(fun.type == Type.Public){
				retval = renderGlobalVar(codegentemplate, fun.getName());
			} else {
				retval = renderLocalVar(codegentemplate, fun.getName());
			}
		}
		
		return retval;
	}
	
	public static String renderLocalVar(STGroup codegentemplate, String name){
		ST st = codegentemplate.getInstanceOf("localid");
		st.add("name", name);
		
		return st.render();
	}
	
	public static String renderGlobalVar(STGroup codegentemplate, String name){
		ST st = codegentemplate.getInstanceOf("globalid");
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
			retval = "i32";
		} else if(type == PrimitiveType.BYTE){
			retval = "i8";
		} else if(type == PrimitiveType.BOOL){
			retval = "i1";
		} else if(type instanceof PointerType){
			retval = renderType(codegentemplate, ((PointerType) type).baseType) + "*";
		} else if(type instanceof ArrayType) {
			retval = String.format("[%d x %s]", 
					((ArrayType)type).arrayLength, 
					renderType(codegentemplate, ((ArrayType)type).baseType));
		}
		
		return retval;
	}
	
	
}
