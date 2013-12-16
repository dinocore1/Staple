package com.devsmart.staple.types;

import java.util.Iterator;

public class MemberVarableType extends StapleType {

	public final ClassType base;
	public final String name;
	public final StapleType type;
	private int offset;
	
	public MemberVarableType(ClassType base, String name, StapleType type) {
		this.base = base;
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return "[" + type + " " + name + "]";
	}

	public StapleType getType() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = false;
		if(obj instanceof MemberVarableType){
			retval = this.name.equals(((MemberVarableType) obj).name);
		}
		
		return retval;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public void computeOffset(ClassType base) {
		offset = 0;
		Iterator<MemberVarableType> it = base.members.iterator();
		while(it.hasNext()){
			MemberVarableType sym = it.next();
			if(this.name.equals(sym.name)){
				break;
			}
			offset++;
		}
		
	}
	
	public int getOffset() {
		return offset;
	}
	
	

}
