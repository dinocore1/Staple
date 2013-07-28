package com.devsmart.staple.instructions;

import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;


public class IntLiteral implements Operand {

	public final int value;
	private StapleType type;

	public IntLiteral(int value) {
		this.value = value;
		this.type = PrimitiveType.INT;
	}
	
	public IntLiteral(int value, StapleType type){
		this.value = value;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public StapleType getType() {
		return type;
	}
	
	

}
