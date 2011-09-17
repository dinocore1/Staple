grammar C;
options {
	output=AST;
	ASTLabelType=CTree;
}

tokens {
	FILE; FUNCDEF; EXTERNFUNC; EXTERNVAR; VARDEF; ARRAY; ARGS; ARG;
	EXPR; ELIST; INDEX; CALL; BLOCK; ASSIGN='=';
}

translation_unit
	: external_declaration+ -> ^(FILE external_declaration+)
	;

external_declaration
	:	function_definition -> function_definition
	|	declaration         -> declaration
	;

function_definition
	:	type_specifier ID '(' parameter_list? ')' compound_statement
		-> ^(FUNCDEF ID type_specifier parameter_list? compound_statement)
	;

declaration
	:	type_specifier declarator[$type_specifier.tree] ';'
		-> ^(VARDEF {$declarator.id} declarator)
	;

type_specifier
	: 'void'
	| 'int'
	;

declarator[CTree typeAST] returns [CommonTree id]
	:   ID '[' expressionRoot ']' {$id=new CTree($ID);}
			-> ^(ARRAY {$typeAST} expressionRoot)
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
options {backtrack=true;}
	: compound_statement
	| assignment_expression ';'   -> assignment_expression
	| postfix_expression ';'      -> postfix_expression // handles function calls
	| 'return' expressionRoot ';' -> ^('return' expressionRoot)
	| 'if' '(' expressionRoot ')' s1=statement ('else' s2=statement)?
								  -> ^('if' expressionRoot $s1 $s2?)
	| 'while' '(' expressionRoot ')' statement
								  -> ^('while' expressionRoot statement)
/*
	| 'printf' '(' STRING (',' parameter_list)? ')' ';'
								  -> ^('printf' STRING parameter_list?)
*/
	;

compound_statement
	: '{' declaration* statement* '}' -> ^(BLOCK declaration* statement*)
	;

// E x p r e s s i o n s

assignment_expression
	: postfix_expression ('='^ expressionRoot)?
	;

expressionRoot
	:	expression -> ^(EXPR expression)
	;
	
expression
	:	conditional_expression 
	;

conditional_expression
	:	relational_expression (('=='|'!=')^ relational_expression)?
	;

relational_expression
    :	additive_expression (('<'|'>'|'<='|'>=')^ additive_expression)*
    ;

additive_expression
	:	multiplicative_expression (('+'|'-')^ multiplicative_expression)*
	;

multiplicative_expression
	:	postfix_expression (('*'|'/')^ postfix_expression)*
	;

postfix_expression
	:   (primary_expression->primary_expression)
        (   '[' expression ']'
        			-> ^(INDEX $postfix_expression expression)
        |   '(' argument_expression_list ')'
        			-> ^(CALL $postfix_expression argument_expression_list)
        |   '(' ')'	-> ^(CALL $postfix_expression)
        )*
	;

argument_expression_list
	:   expression (',' expression)* -> ^(ELIST expression+)
	;

primary_expression
	: ID
	| STRING
	| INT
	| '(' expression ')' -> expression
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
