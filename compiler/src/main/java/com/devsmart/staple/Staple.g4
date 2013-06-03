grammar Staple;

compileUnit
	: namespace?  functions=globalFunction*
	;
	
namespace
	: 'namespace' packages+=ID ('.' packages+=ID)* ';'
	;
	
globalFunction
	: type name=ID formalParameters functionBody
	;
	
formalParameters
	: '(' params+=formalParameter? (',' params+=formalParameter)* ')'
	;

formalParameter
	: type ID
	;
	
type
	: ID
	| primitiveType
	;
	
primitiveType
    :   'bool'
    |   'char'
    |   'byte'
    |   'int'
    | 	'void'
    ;
    
functionBody
	: '{' statement* '}'
	;
	
block
	: '{' statement* '}'
	;


statement
	: block
	| ifStatement
	| 'for'
	| 'break'
	| localVariableDeclaration ';'
	| returnStatement ';'
	| expression ';'
	;
	
ifStatement
	: 'if' '(' cond=expression ')' pos=statement ('else' neg=statement)?
	;
	
returnStatement
	: 'return' result=expression?
	;
	
localVariableDeclaration
    : type ID ('=' init=variableInitializer)?
    ;
    
variableInitializer
	: expression
	;

expression
	: primary # primaryExpression
	| ID # varRefExpression
	| expression '.' ID # refExpression
	| ID arguments # functionCall
	| expression ('*'|'/') expression # mathExpression
	| expression ('+'|'-') expression # mathExpression
	| expression ('<=' | '>=' | '>' | '<' | '==') expression # compareExpression
	| left=expression '='<assoc=right> right=expression # assignExpression
	;
	
arguments
	: '(' args+=expression? (',' args+=expression)* ')'
	;
	
primary
	: '(' expression ')'
	| literal
	;
	
literal
	: INT
	| StringLiteral
	| booleanLiteral
	;
	
booleanLiteral
    :   'true'
    |   'false'
    ;

// T o k e n s

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;
    
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    ;

WS: [ \t\r\n]+ -> skip ;

COMMENT
    :   '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' -> skip
	;

ID:	LETTER (LETTER|'0'..'9')* ;

fragment
LETTER
	:	'A'..'Z'
	|	'a'..'z'
	|	'_'
	;


INT : '0'..'9'+ ;