grammar Staple;
options {
	output = AST;
	ASTLabelType = StapleTree;
}

tokens {
	UNIT; PACKAGE; IMPORT; CLASS; FIELDS; METHODS; FUNCTION; FORMALARGS; CALL;
	VARDEF; ASSIGN; BLOCK; FIELDACCESS; THIS; NOT; ARGS; CREATEOBJ; IF; ELSE;
}

@parser::header {
package com.devsmart;
}
@lexer::header {
package com.devsmart;
}

compilationUnit
	: packageDefinition? importDefinition* classDefinition -> ^(UNIT packageDefinition? importDefinition* classDefinition)
	;
	
packageDefinition
	:	'package' classname ';' -> ^(PACKAGE classname)
	;
	
classname
	: ID ('.' ID)* -> ID+
	;

importDefinition
	: 'import' classname ';' -> ^(IMPORT classname)
	;
	
classDefinition
	: 'class' cname=ID
		('extends' sup=ID)?
		'{' 
		( fieldDefinition
		| methodDefinition
		)*
		'}'
		-> {$sup!=null}? ^(CLASS $cname $sup ^(FIELDS fieldDefinition*) ^(METHODS methodDefinition*))
		-> ^(CLASS $cname ID["staple.runtime.object"] ^(FIELDS fieldDefinition*) ^(METHODS methodDefinition*))
		 
	;
	
fieldDefinition
	: typeDefinition ID ';' -> ^(typeDefinition ID)
	;
	
methodDefinition
	: typeDefinition ID '(' formalArgs? ')' block
	  -> ^(FUNCTION ID typeDefinition ^(FORMALARGS formalArgs?) block)
	;
	
formalArgs
	: formalArg (',' formalArg )* -> formalArg+
	;
	
formalArg
	: typeDefinition ID -> ^(typeDefinition ID)
	;
	
typeDefinition
	: 'void' 
	| 'int'
	| 'bool'
	| ID
	;
	
variableDefinition
	: typeDefinition ID -> ^(VARDEF typeDefinition ID)
	;
	
block
	: '{' statement* '}' -> ^(BLOCK statement*)
	;
	
statement
	: block
	| typeDefinition ID 
		( '=' rvalue=expression -> ^(  VARDEF typeDefinition ID ) ^(ASSIGN ID $rvalue ) 
		 | -> ^(VARDEF typeDefinition ID)
		) ';' 
	| lvalue=postfixExpression 
		( '=' rvalue=expression -> ^(ASSIGN $lvalue $rvalue) 
		  |  -> postfixExpression 
		) ';'
	| 'if' '(' expression ')' 
		p=block
	  ('else' e=block)? -> ^(IF expression $p $e?)
	;


expression
	: additive_expression (('=='|'!='|'<'|'>'|'<='|'>=')^ additive_expression)*
	;

additive_expression
	: multiplicative_expression (('+'|'-')^ multiplicative_expression)*
	;

multiplicative_expression
	: unaryExpression (('*'|'/')^ unaryExpression)*
	;
	
unaryExpression
	: '!' unaryExpression -> ^(NOT unaryExpression)
	| postfixExpression
	;
	
primary
	: 'this' -> THIS
	| ID
	| INT
	| StringLiteral
	| 'new' classname arguments -> ^(CREATEOBJ classname arguments)
	;
	
postfixExpression
	: (primary -> primary)
		( '.' ID args=arguments -> ^(CALL $postfixExpression ID $args)
		| '.' p=primary 		-> ^(FIELDACCESS $postfixExpression $p)
		)*
	;
	
arguments
    :   '(' expressionList? ')' -> ^(ARGS expressionList*)
    ;
    
expressionList
    :   expression (',' expression)*
    ;
	

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;
	
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    ;

ID
	: LETTER (LETTER|'0'..'9')*
	;

fragment
LETTER
	: 'A'..'Z'
	| 'a'..'z'
	| '_'
	;

INT : '1'..'9' ('0'..'9')*
	;
	
WS : (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    : '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    ;