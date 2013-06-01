package com.devsmart.staple;

import java.io.File;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.stringtemplate.v4.STGroup;

import com.devsmart.staple.instructions.Instruction;
import com.devsmart.staple.symbols.StapleSymbol;
import com.devsmart.staple.types.StapleType;

public class CompileContext {

	public ParserRuleContext tree;
	public ParseTreeProperty<StapleSymbol> symbolTreeProperties = new ParseTreeProperty<StapleSymbol>();
	public ParseTreeProperty<StapleType> typeTreeProperty = new ParseTreeProperty<StapleType>();
	public Scope globalScope = new Scope(null);
	public File file;
	public STGroup codegentemplate;
	public List<Instruction> code;
}
