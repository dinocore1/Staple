package com.devsmart.staple.instructions;

import java.util.Arrays;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class GetPointerInstruction implements Instruction {

	private Location result;
	private Operand obj;
	private List<Operand> index;
	
	public GetPointerInstruction(Location result, Operand obj, List<Operand> indexs) {
		this.result = result;
		this.obj = obj;
		this.index = indexs;
	}
	
	public GetPointerInstruction(Location result, Operand obj, Operand... index) {
		this.result = result;
		this.obj = obj;
		this.index = Arrays.asList(index);
	}
	
	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("getpointer");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("type", RenderHelper.renderType(codegentemplate, obj.getType()));
		st.add("obj", RenderHelper.render(codegentemplate, obj));
		st.add("indices", new OperandRenderer(codegentemplate, index.iterator()));
		
		return st.render();
	}

}
