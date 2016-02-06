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

%token TIF TELSE
%token <string> TID

%{
int yylex(YYSTYPE* lvalp, YYLTYPE* llocp, void* scanner);

void yyerror(YYLTYPE* locp, staple::ParserContext* context, const char* err);

#define scanner ctx->mScanner
%}


%%



expr
	: TIF
	;