package com.devsmart.staple;

import java.util.LinkedList;
import java.util.List;

import com.devsmart.staple.StapleParser.StructDefinitionContext;

public class StructVisitor extends StapleBaseVisitor<Void> {

	public final List<StructDefinitionContext> structs = new LinkedList<StructDefinitionContext>();
	
	@Override
	public Void visitStructDefinition(StructDefinitionContext ctx) {
		
		structs.add(ctx);
		
		return null;
	}


	

	
	
}
