package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;

public class ExternalFunctionInstruction implements Instruction {

	private FunctionSymbol symbol;

	public ExternalFunctionInstruction(FunctionSymbol functionSymbol) {
		this.symbol = functionSymbol;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("externfunctiondecl");
		st.add("name", symbol.getName());
		st.add("returntype", RenderHelper.renderType(codegentemplate, symbol.returnType));
		st.add("formals", new ArgsRenderer(codegentemplate, symbol.parameters.iterator()));
		
		return st.render();
	}

}
