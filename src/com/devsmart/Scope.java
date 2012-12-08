package com.devsmart;

import java.util.HashMap;

import com.devsmart.symbol.AbstractSymbol;

public class Scope {
	
	private Scope mParent;
	private HashMap<String, AbstractSymbol> mTable = new HashMap<String, AbstractSymbol>();
	
	public Scope(Scope parent){
		mParent = parent;
	}
	
	public Scope push() {
		return new Scope(this);
	}
	
	public Scope pop() {
		return mParent;
	}
	
	public Scope getParent(){
		return mParent;
	}
	
	public void define(AbstractSymbol symbol){
		String name = symbol.getName();
		if(!mTable.containsKey(name)){
			mTable.put(name, symbol);
		}
	}
	
	public AbstractSymbol resolve(String name){
		AbstractSymbol retval = mTable.get(name);
		if(retval == null && mParent != null){
			retval = mParent.resolve(name);
		}
		return retval;
	}

}
