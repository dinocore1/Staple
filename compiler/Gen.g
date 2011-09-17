tree grammar Gen;

options {
    tokenVocab=C;
    ASTLabelType=CTree;
    output=template;
}

@members {
    SymbolTable symtab;
    Scope currentScope;
    public Gen(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;
        currentScope = symtab.globals;
    }

    int reg = 1;
    public int getreg() { return reg++; }

    List<CString> strings = new ArrayList<CString>();
    public int getstr(String s) {
    	strings.add(new CString(s));
    	return strings.size();
    }
}

translation_unit
	:	^(FILE d+=external_declaration+) 
		-> file(decls={$d}, strings={strings})
	;

external_declaration
	:	function_definition	-> {$function_definition.st}
	|	declaration			-> {$declaration.st}
	;
		
function_definition
	:	^(	FUNCDEF ID type_specifier 
			(^(ARGS p+=parameter_declaration+) )?
		 	compound_statement
		 )
		 -> def_func(sym={$ID.symbol}, args={$p},
		 			 block={$compound_statement.st})
	;
	
declaration
@after {$st.setAttribute("descr", $text);}
	:	^(VARDEF ID ^(ARRAY type_specifier expression))
		-> {$ID.symbol.scope instanceof GlobalScope}?
		   def_globalarray(id={$ID.text},
						   type={$type_specifier.text}, size={$expression.text})
		-> def_array(reg={getreg()}, id={$ID.text},
				     type={$type_specifier.text}, size={$expression.st})
	|	^(VARDEF ID type_specifier)
		-> def_var(id={$ID.text}, type={$type_specifier.text})
	;

type_tree
	:	^(ARRAY type_tree expression)
	|	type_specifier
	;
	
type_specifier
	: 'void'
	| 'int'
	;

parameter_declaration
//@after { System.out.println("arg: "+$st); }
	:	^(ARG ID ^(ARRAY type_specifier expression))
		-> def_array(reg={getreg()}, id={$ID.text},
				     type={$type_specifier.text}, size={$expression.st})
	|	^(ARG ID type_specifier)
		-> def_arg(id={$ID.text}, type={$type_specifier.text})
	;

statement
@after {$st.setAttribute("descr", $text.replaceAll("\\n"," "));}
	:	compound_statement
	|	^('=' ID expression) -> assign(id={$ID.text}, rhs={$expression.st})
	|	^('=' ^(INDEX ID expr) expression)
		-> assign_array(sym={$ID.symbol}, index={$expr.st},
					    rhs={$expression.st}, tmp1={getreg()}, tmp2={getreg()})
	|	call -> {$call.st}
	|	^('return' expression) -> return(v={$expression.st})
	|	^('if' expression s1=statement s2=statement?)
		-> if(cond={$expression.st}, stat1={$s1.st}, stat2={$s2.st}, tmp={getreg()})
	|	^('while' expression s=statement)
		-> while(cond={$expression.st}, stat={$s.st}, tmp={getreg()})
	;

compound_statement
	:	^(BLOCK d+=declaration* s+=statement*) -> block(decls={$d}, stats={$s})
	;

expression
	:	^(EXPR expr) -> {$expr.st}
	;
	
// try to store return value reg in template so it has code/reg

expr returns [Type type]
@after {
//System.out.println("expr: "+$st);
}
	:	^(('=='|'!='|'+'|'-'|'*'|'/'|'<'|'>'|'<='|'>=') a=expr b=expr)
		-> bop(reg={getreg()}, op={$start.token}, a={$a.st}, b={$b.st})
	|	^(INDEX ID i=expr)
						   -> index(reg={getreg()}, sym={$ID.symbol},
						   			type={$a.type}, index={$i.st},
						   			tmp1={getreg()}, tmp2={getreg()})
	|	call {$call.st.setAttribute("descr", $text);} -> {$call.st}
	|	primary_expression {$type=$primary_expression.type;}
						   -> {$primary_expression.st}
	;

call
	:	{((CTree)input.LT(3)).getText().equals("printf")}?  // 3 not 2 due to DOWN
		^(CALL ID  ( ^(ELIST primary_expression e+=expr*) )?)
		-> printf(format={$primary_expression.st}, args={$e})
	|	^(CALL ID ( ^(ELIST e+=expr+) )?)
		-> call(reg={getreg()}, sym={$ID.symbol}, args={$e})
	;

primary_expression returns [Type type]
	: ID {$type = $ID.symbol.type;}
				-> load_var(reg={getreg()}, id={$ID})
	| STRING 	-> string(reg={getreg()}, s={new CString($STRING.text)},
						  sreg={getstr($STRING.text)})
	| INT {$type = (Type)symtab.globals.resolve("int");}
				-> int(reg={getreg()}, v={$INT.text})
	;
