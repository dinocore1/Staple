grammar Staple;

compileUnit
	: namespace?  (globalfun+=globalFunction | externalfun+=externalFunction | classDef+=classDefinition)*
	;
	
namespace
	: 'namespace' packages+=ID ('.' packages+=ID)* ';'
	;
	
externalFunction
	: 'extern' returnType=type name=ID params=formalParameters ';'
	;
	
globalFunction
	: returnType=type name=ID params=formalParameters body=functionBody
	;
	
classDefinition
	: 'class' name=ID ('extends' extend=ID)? '{'  (members+=memberVarableDeclaration | functions+=memberFunction)*  '}'
	;
	
memberVarableDeclaration
	: type ID ';'
	;
	
memberFunction
	: returnType=type name=ID params=formalParameters body=functionBody
	;
	
formalParameters
	: '(' params+=formalParameter? (',' params+=formalParameter)* ')'
	;

formalParameter
	: type ID
	| '...'
	;
	
type
	: ID '*'?
	| primitiveType '*'?
	;
	
primitiveType
    : 'bool'
    | 'byte'
    | 'int'
    | 'void'
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
	| expression ('&' | '|' | '^' | '>>' | '<<') expression # mathExpression
	| left=expression ('<=' | '>=' | '>' | '<' | '==' | '!=') right=expression # compareExpression
	| left=expression op=('&&' | '||') right=expression # logicExpression
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
	: INT # intLiteral
	| StringLiteral # stringLiteral
	| booleanLiteral # boolLiteral
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
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|HEX HEX)
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
HEX : [a-zA-Z09];
