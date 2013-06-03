package com.devsmart.staple.instructions;

import java.util.List;

import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;

public class InvokeFunctionInstruction implements Instruction {

	private FunctionSymbol targetSymbol;
	private List<Operand> arguments;
	private LabelInstruction normalLabel;
	private LabelInstruction exceptionLabel;

	public InvokeFunctionInstruction(
			FunctionSymbol targetSymbol,
			List<Operand> arguments,
			LabelInstruction normallabel,
			LabelInstruction exceptionlabel) {
	
		this.targetSymbol = targetSymbol;
		this.arguments = arguments;
		this.normalLabel = normallabel;
		this.exceptionLabel = exceptionlabel;
	}

	@Override
	public String render(STGroup codegentemplate) {
		// TODO Auto-generated method stub
		return null;
	}

}
