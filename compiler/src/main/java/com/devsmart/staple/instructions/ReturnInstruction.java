package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.PrimitiveType;

public class ReturnInstruction implements Instruction {
	
	private Operand result;

	public ReturnInstruction(Operand result) {
		this.result = result;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("return");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("type", RenderHelper.renderType(codegentemplate, PrimitiveType.INT));
		
		return st.render();
	}

}
