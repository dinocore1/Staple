package com.devsmart.staple;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTree;

import com.devsmart.staple.StapleParser.BlockContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.FormalParametersContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.TypeContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.TypeFactory;

public class SemPass1Listener extends StapleBaseVisitor<Void> {
	
	private CompileContext mContext;
	private Scope mCurrentScope;

	public SemPass1Listener(CompileContext context) {
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
	public Void visitGlobalFunction(GlobalFunctionContext ctx) {
		
		String functionName = ctx.getChild(1).getText();FunctionSymbol symbol = new FunctionSymbol(functionName);
		mCurrentScope.define(symbol);
		
		//visit return type
		visit(ctx.getChild(0));
		symbol.returnType = mContext.typeTreeProperty.get(ctx.getChild(0));
		
		
		
		mCurrentScope = mCurrentScope.push();
		symbol.scope = mCurrentScope;
		
		FormalParametersContext formalParamsNode = (FormalParametersContext)ctx.getChild(2);
		
		//visit formals
		visit(formalParamsNode);
		symbol.parameters = new ArrayList<LocalVarableSymbol>(formalParamsNode.params.size());
		for(FormalParameterContext paramCtx : formalParamsNode.params){
			symbol.parameters.add((LocalVarableSymbol) mContext.symbolTreeProperties.get(paramCtx));
		}
		
		//visit body
		visit(ctx.getChild(3));
		
		
		mCurrentScope = mCurrentScope.pop();
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}
	
	@Override
	public Void visitBlock(BlockContext ctx) {
		
		mCurrentScope = mCurrentScope.push();
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		
		return null;
	}


	@Override
	public Void visitFormalParameter(FormalParameterContext ctx) {
		
		visitChildren(ctx);
		
		StapleType varType = mContext.typeTreeProperty.get(ctx.getChild(0));
		String varName = ctx.getChild(1).getText();
		LocalVarableSymbol varSymbol = new LocalVarableSymbol(varName, varType);
		mContext.symbolTreeProperties.put(ctx, varSymbol);
		mCurrentScope.define(varSymbol);
		
		return null;
	}
	

	@Override
	public Void visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
		
		visitChildren(ctx);
		
		StapleType varType = mContext.typeTreeProperty.get(ctx.getChild(0));
		String varName = ctx.getChild(1).getText();
		LocalVarableSymbol varSymbol = new LocalVarableSymbol(varName, varType);
		mContext.symbolTreeProperties.put(ctx, varSymbol);
		mCurrentScope.define(varSymbol);
		
		
		return null;
	}
	
	@Override
	public Void visitVarRefExpression(VarRefExpressionContext ctx) {
		
		String name = ctx.getText();
		StapleSymbol symbol = mCurrentScope.resolve(name);
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}

	@Override
	public Void visitCompareExpression(CompareExpressionContext ctx) {
		
		visitChildren(ctx);
		mContext.typeTreeProperty.put(ctx, PrimitiveType.BOOL);
		
		return null;
	}

	@Override
	public Void visitType(TypeContext ctx) {
		
		visitChildren(ctx);
		
		String typeStr = ctx.getText();
		StapleType stpType = TypeFactory.getType(typeStr, mCurrentScope);
		mContext.typeTreeProperty.put(ctx, stpType);

		return null;
	}

	

}
