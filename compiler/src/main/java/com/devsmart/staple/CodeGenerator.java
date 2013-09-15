package com.devsmart.staple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.instructions.*;
import com.devsmart.staple.StapleParser.*;
import com.devsmart.staple.symbols.*;
import com.devsmart.staple.types.*;


public class CodeGenerator extends StapleBaseVisitor<Operand> {

	private CompileContext mContext;
	
	private LocationFactory mLocationFactory = new LocationFactory();
	private LabelFactory mLabelFactory = new LabelFactory();
	private LinkedList< List<Instruction> > mCodeBlockStack = new LinkedList< List<Instruction> >();
	
	
	private Set<Pair<StapleSymbol, Register>> registers = new HashSet<Pair<StapleSymbol, Register>>();
	

	public CodeGenerator(CompileContext context) {
		mContext = context;
	}
	
	private void addLocation(StapleSymbol symbol, Register register){
		registers.add(new Pair<StapleSymbol, Register>(symbol, register));
	}
	
	private Register getValueOf(StapleSymbol symbol){
		
		Register pointer = null;
		Pair<StapleSymbol, Register> pair;
		Iterator<Pair<StapleSymbol, Register>> it = registers.iterator();
		
		while(it.hasNext()){
			pair = it.next();
			if(pair.a.equals(symbol)){
				if(symbol.getType() instanceof PointerType &&
						pair.b.getType() instanceof PointerType){
					return pair.b;
				} else {
					pointer = pair.b;
				}
			}
		}
		
		if(pointer != null){
			Register r = mLocationFactory.createTempLocation(symbol.getType());
			emit(new LoadInstruction(pointer, r));
			registers.add(new Pair<StapleSymbol, Register>(symbol, r));
			return r;
		}
		
		return null;
	}
	
	private Location getPointerTo(StapleSymbol symbol){
		Pair<StapleSymbol, Register> pair;
		Iterator<Pair<StapleSymbol, Register>> it = registers.iterator();
		
		while(it.hasNext()){
			pair = it.next();
			if(pair.a.equals(symbol)){
				if((pair.b.getType() instanceof PointerType)){
					return pair.b;
				}
			}
		}
		
		return null;
	}
	

	/**
	 * pushes a new codeblock on the stack. Returns the previous top of the stack
	 * @return
	 */
	private List<Instruction> pushCodeBlock() {
		List<Instruction> last = mCodeBlockStack.peek();
		mCodeBlockStack.push(new LinkedList<Instruction>());
		return last;
	}
	
	private List<Instruction> popCodeBlock() {
		return mCodeBlockStack.pop();
	}
	
	private void emit(Instruction i) {
		mCodeBlockStack.peek().add(i);
	}
	
	private void emit(List<Instruction> instructions){
		mCodeBlockStack.peek().addAll(instructions);
	}
	

	@Override
	public Operand visitCompileUnit(CompileUnitContext ctx) {
		
		pushCodeBlock();
		collectStringLiterals(ctx);
		collectStructs(ctx);
		collectClassStructs(ctx);
		visitChildren(ctx);
		mContext.code = popCodeBlock();
		
		return null;
	}
	
	private void collectStringLiterals(RuleContext ctx) {
		StringLiteralVisitor strLiteralCollector = new StringLiteralVisitor();
		strLiteralCollector.visit(ctx);
		
		Set<StringLiteralSymbol> symbolSet = new HashSet<StringLiteralSymbol>();
		
		for(StringLiteralContext slc : strLiteralCollector.strings){
			symbolSet.add((StringLiteralSymbol)mContext.symbolTreeProperties.get(slc));
		}
		
		for(StringLiteralSymbol symbol : symbolSet){
			emit(new StringLiteralDeclareInstruction( symbol ));
		}
	}
	
	private void collectStructs(RuleContext ctx){
		StructVisitor structCollector = new StructVisitor();
		structCollector.visit(ctx);
		
		for(StructDefinitionContext structCtx : structCollector.structs){
			StructSymbol structSym = (StructSymbol) mContext.symbolTreeProperties.get(structCtx);
			emit(new StructDeclarationInstruction(structSym));
		}
	}
	
