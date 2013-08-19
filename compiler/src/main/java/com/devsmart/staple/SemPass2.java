package com.devsmart.staple;

import java.util.ArrayList;
import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTree;

import com.devsmart.staple.StapleParser.BlockContext;
import com.devsmart.staple.StapleParser.ClassDefinitionContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.FunctionCallContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.IntLiteralContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.LogicExpressionContext;
import com.devsmart.staple.StapleParser.MemberFunctionContext;
import com.devsmart.staple.StapleParser.MemberVarableDeclarationContext;
import com.devsmart.staple.StapleParser.StringLiteralContext;
import com.devsmart.staple.StapleParser.TypeContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.symbols.*;
import com.devsmart.staple.types.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

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
		
		FunctionSymbol symbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		
		//visit return type
		symbol.returnType = visit(ctx.returnType);
		
		//visit formals
		mCurrentScope = mCurrentScope.push();
		visit(ctx.params);
		mCurrentScope = mCurrentScope.pop();
		symbol.parameters = new ArrayList<StapleSymbol>(ctx.params.params.size());
		for(FormalParameterContext paramCtx : ctx.params.params){
			StapleSymbol paramSymbol = mContext.symbolTreeProperties.get(paramCtx);
			symbol.parameters.add(paramSymbol);
		}
		
		symbol.type = new FunctionType(symbol);
		return symbol.type;
	}
	
	@Override
	public StapleType visitGlobalFunction(GlobalFunctionContext ctx) {
		
		FunctionSymbol symbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		
		//visit return type
		symbol.returnType = visit(ctx.returnType);
		
		mCurrentScope = mCurrentScope.push();
		symbol.scope = mCurrentScope;
		
		//visit formals
		visit(ctx.params);
		symbol.parameters = new ArrayList<StapleSymbol>(ctx.params.params.size());
		for(FormalParameterContext paramCtx : ctx.params.params){
			symbol.parameters.add((LocalVarableSymbol) mContext.symbolTreeProperties.get(paramCtx));
		}
		
		visit(ctx.body);
		
		mCurrentScope = mCurrentScope.pop();
		
		symbol.type = new FunctionType(symbol);
		return symbol.type;
	}
	
	@Override
	public StapleType visitMemberFunction(MemberFunctionContext ctx) {
		
		final String functionName = ctx.name.getText();
		FunctionSymbol symbol = new FunctionSymbol(functionName);
		symbol.access = FunctionSymbol.Access.Protected;
		mCurrentScope.define(symbol);
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		//visit return type
		symbol.returnType = visit(ctx.returnType);
		
		mCurrentScope = mCurrentScope.push();
		symbol.scope = mCurrentScope;
		
		//visit formals
		visit(ctx.params);
		symbol.parameters = new ArrayList<StapleSymbol>(ctx.params.params.size());
		for(FormalParameterContext paramCtx : ctx.params.params){
			symbol.parameters.add((LocalVarableSymbol) mContext.symbolTreeProperties.get(paramCtx));
		}
		
		visit(ctx.body);
		
		mCurrentScope = mCurrentScope.pop();
		
		symbol.type = new FunctionType(symbol);
		return symbol.type;
	}
	
	@Override
	public StapleType visitClassDefinition(ClassDefinitionContext ctx) {
		
		ClassSymbol symbol = (ClassSymbol)mContext.symbolTreeProperties.get(ctx);
		
		//visit members
		symbol.members = new ArrayList<MemberVarableSymbol>(ctx.members.size());
		for(MemberVarableDeclarationContext memberCtx : ctx.members){
			visit(memberCtx);
			symbol.members.add((MemberVarableSymbol) mContext.symbolTreeProperties.get(memberCtx));
		}
		
		//visit functions
		symbol.functions = new ArrayList<FunctionSymbol>(ctx.functions.size());
		for(MemberFunctionContext functionCtx : ctx.functions){
			visit(functionCtx);
			symbol.functions.add((FunctionSymbol) mContext.symbolTreeProperties.get(functionCtx));
		}
		
		return symbol.type;
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
	public StapleType visitMemberVarableDeclaration(MemberVarableDeclarationContext ctx) {
		visitChildren(ctx);
		
		StapleType varType = mContext.typeTreeProperty.get(ctx.getChild(0));
		String varName = ctx.getChild(1).getText();
		MemberVarableSymbol varSymbol = new MemberVarableSymbol(varName, varType);
		mContext.symbolTreeProperties.put(ctx, varSymbol);
		mCurrentScope.define(varSymbol);
		
		return PrimitiveType.VOID;
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
		if(symbol == null){
			mContext.errorStream.error("undefined symbol: " + ctx.getText(), ctx.start);
			return null;
		}
		
		mContext.symbolTreeProperties.put(ctx, symbol);
		
		return symbol.getType();
	}

	@Override
	public StapleType visitCompareExpression(CompareExpressionContext ctx) {
		
		StapleType left = visit(ctx.left);
		if(PrimitiveType.INT != left){
			mContext.errorStream.error("operand must be integer type", ctx.left.start);
			return null;
		}
		
		StapleType right = visit(ctx.right);
		if(PrimitiveType.INT != right){
			mContext.errorStream.error("operand must be integer type", ctx.right.start);
			return null;
		}
		
		
		
		mContext.typeTreeProperty.put(ctx, PrimitiveType.BOOL);
		
		return PrimitiveType.BOOL;
	}
	
	@Override
	public StapleType visitLogicExpression(LogicExpressionContext ctx){
		
		StapleType left = visit(ctx.left);
		if(PrimitiveType.BOOL != left){
			mContext.errorStream.error("operand must be bool type", ctx.left.start);
			return null;
		}
		
		StapleType right = visit(ctx.right);
		if(PrimitiveType.BOOL != right){
			mContext.errorStream.error("operand must be bool type", ctx.right.start);
			return null;
		}
		
		
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
	public StapleType visitIntLiteral(IntLiteralContext ctx) {
		mContext.typeTreeProperty.put(ctx, PrimitiveType.INT);
		return PrimitiveType.INT;
	}
	
	@Override
	public StapleType visitFormalParameter(FormalParameterContext ctx) {
		
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
		
		return varType;
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
