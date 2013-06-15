package com.devsmart.staple;

import java.util.HashMap;

import com.devsmart.staple.StapleParser.BlockContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.FunctionCallContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.StringLiteralContext;
import com.devsmart.staple.StapleParser.TypeContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.symbols.BlockSymbol;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.symbols.StringLiteralSymbol;
import com.devsmart.staple.types.FunctionType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;
import com.devsmart.staple.types.TypeFactory;

public class SemPass2 extends StapleBaseVisitor<StapleType> {

	private CompileContext mContext;
	private Scope mCurrentScope;
	private HashMap<String, StringLiteralSymbol> mStringLiteralMap = new HashMap<String, StringLiteralSymbol>();
	
	public SemPass2(CompileContext context){
		mContext = context;
	}
	
	@Override
	public StapleType visitCompileUnit(CompileUnitContext ctx) {
		mCurrentScope = mContext.globalScope;
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		return null;
	}
	
	@Override
	public StapleType visitExternalFunction(ExternalFunctionContext ctx) {
		FunctionSymbol functionSymbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		return functionSymbol.getType();
	}
	
	@Override
	public StapleType visitGlobalFunction(GlobalFunctionContext ctx) {
		
		FunctionSymbol functionSymbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		mCurrentScope = functionSymbol.scope;
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		
		return functionSymbol.getType();
	}
	
	@Override
	public StapleType visitBlock(BlockContext ctx) {
		
		mCurrentScope = mCurrentScope.push();
		mContext.symbolTreeProperties.put(ctx, new BlockSymbol(mCurrentScope));
		visitChildren(ctx);
		mCurrentScope = mCurrentScope.pop();
		
		return PrimitiveType.VOID;
	}
	
	@Override
	public StapleType visitFunctionCall(FunctionCallContext ctx) {
		
		visitChildren(ctx);
		
		String functionName = ctx.getChild(0).getText();
		StapleSymbol functionSymbol = mCurrentScope.resolve(functionName);
		if(functionSymbol == null){
			mContext.errorStream.error("undefined function: " + functionName, ctx.start);
			return null;
		}
		
		
		if(functionSymbol instanceof FunctionSymbol){
			mContext.symbolTreeProperties.put(ctx, functionSymbol);
			return ((FunctionSymbol)functionSymbol).returnType;
		} else {
			mContext.errorStream.error(functionName + " is not a function", ctx.start);
			return null;
		}
	}
	
	@Override
	public StapleType visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
		
		visitChildren(ctx);
		
		StapleType varType = mContext.typeTreeProperty.get(ctx.getChild(0));
		String varName = ctx.getChild(1).getText();
		LocalVarableSymbol varSymbol = new LocalVarableSymbol(varName, varType);
		mContext.symbolTreeProperties.put(ctx, varSymbol);
		mCurrentScope.define(varSymbol);
		
		
		return PrimitiveType.VOID;
	}
	
	@Override
	public StapleType visitVarRefExpression(VarRefExpressionContext ctx) {
		
		String name = ctx.getText();
		StapleSymbol symbol = mCurrentScope.resolve(name);
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return symbol.getType();
	}

	@Override
	public StapleType visitCompareExpression(CompareExpressionContext ctx) {
		
		visitChildren(ctx);
		mContext.typeTreeProperty.put(ctx, PrimitiveType.BOOL);
		
		return PrimitiveType.BOOL;
	}
	
	@Override
	public StapleType visitStringLiteral(StringLiteralContext ctx){
		
		
		String theStr = ctx.getText();
		theStr = theStr.substring(1, theStr.length()-1);
		
		StringLiteralSymbol symbol = mStringLiteralMap.get(theStr);
		if(symbol == null){
			symbol = new StringLiteralSymbol("localStr" + mStringLiteralMap.size(), theStr);
			mStringLiteralMap.put(theStr, symbol);
		}
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return symbol.getType();
	}
	
	@Override
	public StapleType visitType(TypeContext ctx) {
		
		String baseType = ctx.getChild(0).getText();
		StapleType stpType = TypeFactory.getType(baseType, mCurrentScope);
		
		if(ctx.getChild(1) != null){
			stpType = new PointerType(stpType);
		}
		
		mContext.typeTreeProperty.put(ctx, stpType);

		return stpType;
	}
	
	
}
