grammar Staple;

compileUnit
    : classDecl+
    ;

classDecl
    : 'class' ID extendsDecl? '{' (m+=classMemberDecl | f+=classFunctionDecl)* '}'
    ;

extendsDecl
    : 'extends' ID
    ;

classMemberDecl
    : type ID ';'
    ;

classFunctionDecl
    : linkage? type ID '(' argList ')'
    ;

argList
    : type ID
    | type ID (',' type ID)+
    ;

linkage
    : 'public'
    | 'static'
    ;

type
    : 'void'
    | INTTYPE
    | 'bool'
    | FLOATTYPE
    | ID
    ;



WS
	: [ \t\r\n]+ -> skip
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

FLOATTYPE : 'float' FLOATSIZE? ;

fragment
FLOATSIZE
    : '32'
    | '64'
    ;

INTTYPE
    : 'uint' INTSIZE?
    | 'int' INTSIZE?
    ;

fragment
INTSIZE
    : '8'
    | '16'
    | '32'
    | '64'
    ;