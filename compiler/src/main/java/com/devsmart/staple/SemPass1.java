package com.devsmart.staple;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTree;

import com.devsmart.staple.StapleParser.ArgumentsContext;
import com.devsmart.staple.StapleParser.BlockContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.FormalParametersContext;
import com.devsmart.staple.StapleParser.FunctionCallContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.TypeContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.symbols.BlockSymbol;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.MultiVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.FunctionType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.TypeFactory;

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
	public Void visitExternalFunction(ExternalFunctionContext ctx){
		
		String functionName = ctx.getChild(2).getText();
		FunctionSymbol symbol = new FunctionSymbol(functionName);
		symbol.access = FunctionSymbol.Access.External;
		mCurrentScope.define(symbol);
		
		//visit return type
		visit(ctx.getChild(1));
		symbol.returnType = mContext.typeTreeProperty.get(ctx.getChild(1));
		
		
		FormalParametersContext formalParamsNode = (FormalParametersContext)ctx.getChild(3);
		
		//visit formals
		mCurrentScope = mCurrentScope.push();
		visit(formalParamsNode);
		mCurrentScope = mCurrentScope.pop();
		symbol.parameters = new ArrayList<StapleSymbol>(formalParamsNode.params.size());
		for(FormalParameterContext paramCtx : formalParamsNode.params){
			StapleSymbol paramSymbol = mContext.symbolTreeProperties.get(paramCtx);
			symbol.parameters.add(paramSymbol);
		}
		
		symbol.type = new FunctionType(symbol);
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}

	@Override
	public Void visitGlobalFunction(GlobalFunctionContext ctx) {
		
		String functionName = ctx.getChild(1).getText();
		FunctionSymbol symbol = new FunctionSymbol(functionName);
		mCurrentScope.define(symbol);
		
		//visit return type
		visit(ctx.getChild(0));
		symbol.returnType = mContext.typeTreeProperty.get(ctx.getChild(0));
		
		
		
		mCurrentScope = mCurrentScope.push();
		symbol.scope = mCurrentScope;
		
		FormalParametersContext formalParamsNode = (FormalParametersContext)ctx.getChild(2);
		
		//visit formals
		visit(formalParamsNode);
		symbol.parameters = new ArrayList<StapleSymbol>(formalParamsNode.params.size());
		for(FormalParameterContext paramCtx : formalParamsNode.params){
			symbol.parameters.add((LocalVarableSymbol) mContext.symbolTreeProperties.get(paramCtx));
		}
		
		//visit body
		visit(ctx.getChild(3));
		
		
		mCurrentScope = mCurrentScope.pop();
		
		symbol.type = new FunctionType(symbol);
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return null;
	}
	
	
	@Override
	public Void visitBlock(BlockContext ctx) {
		
		mCurrentScope = mCurrentScope.push();
		mContext.symbolTreeProperties.put(ctx, new BlockSymbol(mCurrentScope));
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		
		return null;
	}


	@Override
	public Void visitFormalParameter(FormalParameterContext ctx) {
		
		
		visitChildren(ctx);
		
		StapleType varType = mContext.typeTreeProperty.get(ctx.getChild(0));
		
		ParseTree nameNode = ctx.getChild(1);
		if(nameNode != null){
			String varName = ctx.getChild(1).getText();
			LocalVarableSymbol varSymbol = new LocalVarableSymbol(varName, varType);
			mContext.symbolTreeProperties.put(ctx, varSymbol);
			mCurrentScope.define(varSymbol);
		} else {
			mContext.symbolTreeProperties.put(ctx, new MultiVarableSymbol());
		}
		
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
		
		String baseType = ctx.getChild(0).getText();
		StapleType stpType = TypeFactory.getType(baseType, mCurrentScope);
		
		if(ctx.getChild(1) != null){
			stpType = new PointerType(stpType);
		}
		
		mContext.typeTreeProperty.put(ctx, stpType);

		return null;
	}

	

}
