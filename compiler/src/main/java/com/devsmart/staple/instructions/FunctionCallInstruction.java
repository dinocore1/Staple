package com.devsmart.staple.instructions;

import java.util.Iterator;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.types.PrimitiveType;

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
		st.add("type", targetSymbol.returnType == PrimitiveType.VOID ? null : RenderHelper.renderType(codegentemplate, targetSymbol.returnType));
		st.add("args", new ArgOpsToStr(codegentemplate, arguments.iterator()));
		
		
		return st.render();
	}
	
	private class ArgOpsToStr implements Iterator<String> {

		private STGroup template;
		private Iterator<Operand> it;

		public ArgOpsToStr(STGroup codegentemplate, Iterator<Operand> iterator) {
			template = codegentemplate;
			it = iterator;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public String next() {
			StringBuilder retval = new StringBuilder();
			
			Operand operand = it.next();
			
			retval.append(RenderHelper.renderType(template, operand.getType()));
			retval.append(" ");
			retval.append(RenderHelper.render(template, operand));
			
			return retval.toString();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
	}

}
