package com.devsmart.staple.instructions;

import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.types.PointerType;

public class FunctionCallInstruction implements Instruction {

	private FunctionSymbol targetSymbol;
	private Location result;
	private List<Operand> arguments;
	
	
	public FunctionCallInstruction(FunctionSymbol targetSymbol, Location result, List<Operand> arguments) {
		this.targetSymbol = targetSymbol;
		this.result = result;
		this.arguments = arguments;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("call");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("name", RenderHelper.render(codegentemplate, targetSymbol));
		st.add("type", RenderHelper.renderType(codegentemplate, new PointerType(targetSymbol.getType())));
		st.add("args", new OperandRenderer(codegentemplate, arguments.iterator()));
		
		
		return st.render();
	}
	
	

}
