package com.devsmart.staple.instructions;

import java.util.ArrayList;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.common.StringUtils;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.FunctionSymbol.Access;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.MemberVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.symbols.StringLiteralSymbol;
import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.ClassType;
import com.devsmart.staple.types.FunctionType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class RenderHelper {
	
	public static String render(STGroup codegentemplate, Object obj) {
		String retval = null;
		
		if(obj instanceof Register) {
			retval = renderLocalVar(codegentemplate, ((Register) obj).name);
		} else if(obj instanceof IntLiteral) {
			retval = renderIntLiteral(codegentemplate, ((IntLiteral) obj).value);
		} else if(obj instanceof StringLiteralSymbol) {
			retval = renderGlobalVar(codegentemplate, ((StringLiteralSymbol) obj).getName());
		} else if(obj instanceof LocalVarableSymbol){
			retval = renderLocalVar(codegentemplate, ((LocalVarableSymbol) obj).getName());
		} else if(obj instanceof SymbolReference){
			StapleSymbol symbol = ((SymbolReference) obj).symbol;
			retval = render(codegentemplate, symbol);
			//retval = renderLocalVar(codegentemplate, ((SymbolReference) obj).symbol.getName());
		} else if(obj instanceof LabelInstruction){
			retval = renderLocalVar(codegentemplate, ((LabelInstruction) obj).name);
		} else if(obj instanceof FunctionSymbol){
			FunctionSymbol fun = (FunctionSymbol) obj;
			if(fun.access == Access.Public || fun.access == Access.External){
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
		} else if(type == PrimitiveType.VOID){
			retval = "void";
		} else if(type == PrimitiveType.BYTE){
			retval = "i8";
		} else if(type == PrimitiveType.BOOL){
			retval = "i1";
		} else if(type == PrimitiveType.ELIPSE){
			retval = "...";
		} else if(type instanceof PointerType){
			retval = renderType(codegentemplate, ((PointerType) type).baseType) + "*";
		} else if(type instanceof ClassType){
			retval = "%" + ((ClassType)type).mName;
		} else if(type instanceof ArrayType) {
			retval = String.format("[%d x %s]", 
					((ArrayType)type).arrayLength, 
					renderType(codegentemplate, ((ArrayType)type).baseType));
		} else if(type instanceof FunctionType){
			FunctionType functionType = (FunctionType) type;
			retval = String.format("%s (%s)", 
					RenderHelper.renderType(codegentemplate, functionType.returnType),
					renderParameters(codegentemplate, functionType.arguments)
					);
		}
		
		return retval;
	}
	
	public static String renderParameters(STGroup codegentemplate, StapleType[] params){
		ArrayList<String> renderArray = new ArrayList<String>(params.length);
		for(StapleType p : params){
			renderArray.add(renderType(codegentemplate, p));
		}
		return StringUtils.delimStr(renderArray, ", ");
	}
	
	
}
