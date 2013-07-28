package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class SelectInstruction implements Instruction {

	private Location result;
	private Operand selector;
	private Operand left;
	private Operand right;
	
	public SelectInstruction(Location result, Operand selector, Operand left, Operand right) {
		this.result = result;
		this.selector = selector;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("select");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("selector", RenderHelper.render(codegentemplate, selector));
		st.add("left", RenderHelper.render(codegentemplate, left));
		st.add("right", RenderHelper.render(codegentemplate, right));
		st.add("type", RenderHelper.renderType(codegentemplate, left.getType()));
		return st.render();
	}

}
