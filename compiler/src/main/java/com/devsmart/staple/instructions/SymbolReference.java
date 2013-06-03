package com.devsmart.staple.instructions;

import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.StapleType;

public class SymbolReference implements Operand {

	public final StapleSymbol symbol;

	public SymbolReference(StapleSymbol symbol) {
		this.symbol = symbol;
	}

	@Override
	public StapleType getType() {
		StapleType retval = null;
		
		if(symbol instanceof LocalVarableSymbol){
			retval = ((LocalVarableSymbol) symbol).type;
		}
		
		return retval;
	}

}
