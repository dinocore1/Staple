package com.devsmart;

import java.util.HashMap;

import com.devsmart.symbol.AbstractSymbol;
import com.devsmart.symbol.NamespaceSymbol;

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
	
	public Scope getRoot() {
		Scope s = this;
		while(s.mParent != null){
			s = s.mParent;
		}
		return s;
	}
	
	public void define(AbstractSymbol symbol){
		String name = symbol.getName();
		if(!mTable.containsKey(name)){
			mTable.put(name, symbol);
		}
	}
	
	public AbstractSymbol resolve(String[] fqname) {
		AbstractSymbol retval = null;
		Scope s = getRoot();
		for(int i=0;i<fqname.length;i++){
			retval = s.resolve(fqname[i]);
			if(retval instanceof NamespaceSymbol){
				s = ((NamespaceSymbol)retval).scope;
			}
		}
		return retval;
	}
	
	public AbstractSymbol resolve(String name){
		
		String[] symnames = name.split("\\.");
		if(symnames.length > 1){
			return resolve(symnames);
		}
		
		AbstractSymbol retval = mTable.get(name);
		if(retval == null && mParent != null){
			retval = mParent.resolve(name);
		}
		return retval;
	}

}
