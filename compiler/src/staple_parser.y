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

%type <expr> expr cmpexpr addexpr mulexpr unaryexpr primary

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
	| TIF TLPAREN expr TRPAREN stmt %prec ELSE
	| TIF TLPAREN expr TRPAREN stmt TELSE stmt
	| TFOR TLPAREN stmt stmt stmt TRPAREN stmt
	| TRETURN expr TSEMI
	| block
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
	: mulexpr TPLUS mulexpr
	| mulexpr TMINUS mulexpr
	| mulexpr
	;

mulexpr
	: unaryexpr TMUL unaryexpr { $$ = new Op(Op::Type::MUL, $1, $3); }
	| unaryexpr TDIV unaryexpr
	| unaryexpr
	;

unaryexpr
	: TNOT primary { $$ = new Not($2); }
	| TMINUS primary { $$ = new Neg($2); }
	| primary
	;

primary
	: TLPAREN expr TRPAREN { $$ = $2; }
	| TID { $$ = new Id(*$1); delete $1; }
	;
