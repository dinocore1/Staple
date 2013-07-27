package com.devsmart.staple.instructions;

public class BitXorInstruction extends TernaryInstruction {

	
	public BitXorInstruction(Location result, Operand left, Operand right) {
		super(result, left, right);
	}

	@Override
	public String toString() {
		return result + " = " + left + " ^ " + right;
	}
	
	@Override
	protected String getTemplateName() {
		return "bitxor";
	}
	
}
