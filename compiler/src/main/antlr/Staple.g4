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
    | ifStmt
    | forStmt
    ;

ifStmt
    : 'if' '(' c=expr ')' t=stmt ('else' e=stmt)?
    ;

forStmt
    : 'for' '(' (i=expr)? ';' c=expr ';' (n=expr)? ')' block
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
    | l=expr op=('||'|'&&') r=expr # logicOp
    | a=ID ('[' dim+=expr ']')+ # arrayAccess
    | n=ID '(' args+=expr? (',' args+=expr)* ')' # functionCall
    | l=expr '.' r=expr # objectAccess
    | l=expr '=' r=expr # assign
    ;




type
	: 'void'
	| 'bool'
	| intType '[]'?
	| 'byte' '[]'?
	| 'float' '[]'?
	| 'string' '[]'?
	| ID '[]'?
	;

intType
	: 'int' INT?
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