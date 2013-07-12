package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class StoreInstruction implements Instruction {

	private Location dest;
	private Operand src;


	public StoreInstruction(Operand src, Location dest) {
		this.dest = dest;
		this.src = src;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("store");
		st.add("src", RenderHelper.render(codegentemplate, src));
		st.add("dest", RenderHelper.renderLocalVar(codegentemplate, dest.getName()));
		st.add("type", RenderHelper.renderType(codegentemplate, dest.getType()));
		
		return st.render();
	}

}
