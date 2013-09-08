package com.devsmart.staple.symbols;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.devsmart.staple.StapleParser.MemberVarableDeclarationContext;
import com.devsmart.staple.types.ClassType;
import com.devsmart.staple.types.StapleType;

public class ClassSymbol extends AbstractSymbol {

	public final ClassType type;
	public ArrayList<MemberVarableSymbol> members;
	public ArrayList<FunctionSymbol> functions;
	public ClassSymbol extend;

	public ClassSymbol(String name) {
		super(name);
		type = new ClassType(name);
	}

	@Override
	public StapleType getType() {
		return type;
	}
	
	public static void gatherAllMembers(ClassSymbol classSym, List<MemberVarableSymbol> members) {
		if(classSym.extend != null){
			gatherAllMembers(classSym.extend, members);
		}
		for(MemberVarableSymbol member : classSym.members){
			members.add(member);
		}
	}
	
	public List<MemberVarableSymbol> getAllMembers() {
		List<MemberVarableSymbol> retval = new LinkedList<MemberVarableSymbol>();
		
		gatherAllMembers(this, retval);
		
		return retval;
	}

}
