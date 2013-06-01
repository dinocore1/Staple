package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.PrimitiveType;

public abstract class TernaryInstruction implements Instruction {
	
	public Location result;
	public Operand left;
	public Operand right;
	
	public TernaryInstruction(Location result, Operand left, Operand right){
		this.result = result;
		this.left = left;
		this.right = right;
	}

	protected abstract String getTemplateName();
	
	@Override
	public String render(STGroup codegentemplate) {
		
		ST st = codegentemplate.getInstanceOf(getTemplateName());
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("left", RenderHelper.render(codegentemplate, left));
		st.add("right", RenderHelper.render(codegentemplate, right));
		st.add("type", RenderHelper.renderType(codegentemplate, PrimitiveType.INT));
		return st.render();
	}
}
