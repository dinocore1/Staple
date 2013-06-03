package com.devsmart.staple.instructions;

public class DivideInstruction extends TernaryInstruction {

	public DivideInstruction(Location result, Operand left, Operand right) {
		super(result, left, right);
	}

	@Override
	public String toString() {
		return result + " = " + left + " / " + right;
	}

	@Override
	protected String getTemplateName() {
		return "divide";
	}

}
