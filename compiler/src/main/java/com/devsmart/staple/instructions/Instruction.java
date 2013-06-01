package com.devsmart.staple.instructions;

import org.stringtemplate.v4.STGroup;

public interface Instruction {

	String render(STGroup codegentemplate);

}
