package com.devsmart.staple.symbols;

import com.devsmart.staple.types.StapleType;

public class MemberVarableSymbol extends AbstractSymbol {

	public final StapleType type;
	
	public MemberVarableSymbol(String name, StapleType type) {
		super(name);
		this.type = type;
	}

	@Override
	public String toString() {
		return type + " " + mName;
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
