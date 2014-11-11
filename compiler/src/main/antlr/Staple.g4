grammar Staple;

compileUnit
    : classDecl
    ;

classDecl
    : 'class' Identifier extendsDecl? '{' (m+=classMemberDecl | f+=classFunctionDecl | c+=classDecl)* '}'
    ;

extendsDecl
    : 'extends' Identifier
    ;

classMemberDecl
    : type Identifier ';'
    ;

classFunctionDecl
    : type Identifier '(' argList ')' block
    ;

argList
    : type Identifier (',' type Identifier)*
    |
    ;


block
    : '{' (statement | localVariableDeclarationStatement)* '}'
    ;

blockStatement
    : localVariableDeclarationStatement
    | statement
    ;

localVariableDeclarationStatement
    : localVariableDeclaration ';'
    ;

localVariableDeclaration
    : type Identifier
    ;

statement
    : block
    |   'if' parExpression statement ('else' statement)?
    |   'for' '(' forControl ')' statement
    |   'while' parExpression statement
    |   'do' statement 'while' parExpression ';'
    |   'switch' parExpression '{' switchBlockStatementGroups '}'
    |   'synchronized' parExpression block
    |   'return' expression? ';'
    |   'throw' expression ';'
    |   'break' Identifier? ';'
    |   'continue' Identifier? ';'
    |   ';'
    |   statementExpression ';'
    ;

statementExpression
    :   expression
    ;

constantExpression
    :   expression
    ;

parExpression
    :   '(' expression ')'
    ;

expressionList
    :   expression (',' expression)*
    ;

forControl
    :   forInit? ';' expression? ';' forUpdate?
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;

forUpdate
    :   expressionList
    ;

switchBlockStatementGroups
    :   (switchBlockStatementGroup)*
    ;

switchBlockStatementGroup
    :   switchLabel+ blockStatement*
    ;

switchLabel
    :   'case' constantExpression ':'
    |   'default' ':'
    ;

expression
    :   conditionalExpression (assignmentOperator expression)?
    ;

assignmentOperator
    :   '='
    |   '+='
    |   '-='
    |   '*='
    |   '/='
    |   '&='
    |   '|='
    |   '^='
    |   '%='
    |   '<<='
    |   '>>='
    ;

conditionalExpression
    :   conditionalOrExpression ( '?' expression ':' conditionalExpression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( '||' conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( '&&' inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( '|' exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( '^' andExpression )*
    ;

andExpression
    :   equalityExpression ( '&' equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( ('==' | '!=') instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression ('instanceof' Identifier)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
    ;

relationalOp
    :   '<='
    |   '>='
    |   '<'
    |   '>'
    ;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
    ;

shiftOp
    :   t1='<' t2='<'
    |   t1='>' t2='>' t3='>'
    |   t1='>' t2='>'
    ;


additiveExpression
    :   multiplicativeExpression ( ('+' | '-') multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( '*' | '/' | '%' ) unaryExpression )*
    ;

unaryExpression
    :   '+' unaryExpression
    |   '-' unaryExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   '~' unaryExpression
    |   '!' unaryExpression
    |   castExpression
    |   primary selector* ('++'|'--')?
    ;

castExpression
    :  '(' primitiveType ')' unaryExpression
    |  '(' (type | expression) ')' unaryExpressionNotPlusMinus
    ;

primary
    :   parExpression
    |   'this' arguments?
    |   'super' superSuffix
    |   literal
    |   'new' c=Identifier arguments
	|   Identifier ('.' Identifier)* identifierSuffix?
    |   primitiveType ('[' ']')* '.' 'class'
    ;


identifierSuffix
    :   ('[' ']')+ '.' 'class'
    |   '[' expression ']'
    |   arguments
    |   '.' 'class'
    |   '.' 'this'
    |   '.' 'super' arguments
    ;

arguments
    :   '(' expressionList? ')'
    ;

selector
    :   '.' Identifier arguments?
    |   '.' 'this'
    |   '.' 'super' superSuffix
    |   '[' expression ']'
    ;

superSuffix
    :   arguments
    |   '.' Identifier arguments?
    ;

literal
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   StringLiteral
    |   BooleanLiteral
    |   'null'
    ;

IntegerLiteral
	:	DecimalIntegerLiteral
	|	HexIntegerLiteral
	|	BinaryIntegerLiteral
	;

FloatingPointLiteral
    : Sign? Digit+ '.' Digit+
    ;

CharacterLiteral
	:	'\'' ~['\\] '\''
	|	'\'' EscapeSequence '\''
	;

DecimalIntegerLiteral
	: '0'
	| Sign? NonZeroDigit Digit*
	;

BooleanLiteral
    : 'true'
    | 'false'
    ;

StringLiteral
	:	'"' StringCharacters? '"'
	;
fragment
StringCharacters
	:	StringCharacter+
	;
fragment
StringCharacter
	:	~["\\]
	|	EscapeSequence
	;

fragment
EscapeSequence
	:	'\\' [btnfr"'\\]
	;

type
    : Identifier POINTER?
    | primitiveType POINTER?
    ;

primitiveType
    : INTTYPE
    | 'bool'
    | 'void'
    | FLOATTYPE
    ;

BOOL : 'bool';
BREAK : 'break';
CASE : 'case';
CLASS : 'class';
CONTINUE : 'continue';
DEFAULT : 'default';
DO : 'do';
ELSE : 'else';
EXTENDS : 'extends';
FLOAT : 'float';
FOR : 'for';
IF : 'if';
GOTO : 'goto';
INSTANCEOF : 'instanceof';
INT : 'int';
NEW : 'new';
PRIVATE : 'private';
PROTECTED : 'protected';
PUBLIC : 'public';
RETURN : 'return';
STATIC : 'static';
SUPER : 'super';
SWITCH : 'switch';
THIS : 'this';
TRY : 'try';
VOID : 'void';
WHILE : 'while';

fragment
Sign
	:	[+-]
	;

fragment
NonZeroDigit
	:	[1-9]
	;

fragment
Digit
    : [0-9]
    ;

fragment
HexIntegerLiteral
    : '0' [xX] HexDigit+
    ;

fragment
HexDigit
	: [0-9a-fA-F]
	;

fragment
BinaryIntegerLiteral
	: '0' [bB] BinaryDigit
	;

fragment
BinaryDigit
    : [01]
    ;

POINTER: '*';

Identifier
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

WS: [ \t\r\n]+ -> skip
	;
