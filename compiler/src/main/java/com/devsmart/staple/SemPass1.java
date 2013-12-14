package com.devsmart.staple;

import com.devsmart.staple.StapleParser.*;
import com.devsmart.staple.symbols.*;
import com.devsmart.staple.types.*;

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
		ClassType type = new ClassType(name);
		mContext.types.add(type);
		mContext.typeTreeProperty.put(ctx, type);
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
