package com.devsmart.staple.instructions;

import java.util.HashSet;

import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.StapleType;

public class TempLocation implements Operand, Location {

	public final String name;
	public final HashSet<StapleSymbol> registerDescriptor = new HashSet<StapleSymbol>();
	private final StapleType type;
	
	public TempLocation(String name, StapleType type){
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "t" + name;
	}

	@Override
	public StapleType getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
}
