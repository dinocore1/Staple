package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class LabelInstruction implements Instruction {

	public final String name;

	public LabelInstruction(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name + ":";
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("label");
		st.add("name", name);
		
		return st.render();
	}

}
