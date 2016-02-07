
%option reentrant
%option bison-bridge
%option bison-locations
%option noyywrap
%option yylineno


%{

#include "stdafx.h"

#define YY_EXTRA_TYPE staple::ParserContext*

#define YY_USER_ACTION yylloc->first_line = yylineno;

#define YY_INPUT(buf,result,max_size) \
result = yyextra->readBytes(buf, max_size);

#define SAVE_TOKEN yylval->string = new std::string(yytext, yyleng)
%}

%%

[ \t]                   /* eat whitespace */;
\n                      { yycolumn = 1; }
"if"					          return TIF;
"else"					        return TELSE;
"return"				        return TRETURN;
[a-zA-Z_][a-zA-Z0-9_]*  SAVE_TOKEN; return TID;
[0-9]+                  yylval->ival = std::stoul(yytext, 0, 10); return TINT;
0x[0-9a-fA-F]+          yylval->ival = std::stoul(yytext, 0, 16); return TINT;
0b[01]+                 yylval->ival = std::stoul(yytext, 0, 2); return TINT;

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
"&"                     return TBITAND;
"|"                     return TBITOR;
"/"                     return TDIV;
";"                     return TSEMI;
"!"                     return TNOT;
"..."                   return TELLIPSIS;

.                       printf("Unknown token!\n"); yyterminate();

%%

void staple::ParserContext::init_scanner() {
  yylex_init(&mScanner);
  yyset_extra(this, mScanner);
}

void staple::ParserContext::destroy_scanner() {
   yylex_destroy(mScanner);
}