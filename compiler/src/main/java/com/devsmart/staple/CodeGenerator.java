package com.devsmart.staple;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.RuleContext;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.StapleParser.ArgumentsContext;
import com.devsmart.staple.StapleParser.AssignExpressionContext;
import com.devsmart.staple.StapleParser.CompareExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.ExpressionContext;
import com.devsmart.staple.StapleParser.ExternalFunctionContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.FunctionCallContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.IfStatementContext;
import com.devsmart.staple.StapleParser.IntLiteralContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.LogicExpressionContext;
import com.devsmart.staple.StapleParser.MathExpressionContext;
import com.devsmart.staple.StapleParser.ReturnStatementContext;
import com.devsmart.staple.StapleParser.StringLiteralContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.instructions.AddInstruction;
import com.devsmart.staple.instructions.AllocVariableInstruction;
import com.devsmart.staple.instructions.BitAndInstruction;
import com.devsmart.staple.instructions.BitOrInstruction;
import com.devsmart.staple.instructions.BitXorInstruction;
import com.devsmart.staple.instructions.BranchInstruction;
import com.devsmart.staple.instructions.DivideInstruction;
import com.devsmart.staple.instructions.ExternalFunctionInstruction;
import com.devsmart.staple.instructions.FunctionCallInstruction;
import com.devsmart.staple.instructions.FunctionDeclareInstruction;
import com.devsmart.staple.instructions.GetPointerInstruction;
import com.devsmart.staple.instructions.Instruction;
import com.devsmart.staple.instructions.IntLiteral;
import com.devsmart.staple.instructions.IntegerCompareInstruction;
import com.devsmart.staple.instructions.LabelFactory;
import com.devsmart.staple.instructions.LabelInstruction;
import com.devsmart.staple.instructions.LoadInstruction;
import com.devsmart.staple.instructions.Location;
import com.devsmart.staple.instructions.LocationFactory;
import com.devsmart.staple.instructions.MemoryAddress;
import com.devsmart.staple.instructions.MultiplyInstruction;
import com.devsmart.staple.instructions.Operand;
import com.devsmart.staple.instructions.Register;
import com.devsmart.staple.instructions.ReturnInstruction;
import com.devsmart.staple.instructions.StoreInstruction;
import com.devsmart.staple.instructions.StringLiteralDeclareInstruction;
import com.devsmart.staple.instructions.SubtractInstruction;
import com.devsmart.staple.instructions.SymbolReference;
import com.devsmart.staple.instructions.SelectInstruction;
import com.devsmart.staple.instructions.*;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.symbols.StringLiteralSymbol;
import com.devsmart.staple.types.ArrayType;
import com.devsmart.staple.types.PointerType;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;


public class CodeGenerator extends StapleBaseVisitor<Operand> {

	private CompileContext mContext;
	
	private LocationFactory mLocationFactory = new LocationFactory();
	private LabelFactory mLabelFactory = new LabelFactory();
	private LinkedList< List<Instruction> > mCodeBlockStack = new LinkedList< List<Instruction> >();
	
	private Map<StapleSymbol, Set<Location> > addressDescriptor = new HashMap<StapleSymbol, Set<Location>>();
	

	public CodeGenerator(CompileContext context) {
		mContext = context;
	}
	
	private void addLocation(StapleSymbol symbol, Location location){
		Set<Location> locations = addressDescriptor.get(symbol);
		if(locations == null){
			locations = new HashSet<Location>();
			addressDescriptor.put(symbol, locations);
		}
		locations.add(location);
	}
	
