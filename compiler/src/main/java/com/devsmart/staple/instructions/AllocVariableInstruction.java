package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.LocalVarableSymbol;

public class AllocVariableInstruction implements Instruction {

	private LocalVarableSymbol symbol;

	public AllocVariableInstruction(LocalVarableSymbol symbol) {
		this.symbol = symbol;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("allocvar");
		st.add("name", RenderHelper.renderLocalVar(codegentemplate, symbol.getName()));
		st.add("type", RenderHelper.renderType(codegentemplate, symbol.type));
		
		return st.render();
	}

}
