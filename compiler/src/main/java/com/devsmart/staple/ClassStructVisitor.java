package com.devsmart.staple;

import java.util.LinkedList;
import java.util.List;

import com.devsmart.staple.StapleParser.ClassDefinitionContext;

public class ClassStructVisitor extends StapleBaseVisitor<Void> {

	public final List<ClassDefinitionContext> classes = new LinkedList<ClassDefinitionContext>();
	
	@Override
	public Void visitClassDefinition(ClassDefinitionContext ctx) {
		classes.add(ctx);
		return null;
	}
}
