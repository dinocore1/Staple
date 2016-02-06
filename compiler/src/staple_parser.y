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

%union {
	std::string* string;
}

%token TIF TELSE TNOT TSEMI TRETURN
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TELLIPSIS
%token TPLUS TMINUS TMUL TDIV TAND TOR TFOR
%token <string> TID

%right ELSE TELSE

%start stmt

%{
int yylex(YYSTYPE* lvalp, YYLTYPE* llocp, void* scanner);

void yyerror(YYLTYPE* locp, staple::ParserContext* context, const char* err);

#define scanner ctx->mScanner
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
	: compare
	;
	
compare
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
	| mulexpr
	;

mulexpr
	: unaryexpr TMUL unaryexpr
	| unaryexpr TDIV unaryexpr
	| unaryexpr
	;
	
unaryexpr
	: TNOT primary
	| TMINUS primary
	| primary
	;

primary
	: TLPAREN expr TRPAREN
	| TID
	;
	