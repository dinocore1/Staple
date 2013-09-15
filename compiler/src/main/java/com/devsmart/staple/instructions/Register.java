package com.devsmart.staple.instructions;

import com.devsmart.staple.types.StapleType;

public class Register implements Operand, Location {

	public final String name;
	private final StapleType type;
	
	public Register(String name, StapleType type){
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
