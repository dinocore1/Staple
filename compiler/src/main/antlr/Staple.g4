grammar Staple;

compileUnit
	: classDecl+
	;

classDecl
	: 'class' n=ID '{' (f+=memberFunctionDecl | m+=memberVarDecl)* '}'
	;

memberVarDecl
    : t=type n=ID ';'
    ;

memberFunctionDecl
	: r=type n=ID '(' args+=arg? (',' args+=arg)* ')' block
	;

arg
	: t=type n=ID
	;

block
	: '{' stmt* '}'
	;

stmt
    : expr ';'
    | localVarDecl
    | block
    | returnStmt
    ;

localVarDecl
    : t=type id=ID ';'
    ;

returnStmt
    : 'return' e=expr ';'
    ;

// E x p r e s s i o n s

expr
    : l=expr op=('*'|'/') r=expr # mathOp
    | l=expr op=('+'|'-') r=expr # mathOp
    | l=expr op=('<'|'>'|'>='|'<='|'=='|'!=') r=expr # relation
    | '(' expr ')' # expr1
    | v=INT # intLiteral
    | v=ID # symbolReference
    | a=ID ('[' dim+=expr ']')+ # arrayAccess
    | l=expr '=' r=expr # assign
    ;




type
	: classType '[]'?
	| 'void'
	| intType '[]'?
	| 'byte' '[]'?
	| 'float' '[]'?
	| 'string' '[]'?
	;

intType
	: 'int' INT?
	;

classType
    : CLASSNAME
    ;

// T o k e n s

STRINGLITERAL
	: '"' (ESCAPE | ~('\\' | '"') )* '"'
	;

fragment
ESCAPE
	: '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\'|HEX HEX)
	;

WS
	: [ \t\r\n]+ -> skip
	;

BLOCKCOMMENT
	: '/*' .*? '*/' -> skip
	;

LINECOMMENT
	: '//' ~('\n' | '\r')* '\r'? '\n' -> skip
	;

ID
	: LETTER (LETTER | '0'..'9')*
	;

fragment
LETTER
	:	'A'..'Z'
	|	'a'..'z'
	|	'_'
	;


INT
	: '0'..'9'+
	;

HEX
	: [a-zA-Z0-9]
	;

CLASSNAME
    : [A-Z][a-zA-Z0-9]*
    ;