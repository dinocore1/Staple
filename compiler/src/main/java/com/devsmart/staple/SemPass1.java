package com.devsmart.staple;

import com.devsmart.staple.StapleParser.ClassDefinitionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.symbols.ClassSymbol;
import com.devsmart.staple.symbols.FunctionSymbol;

public class SemPass1 extends StapleBaseVisitor<Void> {
	
	private CompileContext mContext;
	private Scope mCurrentScope;

	public SemPass1(CompileContext context) {
		mContext = context;
		
	}
	
	@Override
	public Void visitCompileUnit(CompileUnitContext ctx) {
		mCurrentScope = mContext.globalScope;
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		return null;
	}
	
	@Override
	public Void visitClassDefinition(ClassDefinitionContext ctx) {
		
		String name = ctx.name.getText();
		ClassSymbol symbol = new ClassSymbol(name);
		mCurrentScope.define(symbol);
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}

	@Override
	public Void visitExternalFunction(ExternalFunctionContext ctx) {
		
		String functionName = ctx.name.getText();
		FunctionSymbol symbol = new FunctionSymbol(functionName);
		symbol.access = FunctionSymbol.Access.External;
		mCurrentScope.define(symbol);
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}

	@Override
	public Void visitGlobalFunction(GlobalFunctionContext ctx) {
		
		String functionName = ctx.name.getText();
		FunctionSymbol symbol = new FunctionSymbol(functionName);
		mCurrentScope.define(symbol);
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}


	

}
