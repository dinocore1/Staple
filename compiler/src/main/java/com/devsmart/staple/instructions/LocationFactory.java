package com.devsmart.staple.instructions;

import com.devsmart.staple.types.StapleType;

public class LocationFactory {

	private int mCounter = 1;
	
	public TempLocation createTempLocation(StapleType type) {
		return new TempLocation(String.valueOf(mCounter++), type);
	}
	
	public void resetTemps() {
		mCounter = 1;
	}
}
