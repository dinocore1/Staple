package com.devsmart.staple.symbols;

import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class StringLiteralSymbol extends AbstractSymbol {

	private String literalString;
	private ArrayType type;

	public StringLiteralSymbol(String name, String literalString) {
		super(name);
		this.literalString = literalString;
		this.type = new ArrayType(PrimitiveType.BYTE, literalString.length()+1);
	}
	
	public String getLiteral() {
		return literalString;
	}

	@Override
	public StapleType getType() {
		return type;
	}

}
