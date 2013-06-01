package com.devsmart.staple.instructions;

public class AddInstruction extends TernaryInstruction {

	
	public AddInstruction(Location result, Operand left, Operand right) {
		super(result, left, right);
	}

	@Override
	public String toString() {
		return result + " = " + left + " + " + right;
	}
	
	@Override
	protected String getTemplateName() {
		return "add";
	}
	
}
