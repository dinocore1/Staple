package com.devsmart.staple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import com.devsmart.staple.StapleParser.*;
import com.devsmart.staple.instructions.Operand;
import com.devsmart.staple.symbols.*;
import com.devsmart.staple.types.*;

public class SemPass2 extends StapleBaseVisitor<StapleType> {

	private CompileContext mContext;
	private Scope mCurrentScope;
	private HashMap<String, StringLiteralSymbol> mStringLiteralMap = new HashMap<String, StringLiteralSymbol>();
	private ClassType mCurrentClassType;
	
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
		symbol.parameters = new ArrayList<StapleSymbol>(ctx.params.params.size()+1);
		
		//add the 'this' formal
		LocalVarableSymbol thisformal = new LocalVarableSymbol("this", new PointerType(mCurrentClassType));
		mCurrentScope.define(thisformal);
		symbol.parameters.add(thisformal);
		
		
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
		
		mCurrentClassType = (ClassType) mContext.typeTreeProperty.get(ctx);
		
		String extend = ctx.extend == null ? null : ctx.extend.getText();
		
		if(extend != null){
			StapleSymbol extendSymbol = mCurrentScope.resolve(extend);
			if(extendSymbol instanceof ClassSymbol){
				mCurrentClassType.extendsType = (ClassType) extendSymbol.getType();
			} else {
				mContext.errorStream.error("undefined class '" + extend + "'", ctx.extend);
			}
		}
		
		//visit members
		mCurrentClassType.members = new ArrayList<MemberVarableType>(ctx.members.size());
		for(MemberVarableDeclarationContext memberCtx : ctx.members){
			MemberVarableType membertype = (MemberVarableType) visit(memberCtx);
			mCurrentClassType.members.add(membertype);
		}
		
		//visit functions
		mCurrentClassType.functions = new ArrayList<FunctionType>(ctx.functions.size());
		for(MemberFunctionContext functionCtx : ctx.functions){
			FunctionType memberFunctionType = (FunctionType) visit(functionCtx);
			mCurrentClassType.functions.add(memberFunctionType);
		}
		
		return mCurrentClassType;
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
		MemberVarableType varSymbol = new MemberVarableType(varName, varType);
		mContext.typeTreeProperty.put(ctx, varSymbol);
		
		return varSymbol;
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
	public StapleType visitVarRef(VarRefContext ctx) {
		String varName = ctx.name.getText();
		StapleSymbol symbol = mCurrentScope.resolve(varName);
		if(symbol == null){
			mContext.errorStream.error(String.format("No symbol: '%s'", varName), ctx.name);
			return null;
		}
		return symbol.getType();
	}
	

	@Override
	public StapleType visitMemberRef(MemberRefContext ctx) {
		
		StapleType retval = null;
		
		String varName = ctx.m.base.base.getText();
		StapleSymbol symbol = mCurrentScope.resolve(varName);
		if(symbol == null){
			mContext.errorStream.error(String.format("No symbol: '%s'", varName), ctx.name);
			return null;
		}
		if(!(symbol.getType() instanceof ClassType)) {
			mContext.errorStream.error(String.format("'%s' is not a class type", varName), ctx.name);
			return null;
		}
		
		ClassType classType = (ClassType)symbol.getType();
		for(int i=0;i<ctx.m.base.members.size();i++){
			Token memberToken = ctx.m.base.members.get(i);
			MemberVarableType member = classType.getMemberByName(memberToken.getText());
			if(i < ctx.m.base.members.size()-1){
				//should be a class type
				
				if(!(member.getType() instanceof PointerType)){
					mContext.errorStream.error(String.format("'%s' is not a pointer type", memberToken.getText()), memberToken);
					return null;
				} else {
					PointerType pointer = (PointerType) member.getType();
					if(!(pointer.baseType instanceof ClassType)){
						mContext.errorStream.error(String.format("'%s' is not a pointer to a class", memberToken.getText()), memberToken);
						return null;
					}
					classType = (ClassType)pointer.baseType;
				}
			} else {
				retval = member;
				break;
			}
		}
		
		return retval;
	}
	
	@Override
	public StapleType visitMemberRef_p(MemberRef_pContext ctx) {
		return mCurrentClassType;
		
		
	}
	
	@Override
	public StapleType visitDeRef(DeRefContext ctx) {
		
		if(ctx.name != null){
			String name = ctx.name.getText();
			StapleSymbol symbol = mCurrentScope.resolve(name);
			if(symbol == null){
				mContext.errorStream.error("undefined symbol: '" + name + "'", ctx.name);
				return null;
			}
			mContext.symbolTreeProperties.put(ctx, symbol);
			return symbol.getType();
		}
		
		if(ctx.base != null){
			String base = ctx.base.getText();
			StapleSymbol basesymbol = mCurrentScope.resolve(base);
			if(basesymbol == null) {
				mContext.errorStream.error("undefined symbol: '" + base + "'", ctx.base);
				return null;
			}
			if(!(basesymbol instanceof ClassSymbol)){
				mContext.errorStream.error(String.format("'%s' is not a classtype", base), ctx.base);
				return null;
			} else {
				ClassSymbol baseclasssymbol = (ClassSymbol)basesymbol;
				MemberVarableType membersymbol = baseclasssymbol.type.getMemberByName(ctx.member.getText());
				mContext.symbolTreeProperties.put(ctx, new MemberVarableSymbol(ctx.member.getText(), membersymbol));
				return membersymbol.getType();
			}
			
		}
		
		return null;
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
		StapleType stpType = TypeFactory.getType(baseType, mContext);
		
		String astrix = ctx.getChild(1) == null ? null : ctx.getChild(1).getText();
		if(astrix != null && "*".equals(astrix)){
			stpType = new PointerType(stpType);
		}
		
		mContext.typeTreeProperty.put(ctx, stpType);

		return stpType;
	}
	
	
}
