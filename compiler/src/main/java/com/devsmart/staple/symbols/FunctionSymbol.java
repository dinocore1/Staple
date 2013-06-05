package com.devsmart.staple.symbols;

import java.util.ArrayList;
import java.util.List;

import com.devsmart.staple.Scope;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.types.FunctionType;
import com.devsmart.staple.types.StapleType;


public class FunctionSymbol extends AbstractSymbol {

	public static enum Access {
		Public,
		Private,
		Protected,
		External
	}
	
	public Access access = Access.Public;
	public Scope scope;
	public StapleType returnType;
	public List<StapleSymbol> parameters = new ArrayList<StapleSymbol>();
	public FunctionType type;

	public FunctionSymbol(String name) {
		super(name);
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
