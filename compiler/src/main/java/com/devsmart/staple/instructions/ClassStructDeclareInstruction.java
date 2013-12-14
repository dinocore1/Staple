package com.devsmart.staple.instructions;

import java.util.Iterator;

import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.ClassSymbol;
import com.devsmart.staple.types.ClassType;
import com.devsmart.staple.types.MemberVarableType;

public class ClassStructDeclareInstruction implements Instruction {

	private ClassType symbol;

	public ClassStructDeclareInstruction(ClassType classtype) {
		this.symbol = classtype;
	}

	@Override
	public String render(STGroup codegentemplate) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("%");
		buf.append(symbol.mName);
		buf.append(" = type {");
		Iterator<MemberVarableType> it = symbol.getAllMembers().iterator();
		while(it.hasNext()){
			MemberVarableType member = it.next();
			buf.append(RenderHelper.renderType(codegentemplate, member.getType()));
			if(it.hasNext()){
				buf.append(", ");
			}
		}
		buf.append("}\n");
		
		return buf.toString();
	}

}