	private void collectClassStructs(RuleContext ctx) {
		ClassStructVisitor classCollector = new ClassStructVisitor();
		classCollector.visit(ctx);
		
		for(ClassDefinitionContext classCtx : classCollector.classes){
			ClassSymbol classSym = (ClassSymbol) mContext.symbolTreeProperties.get(classCtx);
			emit(new ClassStructDeclareInstruction(classSym));
		}
		
	}
	
	@Override
	public Operand visitClassDefinition(ClassDefinitionContext ctx) {
		
		for(MemberFunctionContext method : ctx.functions){
			visit(method);
		}
		
		return null;
	}

	@Override
	public Operand visitGlobalFunction(GlobalFunctionContext ctx) {
		
		mLocationFactory.resetTemps();
		
		FunctionSymbol symbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		FunctionDeclareInstruction instruction = new FunctionDeclareInstruction(symbol);
		
		pushCodeBlock();
		
		visit(ctx.getChild(2));
		
		visit(ctx.getChild(3));
		instruction.body = popCodeBlock();
		
		emit(instruction);
		
		return null;
	}

	@Override
	public Operand visitExternalFunction(ExternalFunctionContext ctx) {
		
		FunctionSymbol functionSymbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		
		emit(new ExternalFunctionInstruction(functionSymbol));
		
		return null;
	}

	@Override
	public Operand visitMemberFunction(MemberFunctionContext ctx) {
		
		mLocationFactory.resetTemps();
		
		FunctionSymbol symbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		FunctionDeclareInstruction instruction = new FunctionDeclareInstruction(symbol);
		
		pushCodeBlock();
		
		visit(ctx.getChild(2));
		
		visit(ctx.getChild(3));
		instruction.body = popCodeBlock();
		
		emit(instruction);
		
		return null;
	}
	
	@Override
	public Operand visitFormalParameter(FormalParameterContext ctx) {
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		addLocation(varSymbol, new Register(varSymbol.getName(), varSymbol.getType()));
		
		Register dest = mLocationFactory.createTempLocation(new PointerType(varSymbol.getType()));
		AllocVariableInstruction instruction = new AllocVariableInstruction(dest, 1);
		emit(instruction);
		addLocation(varSymbol, dest);
		
		return null;
	}
	
	@Override
	public Operand visitVarRefExpression(VarRefExpressionContext ctx) {
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register retval = getValueOf(varSymbol);
		
		return retval;
	}
	
