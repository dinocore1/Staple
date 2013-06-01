package com.devsmart.staple.instructions;

import com.devsmart.staple.types.StapleType;

public class MemoryAddress implements Location {

	private StapleType type;

	public MemoryAddress(StapleType type) {
		this.type = type;
	}
	
	@Override
	public StapleType getType() {
		return type;
	}

}
