package com.devsmart.staple.types;

import java.util.ArrayList;

import com.devsmart.staple.symbols.MemberVarableSymbol;


public class ClassType extends StapleType {

	public final String mName;
	public ClassType extendsType;
	public ArrayList<MemberVarableType> members;
	public ArrayList<FunctionType> functions;
	
	public ClassType(String name) {
		mName = name;
	}
	
	@Override
	public String toString() {
		return mName;
	}

	@Override
	public boolean equals(Object o) {
		boolean retval = false;
		if(o instanceof ClassType){
			retval = mName.equals(((ClassType) o).mName);
		}
		return retval;
	}

	public MemberVarableType getMemberByName(String text) {
		MemberVarableType retval = null;
		for(MemberVarableType member : members) {
			if(member.name.equals(text)){
				retval = member;
				break;
			}
		}
		return retval;
	}

	public Iterable<MemberVarableType> getAllMembers() {
		ArrayList<MemberVarableType> memberList = new ArrayList<MemberVarableType>();
		if(extendsType != null) {
			for(MemberVarableType m : extendsType.getAllMembers()){
				memberList.add(m);
			}
		}
		memberList.addAll(members);
		return memberList;
	}
}
