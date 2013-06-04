package com.devsmart.staple.symbols;

import com.devsmart.staple.Scope;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class BlockSymbol implements StapleSymbol {

	public final Scope scope;

	public BlockSymbol(Scope scope) {
		this.scope = scope;
	}

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public StapleType getType() {
		return PrimitiveType.VOID;
	}

}
