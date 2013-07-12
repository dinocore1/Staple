package com.devsmart.staple.instructions;

import com.devsmart.staple.types.StapleType;

public class MemoryAddress implements Location {

	private StapleType type;
	private String name;

	public MemoryAddress(String name, StapleType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public StapleType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

}
