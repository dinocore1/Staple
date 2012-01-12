grammar Staple;
options {
	output=AST;
	ASTLabelType=CTree;
}

tokens {
	FILE; CLASSDEF; FUNCDEF; EXTERNFUNC; EXTERNVAR; VARDEF; ARRAY; ARGS; ARG;
	EXPR; ELIST; INDEX; CALL; OBJCALL; BLOCK; ASSIGN; DEREF; NEW;
}

translation_unit
	: external_declaration+ -> ^(FILE external_declaration+)
	;

external_declaration
	:   class_definition    -> class_definition
	|	function_definition -> function_definition
	|	declaration         -> declaration
	;
	
class_definition
	:  'class' ID '{' (class_function_definition[$ID.text] | declaration)+ '}'
		-> ^(CLASSDEF ID declaration* class_function_definition*)
	;
	
class_function_definition[String className]
	:	type_specifier ID '(' (parameter_declaration (',' parameter_declaration)*)? ')' block
		-> ^(FUNCDEF ID type_specifier ^(ARGS ^(ARG ID["self"] ID[className]) parameter_declaration*) block)
	;

function_definition
	:	type_specifier ID '(' parameter_list? ')' block
		-> ^(FUNCDEF ID type_specifier parameter_list? block)
	;

declaration
	:	type_specifier declarator[$type_specifier.tree] ';'
		-> ^(VARDEF {$declarator.id} declarator)
	;

type_specifier
	: 'void'
	| 'int'
	|  ID
	;

declarator[CTree typeAST] returns [CommonTree id]
	:   ID '[' expr ']' {$id=new CTree($ID);}
			-> ^(ARRAY {$typeAST} ^(EXPR expr))
	|   ID {$id=new CTree($ID);}
			-> {$typeAST}
	;

parameter_list
	:	parameter_declaration (',' parameter_declaration)*
		-> ^(ARGS parameter_declaration+)
	;

parameter_declaration
	:	type_specifier declarator[$type_specifier.tree]
			-> ^(ARG {$declarator.id} declarator)
	;

// S t a t e m e n t s

statement
	: block
	| (type_specifier ID) => declaration ';' -> declaration
	| l=expr ('=' r=expr -> ^(ASSIGN $l $r) 
				| -> $l
			 ) ';' 
	| 'if' '(' e=expr ')' s1=statement 
		(options {greedy=true;} : 'else' s2=statement)?	-> ^('if' ^(EXPR $e) $s1 $s2?)
	
	
	/*
	| postfix_expression ';'      -> postfix_expression // handles function calls
	| 'return' expressionRoot ';' -> ^('return' expressionRoot)
	
	| 'while' '(' expressionRoot ')' statement
								  -> ^('while' expressionRoot statement)
	| 'return' expressionRoot ';' -> ^('return' expressionRoot)							  
	*/
	;

block
	: '{' statement+ '}' -> ^(BLOCK statement+)
	;

// E x p r e s s i o n s

	
expr
	:	additive_expression (('=='|'!='|'<'|'>'|'<='|'>=')^ additive_expression)*
	;

additive_expression
	:	multiplicative_expression (('+'|'-')^ multiplicative_expression)*
	;

multiplicative_expression
	:	postfix_expression (('*'|'/')^ postfix_expression)*
	;

postfix_expression
	: '-' INT -> INT["-"+$INT.text]
	| INT -> INT
	| '(' expr ')' -> expr
	| lvalue -> lvalue
	| 'new' ID '(' argument_expression_list? ')'  -> ^(NEW ID ^(ELIST argument_expression_list?) ) 
	;

	
lvalue
	: base=ID ( 
				'(' argument_expression_list? ')' -> ^(CALL $base ^(ELIST argument_expression_list?) )
				| lvalue_p[$base] -> lvalue_p
			  ) 
	;

lvalue_p[Token base]
	: '.' n=ID ( 
			'(' argument_expression_list? ')' -> ^(OBJCALL {new CTree($base)} $n ^(ELIST {new CTree($base)} argument_expression_list?))
			| -> ^(DEREF {new CTree($base)} $n) 
			) 
			lvalue_p[base]
	| -> ^({new CTree($base)})
	;

argument_expression_list
	:   expr (',' expr)* -> expr+
	;


// T o k e n s

ID
	:	LETTER (LETTER|'0'..'9')*
	;
	
fragment
LETTER
	:	'A'..'Z'
	|	'a'..'z'
	|	'_'
	;

INT : '0'..'9'+ ;

STRING: '"' .* '"' {setText(getText().substring(1, getText().length()-1));} ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;
