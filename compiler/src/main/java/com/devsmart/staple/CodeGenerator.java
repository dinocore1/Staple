package com.devsmart.staple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.stringtemplate.v4.STGroup;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import com.devsmart.staple.StapleParser.AssignExpressionContext;
import com.devsmart.staple.StapleParser.ClassDefinitionContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.FunctionCallContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.IfStatementContext;
import com.devsmart.staple.StapleParser.IntLiteralContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.LogicExpressionContext;
import com.devsmart.staple.StapleParser.MathExpressionContext;
import com.devsmart.staple.StapleParser.MemberDeRefContext;
import com.devsmart.staple.StapleParser.MemberDeRef_pContext;
import com.devsmart.staple.StapleParser.MemberFunctionContext;
import com.devsmart.staple.StapleParser.ReturnStatementContext;
import com.devsmart.staple.StapleParser.RvalueContext;
import com.devsmart.staple.StapleParser.StringLiteralContext;
import com.devsmart.staple.StapleParser.VarDeRefContext;
import com.devsmart.staple.StapleParser.VarValueContext;
import com.devsmart.staple.instructions.AddInstruction;
import com.devsmart.staple.instructions.AllocVariableInstruction;
import com.devsmart.staple.instructions.BitAndInstruction;
import com.devsmart.staple.instructions.BitOrInstruction;
import com.devsmart.staple.instructions.BitXorInstruction;
import com.devsmart.staple.instructions.BitcastInstruction;
import com.devsmart.staple.instructions.BranchInstruction;
import com.devsmart.staple.instructions.ClassStructDeclareInstruction;
import com.devsmart.staple.instructions.DivideInstruction;
import com.devsmart.staple.instructions.ExternalFunctionInstruction;
import com.devsmart.staple.instructions.FunctionCallInstruction;
import com.devsmart.staple.instructions.FunctionDeclareInstruction;
import com.devsmart.staple.instructions.GetPointerInstruction;
import com.devsmart.staple.instructions.Instruction;
import com.devsmart.staple.instructions.IntLiteral;
import com.devsmart.staple.instructions.IntegerCompareInstruction;
import com.devsmart.staple.instructions.JumpInstruction;
import com.devsmart.staple.instructions.LabelFactory;
import com.devsmart.staple.instructions.LabelInstruction;
import com.devsmart.staple.instructions.LoadInstruction;
import com.devsmart.staple.instructions.Location;
import com.devsmart.staple.instructions.LocationFactory;
import com.devsmart.staple.instructions.MultiplyInstruction;
import com.devsmart.staple.instructions.Operand;
import com.devsmart.staple.instructions.PhiInstruction;
import com.devsmart.staple.instructions.Register;
import com.devsmart.staple.instructions.ReturnInstruction;
import com.devsmart.staple.instructions.StoreInstruction;
import com.devsmart.staple.instructions.StringLiteralDeclareInstruction;
import com.devsmart.staple.instructions.SubtractInstruction;
import com.devsmart.staple.instructions.SymbolReference;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.MemberVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.symbols.StringLiteralSymbol;
import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.ClassType;
import com.devsmart.staple.types.MemberVarableType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;


public class CodeGenerator extends StapleBaseVisitor<Operand> {

	private CompileContext mContext;
	
	private LocationFactory mLocationFactory = new LocationFactory();
	private LabelFactory mLabelFactory = new LabelFactory();
	private LinkedList< List<Instruction> > mCodeBlockStack = new LinkedList< List<Instruction> >();
	private Set<Pair<StapleSymbol, Register>> valueRegisters = new HashSet<Pair<StapleSymbol, Register>>();
	private Set<Pair<StapleSymbol, Register>> locationRegisters = new HashSet<Pair<StapleSymbol, Register>>();

	public CodeGenerator(CompileContext context) {
		mContext = context;
	}
	
	private void setRegisterValue(Register register, StapleSymbol symbol) {
		//remove any register that previously held this symbol
		Iterator<Pair<StapleSymbol, Register>> it = valueRegisters.iterator();
		while(it.hasNext()){
			Pair<StapleSymbol, Register> p = it.next();
			if(p.a.equals(symbol)){
				it.remove();
			}
		}
		valueRegisters.add(new Pair<StapleSymbol, Register>(symbol, register));
	}
	
	private Register getSymbolValue(StapleSymbol symbol) {
		Register retval = null;
		for(Pair<StapleSymbol, Register> p : valueRegisters){
			if(p.a.equals(symbol)){
				retval = p.b;
				break;
			}
		}
		
		if(retval == null){
			//try a load
			Register location = getSymbolLocation(symbol);
			if(location != null) {
				retval = mLocationFactory.createTempLocation(symbol.getType());
				LoadInstruction load = new LoadInstruction(location, retval);
				emit(load);
				setRegisterValue(retval, symbol);
			}
		}
		
		return retval;
	}
	
	private void setRegisterLocation(Register register, StapleSymbol symbol) {
		//remove any register that previously held this symbol
		Iterator<Pair<StapleSymbol, Register>> it = locationRegisters.iterator();
		while(it.hasNext()){
			Pair<StapleSymbol, Register> p = it.next();
			if(p.a.equals(symbol)){
				it.remove();
			}
		}
		locationRegisters.add(new Pair<StapleSymbol, Register>(symbol, register));
	}
	
