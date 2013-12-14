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
	: 'if' '(' cond=rvalue ')' pos=statement ('else' neg=statement)?
	;
	
returnStatement
	: 'return' result=rvalue?
	;
	
localVariableDeclaration
    : type ID ('=' init=rvalue)?
    ;

expression
	: left=lvalue '='<assoc=right> right=rvalue # assignExpression
	;
	
lvalue
	: name=ID # varRef
	| name=ID m=memberRef_p[{new ClassMemberDeRef($name)}] # memberRef
	;
	
memberRef_p[ClassMemberDeRef base]
	: '.' m=ID {base.members.add($m);} memberRef_p[base]?
	;
	
	
rvalue
	: primary # primaryExpression
	| name=ID # deRef
	| base=ID '.' member=ID # deRef
	| name=ID args=arguments # functionCall
	| rvalue ('*'|'/') rvalue # mathExpression
	| rvalue ('+'|'-') rvalue # mathExpression
	| rvalue ('&' | '|' | '^' | '>>' | '<<') rvalue # mathExpression
	| left=rvalue ('<=' | '>=' | '>' | '<' | '==' | '!=') right=rvalue # compareExpression
	| left=rvalue op=('&&' | '||') right=rvalue # logicExpression
	;
	
arguments
	: '(' args+=rvalue? (',' args+=rvalue)* ')'
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
