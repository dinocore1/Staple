grammar Staple;

compileUnit
	: classDecl+
	;

classDecl
	: 'class' ID '{' (memberFunctionDecl | memberVarDecl)* '}'
	;

memberVarDecl
    : type ID ';'
    ;

memberFunctionDecl
	: type ID args block
	;

block
	: '{' stmt* '}'
	;

stmt
    : expr ';'
    | localVarDecl
    | block
    ;

localVarDecl
    : t=type id=ID ';'
    ;

// E x p r e s s i o n s

expr
    : l=expr '=' r=expr # assign
    | l=expr op=('<'|'>'|'>='|'<='|'=='|'!=') r=expr # relation
    | l=expr op=('*'|'/') r=expr # mathOp
    | l=expr op=('+'|'-') r=expr # mathOp
    | '(' expr ')' # expr1
    | v=INT # intLiteral
    | v=ID # symbolReference
    ;


args
	: '(' (type ID (',' type ID)* )?  ')'
	;

type
	: classType
	| 'void'
	| intType
	| 'byte'
	| 'float'
	| 'string'
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