package com.devsmart.staple.instructions;

import java.util.Iterator;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.MultiVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.StapleType;

class ArgsRenderer implements Iterator<String> {

	private STGroup template;
	private Iterator<StapleSymbol> iterator;

	public ArgsRenderer(STGroup codegentemplate, Iterator<StapleSymbol> it) {
		this.template = codegentemplate;
		this.iterator = it;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public String next() {
		StapleSymbol parameter = iterator.next();
		
		StringBuilder retval = new StringBuilder();
		
		if(parameter instanceof LocalVarableSymbol){
			StapleType type = ((LocalVarableSymbol) parameter).type;
			
			retval.append(RenderHelper.renderType(template, type));
			retval.append(" ");
			ST st = template.getInstanceOf("localid");
			st.add("name", parameter.getName());
			retval.append(st.render());
		} else if(parameter instanceof MultiVarableSymbol){
			retval.append("...");
		}
		
		
		
		return retval.toString();
		
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
}
