package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.StapleSymbol;

public class LoadInstruction implements Instruction {
	
	private MemoryAddress src;
	private Register dest;
	private StapleSymbol symbol;

	public LoadInstruction(MemoryAddress src, Register dest, StapleSymbol symbol){
		this.src = src;
		this.dest = dest;
		this.symbol = symbol;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("load");
		st.add("src", RenderHelper.renderLocalVar(codegentemplate, symbol.getName()));
		st.add("dest", RenderHelper.render(codegentemplate, dest));
		st.add("type", RenderHelper.renderType(codegentemplate, dest.getType()));
		
		return st.render();
	}

}