	private void removeLocation(StapleSymbol symbol, Location location){
		Set<Location> locations = addressDescriptor.get(symbol);
		if(locations != null){
			locations.remove(location);
		}
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
	
	@Override
	public Operand visitExternalFunction(ExternalFunctionContext ctx) {
		
		FunctionSymbol functionSymbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		
		emit(new ExternalFunctionInstruction(functionSymbol));
		
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
	public Operand visitFormalParameter(FormalParameterContext ctx) {
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		Register paramLocation = new Register(varSymbol.getName(), varSymbol.type);
		
		Register tempLocation = mLocationFactory.createTempLocation(varSymbol.type);
		
		emit(new AllocVariableInstruction(tempLocation));
		addLocation(varSymbol, paramLocation);
		
		MemoryAddress address = new MemoryAddress(tempLocation.getName(), tempLocation.getType());
		emit(new StoreInstruction(paramLocation, address));
		addLocation(varSymbol, address);
		
		
		return null;
	}
	
	@Override
	public Operand visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx){
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		AllocVariableInstruction instruction = new AllocVariableInstruction(varSymbol);
		emit(instruction);
		
		MemoryAddress address = new MemoryAddress(varSymbol.getName(), varSymbol.type);
		
		if(ctx.init != null){
			Operand right = visit(ctx.init);
			
			emit(new StoreInstruction(right, address));
			
			if(right instanceof SymbolReference){
				Register rightTemp = getTempLocation(((SymbolReference) right).symbol, true);
				
				HashSet<Location> l = new HashSet<Location>();
				l.add(rightTemp);
				addressDescriptor.put(((SymbolReference) right).symbol, l);
			} else if(right instanceof Register){
				addLocation(varSymbol, (Location) right);
			}
		}
		
		addLocation(varSymbol, address);
		
		return null;
	}
	
	@Override
	public Operand visitFunctionCall(FunctionCallContext ctx) {
		
		ArgumentsContext argsContext = (ArgumentsContext) ctx.getChild(1);
		
		List<Operand> arguments = new ArrayList<Operand>(argsContext.args.size());
		for(ExpressionContext argExp : argsContext.args){
			Operand op = visit(argExp);
			if(op instanceof SymbolReference){
				op = getTempLocation(((SymbolReference) op).symbol, true);
			}
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
		emit(new JumpInstruction(negitiveBlockLabel.name));
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
		
		Operand left = visit(ctx.left);
		SymbolReference symbolRef = (SymbolReference)left;
		
		Location address = getSymbolMemoryAddress(symbolRef.symbol);
		
		Operand right = visit(ctx.right);
		
		if(right instanceof SymbolReference){
			Register rightTemp = getTempLocation(((SymbolReference) right).symbol, true);
			right = rightTemp;
			
			HashSet<Location> l = new HashSet<Location>();
			l.add(rightTemp);
			addressDescriptor.put(symbolRef.symbol, l);
		}
		
		addressDescriptor.remove(symbolRef.symbol);
		addLocation(symbolRef.symbol, address);
		if(right instanceof Register){
			addLocation(symbolRef.symbol, (Location) right);
		}
		
		emit(new StoreInstruction(right, address));
		
		return null;
	}

	@Override
	public Operand visitMathExpression(MathExpressionContext ctx) {
		Register retval = null;
		Instruction instruction = null;
		Operand left = visit(ctx.getChild(0));
		Operand right = visit(ctx.getChild(2));
		
		if(left instanceof SymbolReference){
			left = getTempLocation(((SymbolReference) left).symbol, true);
		}
		if(right instanceof SymbolReference){
			right = getTempLocation(((SymbolReference) right).symbol, true);
		}
		
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
			
			if(left instanceof SymbolReference){
				left = getTempLocation(((SymbolReference) left).symbol, true);
			}
			if(right instanceof SymbolReference){
				right = getTempLocation(((SymbolReference) right).symbol, true);
			}
			
			retval = mLocationFactory.createTempLocation(PrimitiveType.BOOL);
			instruction = new BitAndInstruction(retval, left, right);
			emit(instruction);
		} else if("||".equals(opStr)){
			
			LabelInstruction startblock = mLabelFactory.createLabel();
			
			emit(new JumpInstruction(startblock.name));
			emit(startblock);
			
			//evaluate the left side first, if its not true, then branch and eval right side
			left = visit(ctx.left);
			if(left instanceof SymbolReference){
				left = getTempLocation(((SymbolReference) left).symbol, true);
			}
			
			LabelInstruction evalblock = mLabelFactory.createLabel();
			LabelInstruction endblock = mLabelFactory.createLabel();
			
			emit(new BranchInstruction(left, endblock, evalblock));
			
			pushCodeBlock();
			emit(evalblock);
			right = visit(ctx.right);
			if(right instanceof SymbolReference){
				right = getTempLocation(((SymbolReference) right).symbol, true);
			}
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
	
	private MemoryAddress getSymbolMemoryAddress(StapleSymbol symbol){
		MemoryAddress retval = null;
		
		Set<Location> locations = addressDescriptor.get(symbol);
		if(locations != null){
			for(Location l : locations){
				if(l instanceof MemoryAddress){
					retval = (MemoryAddress) l;
					break;
				}
			}
		}
		
		return retval;
	}
	
	/**
	 * Returns a temporay location for <code>symbol</code>. If <code>createLoadIfNecessary</code>
	 * is true, this method will all emit a new LoadInstruction
	 * 
	 * @see LoadInstruction
	 * @param symbol
	 * @param createLoadIfNecessary
	 * @return
	 */
	private Register getTempLocation(StapleSymbol symbol, boolean createLoadIfNecessary){
		Register retval = null;
		MemoryAddress address = null;
		Set<Location> locations = addressDescriptor.get(symbol);
		if(locations != null){
			for(Location l : locations){
				if(l instanceof Register){
					retval = (Register) l;
					break;
				} else if(l instanceof MemoryAddress){
					address = (MemoryAddress) l;
				}
			}
		}
		
		if(retval == null && createLoadIfNecessary){
			//emit at load
			StapleType type = symbol.getType();
			retval = mLocationFactory.createTempLocation(type);
			LoadInstruction instruction = new LoadInstruction(address, retval, symbol);
			emit(instruction);
			addLocation(symbol, retval);
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
	public Operand visitVarRefExpression(VarRefExpressionContext ctx) {
		StapleSymbol varSymbol = mContext.symbolTreeProperties.get(ctx);
		
		return new SymbolReference(varSymbol);
	}
	
	@Override
	public Operand visitIntLiteral(IntLiteralContext context) {
		Operand retval = new IntLiteral(Integer.parseInt(context.getText()));
		
		return retval;
		
	}
	
	@Override
	public Operand visitStringLiteral(StringLiteralContext ctx) {
		StringLiteralSymbol symbol = (StringLiteralSymbol) mContext.symbolTreeProperties.get(ctx);
		
		Register templocation = getTempLocation(symbol, false);
		if(templocation == null){
			templocation = mLocationFactory.createTempLocation(
					new PointerType( ((ArrayType)symbol.getType()).baseType) );
			
			GetPointerInstruction inst = new GetPointerInstruction(templocation, new SymbolReference(symbol), new IntLiteral(0), new IntLiteral(0));
			emit(inst);
			addLocation(symbol, templocation);
		}
		
		return templocation;
	}

	public void render(STGroup codegentemplate, Writer output) throws IOException {
		
		for(Instruction i : mContext.code){
			output.write(i.render(codegentemplate));
		}
		output.flush();
		
	}
	
}
