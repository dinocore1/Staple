package com.devsmart.staple;

import java.util.HashMap;

import com.devsmart.staple.symbols.StapleSymbol;

public class Scope {
	
	private Scope mParent;
	private HashMap<String, StapleSymbol> mTable = new HashMap<String, StapleSymbol>();
	
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

	public Scope getRoot() {
		Scope s = this;
		while(s.mParent != null){
			s = s.mParent;
		}
		return s;
	}

	public void define(StapleSymbol symbol){
		String name = symbol.getName();
		if(!mTable.containsKey(name)){
			mTable.put(name, symbol);
		}
	}


	public StapleSymbol resolve(String name){

		StapleSymbol retval = mTable.get(name);
		if(retval == null && mParent != null){
			retval = mParent.resolve(name);
		}
		return retval;
	}
	
	@Override
	public String toString() {
		return mTable.toString();
	}
	
}
