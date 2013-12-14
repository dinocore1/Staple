package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class BitcastInstruction implements Instruction {

	private Register dest;
	private Register src;

	public BitcastInstruction(Register retval, Register fromRegister) {
		this.dest = retval;
		this.src = fromRegister;
	}

	@Override
	public String render(STGroup codegentemplate) {
		
		ST st = codegentemplate.getInstanceOf("bitcast");
		st.add("src", RenderHelper.render(codegentemplate, src));
		st.add("dest", RenderHelper.render(codegentemplate, dest));
		st.add("srctype", RenderHelper.renderType(codegentemplate, src.getType()));
		st.add("desttype", RenderHelper.renderType(codegentemplate, dest.getType()));
		
		return st.render();
	}

}
