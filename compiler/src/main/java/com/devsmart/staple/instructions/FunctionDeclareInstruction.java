package com.devsmart.staple.instructions;

import java.util.Iterator;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.FunctionSymbol;

public class FunctionDeclareInstruction implements Instruction {

	
	public List<Instruction> body;
	private FunctionSymbol mFunctionSymbol;

	public FunctionDeclareInstruction(FunctionSymbol symbol) {
		mFunctionSymbol = symbol;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("functiondecl");
		st.add("name", mFunctionSymbol.getName());
		st.add("returntype", RenderHelper.renderType(codegentemplate, mFunctionSymbol.returnType));
		st.add("isglobal", true);
		st.add("formals", new ArgsRenderer(codegentemplate, mFunctionSymbol.parameters.iterator()));
		st.add("instructions", new InsToString(codegentemplate, body.iterator()));
		
		return st.render();
	}
	
	
	
	private class InsToString implements Iterator<String> {

		private Iterator<Instruction> mIt;
		private STGroup template;

		public InsToString(STGroup codegentemplate, Iterator<Instruction> iterator) {
			template = codegentemplate;
			mIt = iterator;
		}

		@Override
		public boolean hasNext() {
			return mIt.hasNext();
		}

		@Override
		public String next() {
			Instruction instruction = mIt.next();
			String retval = instruction.render(template);
			return retval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}

		
		
	}

}
