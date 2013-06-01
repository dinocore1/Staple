package com.devsmart.staple.instructions;


public class IntLiteral implements Operand {

	public final int value;

	public IntLiteral(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	

}
