tree grammar DefRef;
options {
  tokenVocab = Staple;
  ASTLabelType = CTree;
  filter = true;
}
@members {
    SymbolTable symtab;
    Scope currentScope;
    ErrorStream mErrorStream;
    public DefRef(TreeNodeStream input, SymbolTable symtab, ErrorStream estream) {
        this(input);
        this.symtab = symtab;
        this.mErrorStream = estream;
        currentScope = symtab.globals;
    }
    
    private void undefinedSymbol(int line, String symbolname) {
        mErrorStream.addSymanticError(line, "Undefined Symbol: " + symbolname);
    }
}

topdown
	:   enterClass
    |   enterBlock
    |   enterFunction
    |   varDeclaration
    ;

bottomup
	:   exitClass
    |   exitBlock
    |   exitFunction
    |   idref
    |	call
    ;

// S C O P E S

enterClass
	:   ^(CLASSDEF ID .*) 
		{
		ClassSymbol classSymbol = new ClassSymbol($ID.text, currentScope);
		currentScope.define(classSymbol);
		currentScope = classSymbol;
		}
	;
	
exitClass
	:   CLASSDEF {currentScope = currentScope.getEnclosingScope();}
	;

enterBlock
    :   BLOCK {currentScope = new LocalScope(currentScope);} // push scope
    ;
exitBlock
    :   BLOCK { currentScope = currentScope.getEnclosingScope();} // pop scope
    ;

enterFunction // match method subtree with 0-or-more args
    :   ^((FUNCDEF|EXTERNFUNC) ID type_tree .*) 
        {
        // System.out.println("line "+$ID.getLine()+": def method "+$ID.text);
        Type retType = $type_tree.type; // rule type returns a Type symbol
		FunctionSymbol fs = new FunctionSymbol($ID.text,$type_tree.type,currentScope);
		$ID.symbol = fs;
        currentScope.define(fs); // def method in globals
        currentScope = fs;       // set current scope to method scope
        }
    ;
exitFunction
    :   (FUNCDEF|EXTERNFUNC)
        {
        // System.out.println("args: "+currentScope);
        currentScope = currentScope.getEnclosingScope();// pop arg scope
        }
    ;

type_tree returns [Type type]
	:	^(ARRAY t=type_tree e=.)	{$type = new ArrayType($t.type,$e);}
	|	type_specifier 				{$type = $type_specifier.type;}
	;
	
type_specifier returns [Type type]
@init { $type = (Type)currentScope.resolve($start.getText()); }
	: 'void'
	| 'int'
	|  ID
	;

// D e f i n e  s y m b o l s

varDeclaration // global, parameter, or local variable
    :   ^((VARDEF|EXTERNVAR|ARG) ID type_tree) 
        {
        // System.out.println("line "+$ID.getLine()+": def "+$ID.text+" type "+$type_tree.type);
        VariableSymbol vs = new VariableSymbol($ID.text,$type_tree.type);
        currentScope.define(vs);
        $ID.symbol = vs;
        }
    ;

// R e s o l v e  I D s

call:   ^(CALL ID .)
        {
        Symbol s = currentScope.resolve($ID.text);
        $ID.symbol = s;
        // System.out.println("line "+$ID.getLine()+": call "+s);
        }
    | ^(OBJCALL sobj=ID fn=ID .)
    	{
    	Symbol s = currentScope.resolve($sobj.text);
    	if(s == null) {
    	  undefinedSymbol($sobj.getLine(), $sobj.text);
    	} else {
    	  s = currentScope.resolve(s.type.getName());
    	  if(s == null){
    	  	mErrorStream.addSymanticError($sobj.getLine(), "Undefined class '" + s.type.getName() + "'");
    	  } else {
    	  	s = ((Scope)s).resolve($fn.text);
    	  	if(s == null) {
    	  		mErrorStream.addSymanticError($fn.getLine(), "Class '" + s.type.getName() + "' has undefined function '" + $fn.text);  
    	  	} else {
    	  		$fn.symbol = s;
    	  	}
    	    
    	  }
    	}
        
    	}
	;
	
idref
    :   {$start.hasAncestor(EXPR) || $start.hasAncestor(ASSIGN) ||
    	 $start.hasAncestor(ELIST)}? ID // only match IDs in expressions
        {
        Symbol s = currentScope.resolve($ID.text);
        $ID.symbol = s;
        // System.out.println("line "+$ID.getLine()+": ref "+s);
        }
    ;
