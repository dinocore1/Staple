package com.devsmart.staple.instructions;

import java.util.Iterator;

import org.stringtemplate.v4.STGroup;
import com.devsmart.staple.symbols.*;

public class StructDeclarationInstruction implements Instruction {

	private StructSymbol symbol;
	
	public StructDeclarationInstruction(StructSymbol symbol){
		this.symbol = symbol;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("%");
		buf.append(symbol.getName());
		buf.append(" = type {");
		Iterator<MemberVarableSymbol> it = symbol.members.iterator();
		while(it.hasNext()){
			MemberVarableSymbol member = it.next();
			buf.append(RenderHelper.renderType(codegentemplate, member.getType()));
			if(it.hasNext()){
				buf.append(", ");
			}
		}
		buf.append("}\n");
		
		return buf.toString();
	}

}
