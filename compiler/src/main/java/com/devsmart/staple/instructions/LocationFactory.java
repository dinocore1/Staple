package com.devsmart.staple.instructions;

import com.devsmart.staple.types.StapleType;

public class LocationFactory {

	private int mCounter = 1;
	
	public Register createTempLocation(StapleType type) {
		return new Register(String.valueOf(mCounter++), type);
	}
	
	public void resetTemps() {
		mCounter = 1;
	}
}
