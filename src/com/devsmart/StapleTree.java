package com.devsmart;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import com.devsmart.symbol.AbstractSymbol;

public class StapleTree extends CommonTree {

	public AbstractSymbol symbol;
	
	public StapleTree(Token t) {super(t);}
}
