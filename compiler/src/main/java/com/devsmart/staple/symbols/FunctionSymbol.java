package com.devsmart.staple.symbols;

import com.devsmart.staple.Scope;
import com.devsmart.staple.types.StapleType;


public class FunctionSymbol extends AbstractSymbol {

	public Scope scope;
	public StapleType returnType;

	public FunctionSymbol(String name) {
		super(name);
	}

}
