package com.devsmart.staple;

import java.io.ObjectOutputStream.PutField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.StapleParser.AssignExpressionContext;
import com.devsmart.staple.StapleParser.CompileUnitContext;
import com.devsmart.staple.StapleParser.FormalParameterContext;
import com.devsmart.staple.StapleParser.GlobalFunctionContext;
import com.devsmart.staple.StapleParser.LiteralContext;
import com.devsmart.staple.StapleParser.LocalVariableDeclarationContext;
import com.devsmart.staple.StapleParser.MathExpressionContext;
import com.devsmart.staple.StapleParser.ReturnStatementContext;
import com.devsmart.staple.StapleParser.VarRefExpressionContext;
import com.devsmart.staple.instructions.AddInstruction;
import com.devsmart.staple.instructions.AllocVariableInstruction;
import com.devsmart.staple.instructions.FunctionDeclareInstruction;
import com.devsmart.staple.instructions.Instruction;
import com.devsmart.staple.instructions.IntLiteral;
import com.devsmart.staple.instructions.LoadInstruction;
import com.devsmart.staple.instructions.Location;
import com.devsmart.staple.instructions.LocationFactory;
import com.devsmart.staple.instructions.MemoryAddress;
import com.devsmart.staple.instructions.MultiplyInstruction;
import com.devsmart.staple.instructions.Operand;
import com.devsmart.staple.instructions.ReturnInstruction;
import com.devsmart.staple.instructions.StoreInstruction;
import com.devsmart.staple.instructions.SymbolReference;
import com.devsmart.staple.instructions.TempLocation;
import com.devsmart.staple.symbols.FunctionSymbol;
import com.devsmart.staple.symbols.LocalVarableSymbol;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.PrimitiveType;
import com.devsmart.staple.types.StapleType;


public class CodeGenerator extends StapleBaseVisitor<Operand> {

	private CompileContext mContext;
	
	private LocationFactory mLocationFactory = new LocationFactory();
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
	
	

	@Override
	public Operand visitCompileUnit(CompileUnitContext ctx) {
		
		pushCodeBlock();
		visitChildren(ctx);
		mContext.code = popCodeBlock();
		
		return null;
	}

	@Override
	public Operand visitGlobalFunction(GlobalFunctionContext ctx) {
		
		FunctionSymbol symbol = (FunctionSymbol) mContext.symbolTreeProperties.get(ctx);
		FunctionDeclareInstruction instruction = new FunctionDeclareInstruction(symbol);
		
		visit(ctx.getChild(2));
		
		pushCodeBlock();
		visit(ctx.getChild(3));
		instruction.body = popCodeBlock();
		
		emit(instruction);
		
		return null;
	}
	
	@Override
	public Operand visitFormalParameter(FormalParameterContext ctx) {
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		
		addLocation(varSymbol, new TempLocation(varSymbol.getName(), varSymbol.type));
		
		return null;
	}
	
	@Override
	public Operand visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx){
		
		LocalVarableSymbol varSymbol = (LocalVarableSymbol) mContext.symbolTreeProperties.get(ctx);
		AllocVariableInstruction instruction = new AllocVariableInstruction(varSymbol);
		emit(instruction);
		
		MemoryAddress address = new MemoryAddress(varSymbol.type);
		
		if(ctx.init != null){
			Operand right = visit(ctx.init);
			
			emit(new StoreInstruction(right, address, varSymbol));
			
			if(right instanceof SymbolReference){
				TempLocation rightTemp = getTempLocation(((SymbolReference) right).symbol);
				
				HashSet<Location> l = new HashSet<Location>();
				l.add(rightTemp);
				addressDescriptor.put(((SymbolReference) right).symbol, l);
			} else if(right instanceof TempLocation){
				addLocation(varSymbol, (Location) right);
			}
		}
		
		addLocation(varSymbol, address);
		
		return null;
	}
	
	
	@Override
	public Operand visitAssignExpression(AssignExpressionContext ctx) {
		
		Operand left = visit(ctx.left);
		SymbolReference symbolRef = (SymbolReference)left;
		MemoryAddress address = getSymbolMemoryAddress(symbolRef.symbol); 
		
		
		Operand right = visit(ctx.right);
		
		if(right instanceof SymbolReference){
			TempLocation rightTemp = getTempLocation(((SymbolReference) right).symbol);
			right = rightTemp;
			
			HashSet<Location> l = new HashSet<Location>();
			l.add(rightTemp);
			addressDescriptor.put(symbolRef.symbol, l);
		} else if(right instanceof TempLocation){
			addLocation(symbolRef.symbol, (Location) right);
		}
		
		emit(new StoreInstruction(right, address, symbolRef.symbol));
		
		return null;
	}

	@Override
	public Operand visitMathExpression(MathExpressionContext ctx) {
		TempLocation retval = null;
		Instruction instruction = null;
		Operand left = visit(ctx.getChild(0));
		Operand right = visit(ctx.getChild(2));
		
		if(left instanceof SymbolReference){
			left = getTempLocation(((SymbolReference) left).symbol);
		}
		if(right instanceof SymbolReference){
			right = getTempLocation(((SymbolReference) right).symbol);
		}
		
		String operation = ctx.getChild(1).getText();
		if("+".equals(operation)){
			retval = mLocationFactory.createTempLocation(PrimitiveType.INT);
			instruction = new AddInstruction(retval, left, right);
		} else if("*".equals(operation)){
			retval = mLocationFactory.createTempLocation(PrimitiveType.INT);
			instruction = new MultiplyInstruction(retval, left, right);
		}
		
		emit(instruction);
		
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
	
	private TempLocation getTempLocation(StapleSymbol symbol){
		TempLocation retval = null;
		MemoryAddress address = null;
		Set<Location> locations = addressDescriptor.get(symbol);
		if(locations != null){
			for(Location l : locations){
				if(l instanceof TempLocation){
					retval = (TempLocation) l;
					break;
				} else if(l instanceof MemoryAddress){
					address = (MemoryAddress) l;
				}
			}
		}
		
		if(retval == null){
			//emit at load
			StapleType type = PrimitiveType.INT;
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
	public Operand visitLiteral(LiteralContext context) {
		Operand retval = null;
		if(context.getStart().getType() == StapleLexer.INT){
			retval = new IntLiteral(Integer.parseInt(context.getText()));
		}
		
		return retval;
		
	}

	public void render(STGroup codegentemplate) {
		
		for(Instruction i : mContext.code){
			System.out.println(i.render(codegentemplate));
		}
		
	}
	
}
