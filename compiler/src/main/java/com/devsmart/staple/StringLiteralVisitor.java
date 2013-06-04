package com.devsmart.staple;

import java.util.LinkedList;
import java.util.List;

import com.devsmart.staple.StapleParser.StringLiteralContext;

public class StringLiteralVisitor extends StapleBaseVisitor<Void> {

	public final List<StringLiteralContext> strings = new LinkedList<StringLiteralContext>();
	
	@Override
	public Void visitStringLiteral(StringLiteralContext ctx) {
		
		strings.add(ctx);
		
		return null;
	}


	

	
	
}
