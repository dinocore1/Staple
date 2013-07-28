package com.devsmart.staple.instructions;

import org.stringtemplate.v4.STGroup;

public class JumpInstruction implements Instruction {

	public final String name;

	public JumpInstruction(String name) {
		this.name = name;
	}

	@Override
	public String render(STGroup codegentemplate) {
		return "br label %" + name;
	}

}
