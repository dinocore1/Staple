
%option reentrant
%option bison-bridge
%option bison-locations
%option noyywrap
%option yylineno


%{

#include <string>
#include "parsercontext.h"
#include "parser.hpp"

#define YY_EXTRA_TYPE staple::ParserContext*

#define YY_USER_ACTION yylloc->first_line = yylineno;


#define YY_INPUT(buf,result,max_size) \
result = yyextra->readBytes(buf, max_size);

#define SAVE_TOKEN yylval->string = new std::string(yytext, yyleng)

using namespace staple;

%}

%x comment


%%

"/*"                    BEGIN(comment);
<comment>[^*\n]*        /* eat anything that's not a '*' */
<comment>"*"+[^*/\n]*   /* eat up '*'s not followed by '/'s */
<comment>\n             yycolumn = 1;
<comment>"*"+"/"        BEGIN(INITIAL);
"//"[^\n]*              ;

[ \t]                   ;
\n                      { yycolumn = 1; }
"package"               return TPACKAGE;
"extern"                return TEXTERN;
"class"                 return TCLASS;
"if"                    return TIF;
"else"                  return TELSE;
"return"                return TRETURN;
"for"                   return TFOR;
"@"                     return TAT;
"new"                   return TNEW;
"sizeof"                return TSIZEOF;
"import"                return TIMPORT;
"extends"               return TEXTENDS;
\"([^\\\"]|\\.)*\"      SAVE_TOKEN; return TSTRINGLIT;
0x[0-9a-fA-F]+          yylval->ival = std::stoul(yytext, 0, 16); return TINTEGER;
0b[01]+                 yylval->ival = std::stoul(yytext, 0, 2); return TINTEGER;
[0-9]+                  yylval->ival = std::stoul(yytext, 0, 10); return TINTEGER;
[0-9]+\.[0-9]*          yylval->fval = std::stod(yytext); return TFLOAT;
INF                     yylval->fval = std::stod(yytext); return TFLOAT;
NAN                     yylval->fval = std::stod(yytext); return TFLOAT;
[a-zA-Z_][a-zA-Z0-9_]*  SAVE_TOKEN; return TIDENTIFIER;


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
"/"                     return TDIV;
";"                     return TSEMI;
"!"                     return TNOT;
"..."                   return TELLIPSIS;
.                       printf("Unknown token!\n"); yyterminate();

%%

void ParserContext::init_scanner()
{
   yylex_init(&scanner);
   yyset_extra(this, scanner);
}

void ParserContext::destroy_scanner()
{
   yylex_destroy(scanner);
}

