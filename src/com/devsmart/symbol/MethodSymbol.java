package com.devsmart.symbol;

import java.util.LinkedList;
import java.util.List;

import com.devsmart.Scope;
import com.devsmart.type.AbstractType;

public class MethodSymbol extends AbstractSymbol {

	public final AbstractType returnType;
	public final ClassSymbol classSymbol;
	public Scope scope;
	public final List<VarableSymbol> formalArgs = new LinkedList<VarableSymbol>();

	public MethodSymbol(String name, AbstractType rtype, ClassSymbol csymbol) {
		super(name);
		returnType = rtype;
		classSymbol = csymbol;
	}

}
