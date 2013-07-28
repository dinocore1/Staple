package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class LoadInstruction implements Instruction {
	
	private Location src;
	private Location dest;

	public LoadInstruction(Location src, Location dest){
		this.src = src;
		this.dest = dest;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("load");
		st.add("src", RenderHelper.render(codegentemplate, src));
		st.add("dest", RenderHelper.render(codegentemplate, dest));
		st.add("type", RenderHelper.renderType(codegentemplate, src.getType()));
		
		return st.render();
	}

}
