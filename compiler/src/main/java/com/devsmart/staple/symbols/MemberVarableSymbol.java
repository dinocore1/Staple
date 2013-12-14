package com.devsmart.staple.symbols;

import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.MemberVarableType;

public class MemberVarableSymbol extends AbstractSymbol {

	public final MemberVarableType type;

	public MemberVarableSymbol(String name, MemberVarableType type) {
		super(name);
		this.type = type;
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
