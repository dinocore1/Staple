package com.devsmart.staple.instructions;

import java.util.Arrays;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class ExtractValueInstruction implements Instruction {

	private Location result;
	private Operand obj;
	private List<Integer> index;
	
	public ExtractValueInstruction(Location result, Operand obj, Integer... index) {
		this.result = result;
		this.obj = obj;
		this.index = Arrays.asList(index);
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("extractvalue");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("type", RenderHelper.renderType(codegentemplate, obj.getType()));
		st.add("obj", RenderHelper.render(codegentemplate, obj));
		st.add("indices", index);
		
		return st.render();
	}

}
