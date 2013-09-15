package com.devsmart.staple;

import java.util.Iterator;
import java.util.List;

import com.devsmart.staple.symbols.ClassSymbol;
import com.devsmart.staple.symbols.MemberVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.symbols.StructSymbol;

public class DeRefHelper {

	public StapleSymbol base;
	public String target;
	private MemberVarableSymbol result;
	private int index;

	public DeRefHelper(StapleSymbol base, String target){
		this.base = base;
		this.target = target;
		
		List<MemberVarableSymbol> members = null;
		if(base instanceof StructSymbol){
			members = ((StructSymbol)base).members;
		} else if(base instanceof ClassSymbol){
			members = ((ClassSymbol)base).getAllMembers();
		}
		
		int index = 0;
		Iterator<MemberVarableSymbol> it = members.iterator();
		while(it.hasNext()){
			MemberVarableSymbol sym = it.next();
			if(target.equals(sym.getName())){
				this.result = sym;
				this.index = index;
				break;
			}
			index++;
		}
		
	}
	
	
	public MemberVarableSymbol getMemberVarableSymbol() {
		return result;
	}
	
	public int getOffset() {
		return index;
	}

}
