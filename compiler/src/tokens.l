
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
#define TOKEN(t) (yylval->token = t)

using namespace staple;

%}

%x comment


%%

"/*"                    BEGIN(comment);
<comment>[^*\n]*        /* eat anything that's not a '*' */
<comment>"*"+[^*/\n]*   /* eat up '*'s not followed by '/'s */
<comment>\n             yycolumn = 1;
<comment>"*"+"/"        BEGIN(INITIAL);

[ \t]                   ;
\n                      { yycolumn = 1; }
"package"               return TOKEN(TPACKAGE);
"extern"                return TOKEN(TEXTERN);
"class"                 return TOKEN(TCLASS);
"if"                    return TOKEN(TIF);
"else"                  return TOKEN(TELSE);
"return"                return TOKEN(TRETURN);
"for"                   return TOKEN(TFOR);
"@"                     return TOKEN(TAT);
"new"                   return TOKEN(TNEW);
"sizeof"                return TOKEN(TSIZEOF);
"include"               return TOKEN(TINCLUDE);
"extends"               return TOKEN(TEXTENDS);
\"([^\\\"]|\\.)*\"      SAVE_TOKEN; return TSTRINGLIT;
[a-zA-Z_][a-zA-Z0-9_]*  SAVE_TOKEN; return TIDENTIFIER;
[0-9]+\.[0-9]*          SAVE_TOKEN; return TDOUBLE;
[0-9]+                  SAVE_TOKEN; return TINTEGER;
"="                     return TOKEN(TEQUAL);
"=="                    return TOKEN(TCEQ);
"!="                    return TOKEN(TCNE);
"<"                     return TOKEN(TCLT);
"<="                    return TOKEN(TCLE);
">"                     return TOKEN(TCGT);
">="                    return TOKEN(TCGE);
"("                     return TOKEN(TLPAREN);
")"                     return TOKEN(TRPAREN);
"{"                     return TOKEN(TLBRACE);
"}"                     return TOKEN(TRBRACE);
"["                     return TOKEN(TLBRACKET);
"]"                     return TOKEN(TRBRACKET);
"."                     return TOKEN(TDOT);
","                     return TOKEN(TCOMMA);
"+"                     return TOKEN(TPLUS);
"-"                     return TOKEN(TMINUS);
"*"                     return TOKEN(TMUL);
"/"                     return TOKEN(TDIV);
";"                     return TOKEN(TSEMI);
"!"                     return TOKEN(TNOT);
"..."                   return TOKEN(TELLIPSIS);
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

