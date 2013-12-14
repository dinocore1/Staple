package com.devsmart.staple.symbols;

import com.devsmart.staple.types.ClassType;
import com.devsmart.staple.types.MemberVarableType;
import com.devsmart.staple.types.StapleType;

public class ClassSymbol extends AbstractSymbol {

	public final ClassType type;
	

	public ClassSymbol(String name, ClassType type) {
		super(name);
		this.type = type;
	}

	@Override
	public StapleType getType() {
		return type;
	}

	


}
