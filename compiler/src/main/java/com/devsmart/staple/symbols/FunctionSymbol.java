package com.devsmart.staple.symbols;

import java.util.ArrayList;
import java.util.List;

import com.devsmart.staple.Scope;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.types.StapleType;


public class FunctionSymbol extends AbstractSymbol {

	public static enum Type {
		Public,
		Private,
		Protected,
		External
	}
	
	public Type type = Type.Public;
	public Scope scope;
	public StapleType returnType;
	public List<StapleSymbol> parameters = new ArrayList<StapleSymbol>();

	public FunctionSymbol(String name) {
		super(name);
	}

	@Override
	public StapleType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
