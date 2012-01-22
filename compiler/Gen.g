tree grammar Gen;

options {
    tokenVocab=Staple;
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
    
    public String className;
    
}

translation_unit
	:	^(FILE d+=external_declaration+) 
		-> file(decls={$d}, strings={strings})
	;

external_declaration
	:   class_definition    -> {$class_definition.st}
	|	function_definition	-> {$function_definition.st}
	|	declaration			-> {$declaration.st}
	;

class_definition
@after {currentScope = currentScope.getEnclosingScope();}
	:   ^( CLASSDEF ID {currentScope = (Scope)currentScope.resolve($ID.text);} mem+=declaration* func+=function_definition*  )
		-> def_object(sym={$ID}, members={$mem}, functions={$func})
	;


function_definition
	:	^(	FUNCDEF ID type_specifier 
			(^(ARGS p+=parameter_declaration*) )?
		 	compound_statement
		 )
		 -> def_func(sym={$ID.symbol}, args={$p},
		 			 block={$compound_statement.st})
	;
	
declaration
	:	^(VARDEF ID ^(ARRAY type_specifier expression))
		-> {$ID.symbol.scope instanceof GlobalScope}?
		   def_globalarray(id={$ID.text},
						   type={$type_specifier.text}, size={$expression.text})
		-> def_array(reg={getreg()}, id={$ID.text},
				     type={$type_specifier.text}, size={$expression.st})
	|	^(VARDEF ID type_specifier)
		{
			Boolean isPtr = false;
			if($ID.symbol.type instanceof ClassSymbol){
				isPtr = true;
			}
		}
		
		-> def_var(id={$ID.text}, type={$ID.symbol.type}, ptr={isPtr})
	;

type_tree
	:	^(ARRAY type_tree expression)
	|	type_specifier
	;
	
type_specifier
	: 'void'
	| 'int'
	|  ID
	;

parameter_declaration
	:	^(ARG ID ^(ARRAY type_specifier expression))
		-> def_array(reg={getreg()}, id={$ID.text},
				     type={$type_specifier.text}, size={$expression.st})
	|	^(ARG ID type_specifier)
		{
			Boolean isPtr = false;
			if($ID.symbol.type instanceof ClassSymbol){
				isPtr = true;
			}
		}
		-> def_arg(id={$ID.text}, type={$ID.symbol.type}, ptr={isPtr})
	;

statement
@after {$st.setAttribute("descr", $text.replaceAll("\\n"," "));}
	:	compound_statement
	|	^(ASSIGN id=expr rhs=expr) -> assign(id={$id.st}, rhs={$rhs.st})
	|	call -> {$call.st}
	|	^('return' expression) -> return(v={$expression.st})
	|	^('if' expression s1=statement s2=statement?)
		-> if(cond={$expression.st}, stat1={$s1.st}, stat2={$s2.st}, tmp={getreg()})
	|	^('while' expression s=statement)
		-> while(cond={$expression.st}, stat={$s.st}, tmp={getreg()})
	;

compound_statement
	:	^(BLOCK (d+=declaration | s+=statement)+ ) -> block(decls={$d}, stats={$s})
	;

expression
	:	^(EXPR expr) -> {$expr.st}
	;
	
resolvesymbol
	: ID 
	{
		Symbol sym = currentScope.resolve($ID.text);
		if(sym instanceof VariableSymbol) {
			if(sym.type instanceof ClassSymbol){
				$st = templateLib.getInstanceOf("pointervarable",new STAttrMap().put("id", $ID.text));
			} else if(sym.type instanceof BuiltInTypeSymbol){
				$st = templateLib.getInstanceOf("builtinvarable",new STAttrMap().put("id", $ID.text));
			}
		}
	}
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
	:   ^(CALL fn=ID ( ^(ELIST e+=expr+) )?)
		-> call(reg={getreg()}, sym={$fn.symbol}, args={$e})
	|   ^(OBJCALL ID fn=ID ( ^(ELIST e+=expr+) )?)
		-> call(reg={getreg()}, sym={$fn.symbol}, args={$e})
	;

primary_expression returns [Type type]
	: ID {$type = $ID.symbol.type;}
				-> load_var(reg={getreg()}, id={$ID})
	| STRING 	-> string(reg={getreg()}, s={new CString($STRING.text)},
						  sreg={getstr($STRING.text)})
	| INT {$type = (Type)symtab.globals.resolve("int");}
				-> int(reg={getreg()}, v={$INT.text})
	| ^(DEREF l=primary_expression r=primary_expression) {$type=$r.type;} 
				-> deref(l={$l.st}, r={$r.st})
	| ^(NEW ID ELIST) -> objconst(id={$ID})
	;
