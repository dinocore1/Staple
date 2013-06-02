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
	| 'if'
	| 'for'
	| 'break'
	| localVariableDeclaration ';'
	| returnStatement ';'
	| expression ';'
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
	| expression ('*'|'/') expression # mathExpression
	| expression ('+'|'-') expression # mathExpression
	| left=expression '='<assoc=right> right=expression # assignExpression
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