	private Register getSymbolLocation(StapleSymbol symbol) {
		Register retval = null;
		for(Pair<StapleSymbol, Register> p : locationRegisters){
			if(p.a.equals(symbol)){
				retval = p.b;
				break;
			}
		}
		return retval;
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
	
	private void collectClassStructs(RuleContext ctx) {
		ClassStructVisitor classCollector = new ClassStructVisitor();
		classCollector.visit(ctx);
		
		for(ClassDefinitionContext classCtx : classCollector.classes){
			ClassType classType = (ClassType) mContext.typeTreeProperty.get(classCtx);
			emit(new ClassStructDeclareInstruction(classType));
		}
		
	}
	
	private Register bitcast(Register r, StapleType toType){
		
		if(r.getType().equals(toType)){
			return r;
		}
		
		Register retval = mLocationFactory.createTempLocation(toType);
		emit(new BitcastInstruction(retval, r));
		
		return retval;
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
		setRegisterValue(new Register(varSymbol.getName(), varSymbol.getType()), varSymbol);
		
		Register location = mLocationFactory.createTempLocation(new PointerType(varSymbol.getType()));
		AllocVariableInstruction instruction = new AllocVariableInstruction(location, 1);
		emit(instruction);
		setRegisterLocation(location, varSymbol);
		
		return null;
	}

	
	@Override
	public Operand visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx){
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register dest = mLocationFactory.createTempLocation(new PointerType(varSymbol.getType()));
		AllocVariableInstruction instruction = new AllocVariableInstruction(dest, 1);
		emit(instruction);
		
		setRegisterLocation(dest, varSymbol);
		
		if(ctx.init != null){
			Register right = (Register) visit(ctx.init);
			emit(new StoreInstruction(right, dest));
			setRegisterValue(right, varSymbol);
		}
		
		return null;
	}
	
	@Override
	public Operand visitFunctionCall(FunctionCallContext ctx) {
		
		List<Operand> arguments = new ArrayList<Operand>(ctx.args.args.size());
		for(RvalueContext argExp : ctx.args.args){
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
	public Operand visitMemberDeRef(MemberDeRefContext ctx) {
		
		MemberVarableSymbol memberVarSymbol = (MemberVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		
		getSymbolValue(memberVarSymbol.baseSymbol);
		Register baseLocation = getSymbolLocation(memberVarSymbol.baseSymbol);
		
		Register result = mLocationFactory.createTempLocation(memberVarSymbol.getType());
		GetPointerInstruction getpointer = new GetPointerInstruction(result, baseLocation, new IntLiteral(0), new IntLiteral(memberVarSymbol.member.getOffset()));
		emit(getpointer);
		
		setRegisterLocation(result, memberVarSymbol);
		
		Operand r = visit(ctx.m);
		if(r != null){
			return r;
		}
		
		return result;
	}
	
	@Override
	public Operand visitMemberDeRef_p(MemberDeRef_pContext ctx) {
		
		Register result = null;
		MemberVarableSymbol memberVarSymbol = (MemberVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		if(memberVarSymbol != null) {
			Register baseLocation = getSymbolValue(memberVarSymbol.baseSymbol);
			//Register baseLocation = getSymbolLocation(memberVarSymbol.baseSymbol);
			
			result = mLocationFactory.createTempLocation(memberVarSymbol.getType());
			GetPointerInstruction getpointer = new GetPointerInstruction(result, baseLocation, new IntLiteral(0), new IntLiteral(memberVarSymbol.member.getOffset()));
			emit(getpointer);
			
			setRegisterLocation(result, memberVarSymbol);
			
			Operand r = visit(ctx.r);
			if(r != null){
				return r;
			}
		}
		return result;
	}
	
	@Override
	public Operand visitVarValue(VarValueContext ctx) {
		StapleSymbol varSymbol = mContext.symbolTreeProperties.get(ctx);
		
		Register r = getSymbolValue(varSymbol);
		return r;
	}
	
	@Override
	public Operand visitVarDeRef(VarDeRefContext ctx) {
		StapleSymbol varSymbol = mContext.symbolTreeProperties.get(ctx);
		Register r = getSymbolLocation(varSymbol);
		return r;
	}
	
	@Override
	public Operand visitAssignExpression(AssignExpressionContext ctx) {
		
		StapleSymbol leftSymbol = mContext.symbolTreeProperties.get(ctx.left);
		
		Operand right = visit(ctx.right);
		
		if(right instanceof Register){
			right = bitcast((Register) right, leftSymbol.getType());
		}
		
		Register left = (Register)visit(ctx.left);
		
		emit(new StoreInstruction(right, left));
		
		setRegisterValue(left, leftSymbol);
		//setRegisterLocation(left, leftSymbol);
		
		
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
	public Operand visitStringLiteral(StringLiteralContext ctx) {
		StringLiteralSymbol symbol = (StringLiteralSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register templocation = mLocationFactory.createTempLocation(
				new PointerType( ((ArrayType)symbol.getType()).baseType) );
		
		GetPointerInstruction inst = new GetPointerInstruction(templocation, new SymbolReference(symbol), new IntLiteral(0), new IntLiteral(0));
		emit(inst);
		setRegisterLocation(templocation, symbol);
		
		return templocation;
	}

	public void render(STGroup codegentemplate, Writer output) throws IOException {
		
		for(Instruction i : mContext.code){
			output.write(i.render(codegentemplate));
		}
		output.flush();
		
	}
	
}
