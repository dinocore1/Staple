package com.devsmart.staple.instructions;

import com.devsmart.staple.symbols.StapleSymbol;

public class SymbolReference implements Operand {

	public final StapleSymbol symbol;

	public SymbolReference(StapleSymbol symbol) {
		this.symbol = symbol;
	}

}
