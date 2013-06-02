package com.devsmart.staple.instructions;

import java.util.Iterator;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;

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
		st.add("formals", new ArgsToString(codegentemplate, mFunctionSymbol.parameters.iterator()));
		st.add("instructions", new InsToString(codegentemplate, body.iterator()));
		
		return st.render();
	}
	
	private class ArgsToString implements Iterator<String> {

		private STGroup template;
		private Iterator<LocalVarableSymbol> iterator;

		public ArgsToString(STGroup codegentemplate,
				Iterator<LocalVarableSymbol> it) {
			this.template = codegentemplate;
			this.iterator = it;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public String next() {
			LocalVarableSymbol parameter = iterator.next();
			
			StringBuilder retval = new StringBuilder();
			
			ST st = template.getInstanceOf("int32type");
			retval.append(st.render());
			
			retval.append(" ");
			
			st = template.getInstanceOf("localid");
			st.add("name", parameter.getName());
			retval.append(st.render());
			
			return retval.toString();
			
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
		
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
