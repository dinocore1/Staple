package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.StapleSymbol;

public class StoreInstruction implements Instruction {

	private MemoryAddress dest;
	private Operand src;
	private StapleSymbol symbol;

	public StoreInstruction(Operand src, MemoryAddress dest, StapleSymbol symbol) {
		this.dest = dest;
		this.src = src;
		this.symbol = symbol;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("store");
		st.add("src", RenderHelper.render(codegentemplate, src));
		st.add("dest", RenderHelper.renderLocalVar(codegentemplate, symbol.getName()));
		st.add("type", RenderHelper.renderType(codegentemplate, dest.getType()));
		
		return st.render();
	}

}
