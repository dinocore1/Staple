package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.types.StapleType;

public class AllocVariableInstruction implements Instruction {

	
	private String mName;
	private StapleType mType;

	public AllocVariableInstruction(LocalVarableSymbol symbol) {
		mName = symbol.getName();
		mType = symbol.type;
	}
	
	public AllocVariableInstruction(String name, StapleType type){
		mName = name;
		mType = type;
	}
	
	public AllocVariableInstruction(Register tempLocation) {
		mName = tempLocation.name;
		mType = tempLocation.getType();
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("allocvar");
		st.add("name", RenderHelper.renderLocalVar(codegentemplate, mName));
		st.add("type", RenderHelper.renderType(codegentemplate, mType));
		
		return st.render();
	}

}
