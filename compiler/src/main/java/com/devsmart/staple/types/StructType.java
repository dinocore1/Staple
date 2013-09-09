package com.devsmart.staple.types;

import com.devsmart.staple.symbols.*;

public class StructType implements StapleType {

	public final String mName;
	
	public StructType(String name) {
		mName = name;
	}
	
	@Override
	public String toString() {
		return mName;
	}
	
}
