package com.devsmart.staple.symbols;

import com.devsmart.staple.types.MemberVarableType;
import com.devsmart.staple.types.StapleType;

public class MemberVarableSymbol extends AbstractSymbol {

	public StapleSymbol baseSymbol;
	public MemberVarableType type;

	public MemberVarableSymbol(StapleSymbol baseSymbol, MemberVarableType member) {
		super(String.format("%s.%s", baseSymbol.getName(), member.name));
		this.baseSymbol = baseSymbol;
		this.type = member;
	}

	@Override
	public StapleType getType() {
		return type.type;
	}
	
	@Override
	public boolean equals(Object o){
		boolean retval = false;
		if(o instanceof MemberVarableSymbol){
			retval = baseSymbol.equals(((MemberVarableSymbol) o).baseSymbol) && type.equals(((MemberVarableSymbol) o).type);
		}
		return retval;
	}
	
	
	
	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return baseSymbol.toString() + "->" + type.toString();
	}

}
