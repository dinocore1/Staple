package com.devsmart.symbol;

import java.util.HashMap;

import com.devsmart.Scope;
import com.devsmart.type.ClassType;

public class ClassSymbol extends AbstractSymbol {

	public Scope scope;
	public ClassSymbol subclass;
	public HashMap<String, VarableSymbol> fields = new HashMap<String, VarableSymbol>();
	public HashMap<String, MethodSymbol> methods = new HashMap<String, MethodSymbol>();
	public final ClassType type = new ClassType(this);
	
	public ClassSymbol(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "CLASS " + mName;
	}

}
