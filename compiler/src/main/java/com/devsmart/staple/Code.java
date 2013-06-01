package com.devsmart.staple;


import java.util.LinkedList;

import com.devsmart.staple.instructions.Instruction;
import com.devsmart.staple.instructions.LocationFactory;

public class Code {

	public LinkedList<Instruction> mCode = new LinkedList<Instruction>();
	public LocationFactory mLocationFactory = new LocationFactory();
	
	public void add(Instruction instruction) {
		mCode.add(instruction);
		
	}
	
}
