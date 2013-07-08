package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.StringLiteralSymbol;

public class StringLiteralDeclareInstruction implements Instruction {

	private StringLiteralSymbol symbol;

	public StringLiteralDeclareInstruction(StringLiteralSymbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public String render(STGroup codegentemplate) {
		
		String cstr = symbol.getLiteral();
		
		ST st = codegentemplate.getInstanceOf("stringliteral");
		st.add("name", RenderHelper.render(codegentemplate, symbol));
		st.add("str", cstr);
		st.add("size", symbol.type.arrayLength);
		
		return st.render();
	}

}
