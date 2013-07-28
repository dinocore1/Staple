package com.devsmart.staple.instructions;

import java.util.Iterator;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class PhiInstruction implements Instruction {
	
	public static class ArgPair {
		public final Operand op;
		public final LabelInstruction label;
		public ArgPair(Operand op, LabelInstruction label){
			this.op = op;
			this.label = label;
		}
	}

	private Location result;
	private ArgPair[] pairs;
	
	public PhiInstruction(Location result, ArgPair[] pairs){
		this.result = result;
		this.pairs = pairs;
	}

	@Override
	public String render(STGroup codegentemplate) {
		ST st = codegentemplate.getInstanceOf("phi");
		st.add("result", RenderHelper.render(codegentemplate, result));
		st.add("args", new ArgPairRenderer(codegentemplate));
		st.add("type", RenderHelper.renderType(codegentemplate, pairs[0].op.getType()));
		
		String retval = st.render();
		return retval;
	}
	
	private class ArgPairRenderer implements Iterator<String> {
		
		private STGroup template;
		private int i = 0;

		ArgPairRenderer(STGroup codegentemplate){
			template = codegentemplate;
			
		}

		@Override
		public boolean hasNext() {
			return i<pairs.length;
		}

		@Override
		public String next() {
			String retval = 
				String.format("[%s, %s]", 
					RenderHelper.render(template, pairs[i].op),
					RenderHelper.render(template, pairs[i].label)
					);
			i++;
			return retval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			
		}
	}

}
