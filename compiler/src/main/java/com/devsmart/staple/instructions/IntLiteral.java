package com.devsmart.staple.instructions;

import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;


public class IntLiteral implements Operand {

	public final int value;

	public IntLiteral(int value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public StapleType getType() {
		return PrimitiveType.INT;
	}
	
	

}
