package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.StapleType;

public class AllocVariableInstruction implements Instruction {

	
	private Register result;
	private StapleType type;
	int num;

	public AllocVariableInstruction(Register result, StapleType type, int num) {
		this.result = result;
		this.type = type;
		this.num = num;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("allocvar");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("type", RenderHelper.renderType(codegentemplate, type));
		if(num > 1){
			st.add("num", String.valueOf(num));
		}
		
		return st.render();
	}

}
