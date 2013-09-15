package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.PointerType;

public class AllocVariableInstruction implements Instruction {

	
	private Register result;
	private PointerType type;
	int num;

	public AllocVariableInstruction(Register result, int num) {
		this.result = result;
		this.type = (PointerType) result.getType();
		this.num = num;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("allocvar");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("type", RenderHelper.renderType(codegentemplate, type.baseType));
		if(num > 1){
			st.add("num", String.valueOf(num));
		}
		
		return st.render();
	}

}
