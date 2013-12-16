package com.devsmart.staple.symbols;

import com.devsmart.staple.types.MemberVarableType;
import com.devsmart.staple.types.StapleType;

public class MemberVarableSymbol extends AbstractSymbol {

	public StapleSymbol baseSymbol;
	public MemberVarableType member;

	public MemberVarableSymbol(StapleSymbol baseSymbol, MemberVarableType member) {
		super(member.name);
		this.baseSymbol = baseSymbol;
		this.member = member;
	}

	@Override
	public StapleType getType() {
		return member.getType();
	}

}
