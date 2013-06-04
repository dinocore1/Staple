package com.devsmart.staple.instructions;

import java.util.Iterator;

import org.stringtemplate.v4.STGroup;

public class OperandRenderer implements Iterator<String> {

	private STGroup template;
	private Iterator<Operand> it;

	public OperandRenderer(STGroup codegentemplate, Iterator<Operand> iterator) {
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
