
%option reentrant
%option bison-bridge
%option bison-locations
%option noyywrap
%option yylineno


%{
#include <string>

#include "ParserContext.h"
#include "staple_parser.hpp"

#define YY_EXTRA_TYPE staple::ParserContext*

#define YY_USER_ACTION yylloc->first_line = yylineno;

#define YY_INPUT(buf,result,max_size) \
result = yyextra->readBytes(buf, max_size);

#define SAVE_TOKEN yylval->string = new std::string(yytext, yyleng)
%}

%%

[ \t]                   /* eat whitespace */;
\n                      { yycolumn = 1; }
"if"					return TIF;
"else"					return TELSE;
"return"				return TRETURN;
[a-zA-Z_][a-zA-Z0-9_]*  SAVE_TOKEN; return TID;


"="                     return TEQUAL;
"=="                    return TCEQ;
"!="                    return TCNE;
"<"                     return TCLT;
"<="                    return TCLE;
">"                     return TCGT;
">="                    return TCGE;
"("                     return TLPAREN;
")"                     return TRPAREN;
"{"                     return TLBRACE;
"}"                     return TRBRACE;
"["                     return TLBRACKET;
"]"                     return TRBRACKET;
"."                     return TDOT;
","                     return TCOMMA;
"+"                     return TPLUS;
"-"                     return TMINUS;
"*"                     return TMUL;
"||"                    return TOR;
"&&"                    return TAND;
"/"                     return TDIV;
";"                     return TSEMI;
"!"                     return TNOT;
"..."                   return TELLIPSIS;

.                       printf("Unknown token!\n"); yyterminate();

%%