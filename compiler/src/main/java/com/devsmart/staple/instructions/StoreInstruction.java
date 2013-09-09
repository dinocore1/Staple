package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class StoreInstruction implements Instruction {

	private Operand dest;
	private Operand src;


	public StoreInstruction(Operand src, Operand dest) {
		this.dest = dest;
		this.src = src;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("store");
		st.add("src", RenderHelper.render(codegentemplate, src));
		st.add("dest", RenderHelper.render(codegentemplate, dest));
		st.add("type", RenderHelper.renderType(codegentemplate, src.getType()));
		
		return st.render();
	}

}
