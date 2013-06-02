package com.devsmart.staple.instructions;


public class LabelFactory {

	private int mCounter = 1;
	
	public LabelInstruction createLabel() {
		return new LabelInstruction("l" + String.valueOf(mCounter++));
	}
	
	public void resetLabels() {
		mCounter = 1;
	}

}
