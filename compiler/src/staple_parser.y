%define api.pure
%locations
%defines
%error-verbose
%parse-param { staple::ParserContext* ctx }
%lex-param { void* scanner  }

%{
#include <string>

#include "stdafx.h"
%}

%code requires {
#include "stdafx.h"
}

%union {
	std::string* string;
	staple::Expr* expr;
}

%token TIF TELSE TNOT TSEMI TRETURN TFOR
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TELLIPSIS
%token TPLUS TMINUS TMUL TDIV TAND TOR TBITAND TBITOR
%token <string> TID
%token TINT

%type <expr> expr cmpexpr addexpr mulexpr unaryexpr primary funcall methodcall fieldref arrayref

%right ELSE TELSE

%start stmt

%{
int yylex(YYSTYPE* lvalp, YYLTYPE* llocp, void* scanner);

void yyerror(YYLTYPE* locp, staple::ParserContext* context, const char* err);

#define scanner ctx->mScanner

using namespace staple;
%}

%%


stmt
	: expr TEQUAL expr TSEMI
	| funcall TSEMI
	| methodcall TSEMI
	| vardecl TSEMI
	| TIF TLPAREN expr TRPAREN stmt %prec ELSE
	| TIF TLPAREN expr TRPAREN stmt TELSE stmt
	| TFOR TLPAREN stmt stmt stmt TRPAREN stmt
	| TRETURN expr TSEMI
	| block
	;

vardecl
	: TID TID
	| TID TID TLBRACKET TINT TRBRACKET
	;

block
	: TLBRACE stmt TRBRACE
	;

expr
	: cmpexpr
	;

cmpexpr
	: addexpr TCEQ addexpr
	| addexpr TCNE addexpr
	| addexpr TCLT addexpr
	| addexpr TCLE addexpr
	| addexpr TCGT addexpr
	| addexpr TCGE addexpr
	| addexpr
	;

addexpr
	: mulexpr TPLUS mulexpr { $$ = new Op(Op::Type::ADD, $1, $3); }
	| mulexpr TMINUS mulexpr { $$ = new Op(Op::Type::SUB, $1, $3); }
	| mulexpr
	;

mulexpr
	: unaryexpr TMUL unaryexpr { $$ = new Op(Op::Type::MUL, $1, $3); }
	| unaryexpr TDIV unaryexpr { $$ = new Op(Op::Type::DIV, $1, $3); }
	| unaryexpr
	;

unaryexpr
	: TNOT primary { $$ = new Not($2); }
	| TMINUS primary { $$ = new Neg($2); }
	| primary
	;

primary
	: TLPAREN expr TRPAREN { $$ = $2; }
	| TINT { $$ = new IntLiteral(); }
	| TID { $$ = new Id(*$1); delete $1; }
	| fieldref
	| arrayref
	| funcall
	| methodcall
	;

funcall
	: TID TLPAREN arglist TRPAREN { $$ = new Call(*$1); delete $1; }
	;

methodcall
	: primary TDOT TID TLPAREN arglist TRPAREN
	;

arrayref
	: primary TLBRACKET expr TRBRACKET
	;

fieldref
	: primary TDOT TID
	;

arglist
	: expr
	| arglist TCOMMA expr
	|
	;