	@Override
	public Operand visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx){
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register dest = mLocationFactory.createTempLocation(new PointerType(varSymbol.getType()));
		AllocVariableInstruction instruction = new AllocVariableInstruction(dest, 1);
		emit(instruction);
		
		addLocation(varSymbol, dest);
		
		if(ctx.init != null){
			Operand right = visit(ctx.init);
			emit(new StoreInstruction(right, dest));
		}
		
		return null;
	}
	
	@Override
	public Operand visitFunctionCall(FunctionCallContext ctx) {
		
		ArgumentsContext argsContext = (ArgumentsContext) ctx.getChild(1);
		
		List<Operand> arguments = new ArrayList<Operand>(argsContext.args.size());
		for(ExpressionContext argExp : argsContext.args){
			Operand op = visit(argExp);
			arguments.add(op);
		}
		
		FunctionSymbol targetSymbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register result = null;
		if(targetSymbol.returnType != PrimitiveType.VOID){
			result = mLocationFactory.createTempLocation(targetSymbol.returnType);
		}
		FunctionCallInstruction instruction = new FunctionCallInstruction(targetSymbol, result, arguments);
		
		emit(instruction);
		
		return result;
	}
	
	@Override
	public Operand visitIfStatement(IfStatementContext ctx) {
		
		Operand cond = visit(ctx.cond);
		
		LabelInstruction positiveBlockLabel = mLabelFactory.createLabel();
		LabelInstruction negitiveBlockLabel = mLabelFactory.createLabel();
		
		pushCodeBlock();
		emit(positiveBlockLabel);
		visit(ctx.pos);
		
		/* SPECIAL CASE - if the last emitted instruction was a return, then cannot emit a jump */
		List<Instruction> code = mCodeBlockStack.peek();
		if(!(code.get(code.size()-1) instanceof ReturnInstruction)){
			emit(new JumpInstruction(negitiveBlockLabel.name));
		}
		
		List<Instruction> positiveBasicBlock = popCodeBlock();
		

		pushCodeBlock();
		emit(negitiveBlockLabel);
		if(ctx.neg != null){
			visit(ctx.neg);
		}
		List<Instruction> negitiveCodeBlock = popCodeBlock();
		
		
		BranchInstruction branchInstruction = new BranchInstruction(cond, positiveBlockLabel, negitiveBlockLabel);
		emit(branchInstruction);
		
		emit(positiveBasicBlock);
		if(negitiveCodeBlock != null){
			emit(negitiveCodeBlock);
		}
		
		return null;
	}
	
	@Override
	public Operand visitCompareExpression(CompareExpressionContext ctx) {
		
		IntegerCompareInstruction.Operation operation = IntegerCompareInstruction.Operation.Equal;
		Operand left = visit(ctx.getChild(0));
		Operand right = visit(ctx.getChild(2));
		
		
		String opStr = ctx.getChild(1).getText();
		if("==".equals(opStr)){
			operation = IntegerCompareInstruction.Operation.Equal;
		} else if("!=".equals(opStr)){
			operation = IntegerCompareInstruction.Operation.NotEqual;
		} else if(">".equals(opStr)){
			operation = IntegerCompareInstruction.Operation.GreaterThan;
		} else if("<".equals(opStr)){
			operation = IntegerCompareInstruction.Operation.LessThan;
		} else if(">=".equals(opStr)) {
			operation = IntegerCompareInstruction.Operation.GreaterThanEqual;
		} else if("<=".equals(opStr)) {
			operation = IntegerCompareInstruction.Operation.LessThanEqual;
		}
		
		Operand result = mLocationFactory.createTempLocation(PrimitiveType.BOOL);
		IntegerCompareInstruction instruction = new IntegerCompareInstruction(operation, (Location) result, left, right);
		emit(instruction);
		
		return result;
	}
	
	@Override
	public Operand visitAssignExpression(AssignExpressionContext ctx) {
		
		Register left = (Register) visit(ctx.left);
		Register right = (Register) visit(ctx.right);
		
		emit(new StoreInstruction(right, left));
		
		StapleSymbol symbol = null;
		Iterator<Pair<StapleSymbol, Register>> it = registers.iterator();
		while(it.hasNext()){
			Pair<StapleSymbol, Register> p = it.next();
			if(p.b.equals(left)){
				it.remove();
			}
			if(p.b.equals(right)){
				symbol = p.a;
			}
		}
		
		if(symbol != null){
			addLocation(symbol, left);
		}
		
		
		return null;
	}

	@Override
	public Operand visitMathExpression(MathExpressionContext ctx) {
		Register retval = null;
		Instruction instruction = null;
		Operand left = visit(ctx.getChild(0));
		Operand right = visit(ctx.getChild(2));
		
		String operation = ctx.getChild(1).getText();
		retval = mLocationFactory.createTempLocation(PrimitiveType.INT);
		if("+".equals(operation)){
			instruction = new AddInstruction(retval, left, right);
		} else if("*".equals(operation)){
			instruction = new MultiplyInstruction(retval, left, right);
		} else if("-".equals(operation)){
			instruction = new SubtractInstruction(retval, left, right);
		} else if("/".equals(operation)){
			instruction = new DivideInstruction(retval, left, right);
		} else if("&".equals(operation)){
			instruction = new BitAndInstruction(retval, left, right);
		} else if("|".equals(operation)){
			instruction = new BitOrInstruction(retval, left, right);
		} else if("^".equals(operation)){
			instruction = new BitXorInstruction(retval, left, right);
		}
		
		emit(instruction);
		
		return retval;
	}
	
	@Override
	public Operand visitLogicExpression(LogicExpressionContext ctx) {
		
		Register retval = null;
		Instruction instruction = null;
		
		Operand left = null;
		Operand right = null;
		
		String opStr = ctx.op.getText();
		if("&&".equals(opStr)){
			left = visit(ctx.left);
			right = visit(ctx.right);
			
			retval = mLocationFactory.createTempLocation(PrimitiveType.BOOL);
			instruction = new BitAndInstruction(retval, left, right);
			emit(instruction);
		} else if("||".equals(opStr)){
			
			LabelInstruction startblock = mLabelFactory.createLabel();
			
			emit(new JumpInstruction(startblock.name));
			emit(startblock);
			
			//evaluate the left side first, if its not true, then branch and eval right side
			left = visit(ctx.left);
			
			LabelInstruction evalblock = mLabelFactory.createLabel();
			LabelInstruction endblock = mLabelFactory.createLabel();
			
			emit(new BranchInstruction(left, endblock, evalblock));
			
			pushCodeBlock();
			emit(evalblock);
			right = visit(ctx.right);
			emit(new JumpInstruction(endblock.name));
			List<Instruction> evalBasicBlock = popCodeBlock();
			
			
			pushCodeBlock();
			emit(endblock);
			retval = mLocationFactory.createTempLocation(PrimitiveType.BOOL);
			emit(new PhiInstruction(retval, new PhiInstruction.ArgPair[]{
					new PhiInstruction.ArgPair(left, startblock),
					new PhiInstruction.ArgPair(right, evalblock)
			}));
			List<Instruction> endBasicBlock = popCodeBlock();
			
			emit(evalBasicBlock);
			emit(endBasicBlock);
			
		}
		
		
		return retval;
		
	}
	
	@Override
	public Operand visitReturnStatement(ReturnStatementContext ctx) {
		
		Operand res = null;
		if(ctx.result != null){
			res = visit(ctx.result);
		}
		
		emit(new ReturnInstruction(res));
		
		return null;
	}
	
	@Override
	public Operand visitIntLiteral(IntLiteralContext context) {
		Operand retval = new IntLiteral(Integer.parseInt(context.getText()));
		
		return retval;
		
	}
	
	@Override
	public Operand visitDeRefExpression(DeRefExpressionContext ctx) {
		
		Operand left = visit(ctx.l);
		
		DeRefHelper derefhelper = (DeRefHelper)mContext.helperTreeProperties.get(ctx);
		
		Register templocation = mLocationFactory.createTempLocation(derefhelper.getMemberVarableSymbol().getType());
		
		
		
		Instruction inst;
		if(left.getType() instanceof PointerType){
			inst = new GetPointerInstruction(templocation, left, new IntLiteral(0), new IntLiteral(derefhelper.getOffset()));
		} else {
			inst = new ExtractValueInstruction(templocation, left, derefhelper.getOffset());
		}
		emit(inst);
		
		return templocation;
	}
	
	@Override
	public Operand visitStringLiteral(StringLiteralContext ctx) {
		StringLiteralSymbol symbol = (StringLiteralSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register templocation = mLocationFactory.createTempLocation(
				new PointerType( ((ArrayType)symbol.getType()).baseType) );
		
		GetPointerInstruction inst = new GetPointerInstruction(templocation, new SymbolReference(symbol), new IntLiteral(0), new IntLiteral(0));
		emit(inst);
		addLocation(symbol, templocation);
		
		return templocation;
	}

	public void render(STGroup codegentemplate, Writer output) throws IOException {
		
		for(Instruction i : mContext.code){
			output.write(i.render(codegentemplate));
		}
		output.flush();
		
	}
	
}
