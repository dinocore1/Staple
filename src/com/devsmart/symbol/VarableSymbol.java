package com.devsmart.symbol;

import com.devsmart.type.AbstractType;

public class VarableSymbol extends AbstractSymbol {

	public final AbstractType type;

	public VarableSymbol(String name, AbstractType t) {
		super(name);
		type = t;
	}
}
