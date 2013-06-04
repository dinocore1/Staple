package com.devsmart.staple.types;

import java.util.Arrays;
import java.util.Iterator;

import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.StapleSymbol;

public class FunctionType implements StapleType {

	public final StapleType returnType;
	public final StapleType[] arguments;

	public FunctionType(StapleType returnType, StapleType[] arguments){
		this.returnType = returnType;
		this.arguments = arguments;
	}

	public FunctionType(FunctionSymbol functionSymbol) {
		this.returnType = functionSymbol.returnType;
		this.arguments = new StapleType[functionSymbol.parameters.size()];
		
		Iterator<StapleSymbol> it = functionSymbol.parameters.iterator();
		for(int i=0;i<arguments.length;i++){
			arguments[i] = it.next().getType();
		}
		
	}

	@Override
	public boolean equals(Object obj) {
		boolean retval = false;
		
		if(obj instanceof FunctionType){
			FunctionType other = (FunctionType) obj;
			retval = returnType.equals(other) && Arrays.equals(arguments, other.arguments);
		}
		
		return retval;
	}

	@Override
	public int hashCode() {
		return returnType.hashCode() ^ arguments.hashCode();
	}

	
}
