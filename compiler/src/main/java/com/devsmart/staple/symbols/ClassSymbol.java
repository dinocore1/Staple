package com.devsmart.staple.symbols;

import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.ClassType;

public class ClassSymbol extends AbstractSymbol {

	public final ClassType type;

	public ClassSymbol(String name) {
		super(name);
		type = new ClassType(name);
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
