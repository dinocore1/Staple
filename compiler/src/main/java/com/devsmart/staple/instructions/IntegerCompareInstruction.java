package com.devsmart.staple.instructions;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.types.PrimitiveType;

public class IntegerCompareInstruction implements Instruction {

	public static enum Operation {
		Equal,
		NotEqual,
		GreaterThan,
		LessThan,
		GreaterThanEqual,
		LessThanEqual
	}
	
	private Operation operation;
	private Location result;
	private Operand left;
	private Operand right;
	
	public IntegerCompareInstruction(Operation operation, Location result, Operand left, Operand right) {
		this.operation = operation;
		this.result = result;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("intcompare");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("operation", renderOperation(codegentemplate));
		st.add("type", RenderHelper.renderType(codegentemplate, PrimitiveType.INT));
		st.add("left", RenderHelper.render(codegentemplate, left));
		st.add("right", RenderHelper.render(codegentemplate, right));
		
		
		return st.render();
	}
	
	private String renderOperation(STGroup codegentemplate) {
		ST st = null;
		switch(operation){
		case Equal:
			st = codegentemplate.getInstanceOf("equalOperator");
			break;
		}
		
		return st.render();
	}

}
