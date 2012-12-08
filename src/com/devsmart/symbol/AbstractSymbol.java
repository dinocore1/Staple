package com.devsmart.symbol;

public abstract class AbstractSymbol {

	protected String mName;
	
	public AbstractSymbol(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}
}
