package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class BranchInstruction implements Instruction {

	private Operand condition;
	private LabelInstruction positiveLabel;
	private LabelInstruction negitiveLabel;

	public BranchInstruction(Operand cond, 
			LabelInstruction positiveBlockLabel,
			LabelInstruction negitiveBlockLabel) {
		
		this.condition = cond;
		this.positiveLabel = positiveBlockLabel;
		this.negitiveLabel = negitiveBlockLabel;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("branch");
		st.add("condition", RenderHelper.render(codegentemplate, condition));
		st.add("positive", RenderHelper.render(codegentemplate, positiveLabel));
		st.add("negitive", RenderHelper.render(codegentemplate, negitiveLabel));
		
		return st.render();
	}

}
