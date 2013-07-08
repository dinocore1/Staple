package com.devsmart.staple.symbols;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;

public class StringLiteralSymbol extends AbstractSymbol {

	private static Pattern sHexPatter = Pattern.compile("\\\\[a-zA-Z0-9]{2}");
	
	private String literalString;
	public final ArrayType type;
	
	public StringLiteralSymbol(String name, String thestring) {
		super(name);
		
		this.literalString = preprocessString(thestring);
		
		int size = literalString.length();
		Matcher hexCountMatcher = sHexPatter.matcher(literalString);
		while(hexCountMatcher.find()){
			size -= 2;
		}
		
		this.type = new ArrayType(PrimitiveType.BYTE, size);
	}
	
	public String getLiteral() {
		return literalString;
	}

	@Override
	public StapleType getType() {
		return type;
	}
	
	private static final String[] sTranslateTable = {
		"\\\\n", "\\\\0a"
	};
	
	private static String preprocessString(String input) {
		for(int i=0;i<sTranslateTable.length;i+=2){
			input = input.replaceAll(sTranslateTable[i], sTranslateTable[i+1]);
		}
		input += "\\00";
		return input;
	}

}
