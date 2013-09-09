package com.devsmart.staple.symbols;

import java.util.List;

import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.StructType;

public class StructSymbol extends AbstractSymbol {

	public final StructType type;
	public List<MemberVarableSymbol> members;
	
	public StructSymbol(String name) {
		super(name);
		type = new StructType(name);
	}
	
	@Override
	public StapleType getType() {
		return type;
	}
	
	public MemberVarableSymbol getMemberByName(String name){
		MemberVarableSymbol retval = null;
		
		for(MemberVarableSymbol sym : members){
			if(sym.mName.equals(name)){
				retval = sym;
				break;
			}
		}
		
		return retval;
	}
	
	@Override
	public String toString() {
		return "struct " + mName;
	}

}
