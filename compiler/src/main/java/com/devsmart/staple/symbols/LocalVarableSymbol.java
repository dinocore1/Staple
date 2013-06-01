package com.devsmart.staple.symbols;

import com.devsmart.staple.types.StapleType;

public class LocalVarableSymbol extends AbstractSymbol {

	public final StapleType type;

	public LocalVarableSymbol(String name, StapleType type) {
		super(name);
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type + " " + mName;
	}

}
