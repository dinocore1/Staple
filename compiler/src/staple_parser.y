%define api.pure
%locations
%defines
%error-verbose
%parse-param { staple::ParserContext* ctx }
%lex-param { void* scanner  }

%{
#include "stdafx.h"
%}

%code requires {
#include "stdafx.h"
typedef std::vector<staple::Expr*> ExprList;
typedef std::vector<staple::Stmt*> StmtList;
typedef std::vector<staple::Param*> ParamList;
typedef std::vector<std::string> FQPath;
}

%union {
  int ival;
	std::string* string;
  staple::Field* field;
  staple::Method* method;
  staple::Type* type;
	staple::Stmt* stmt;
	staple::Expr* expr;
	StmtList* stmtlist;
	ExprList* exprlist;
  ParamList* paramlist;
  FQPath* fqpath;
}

%token TPACKAGE TCLASS TEXTENDS TIMPLEMENTS TNAMESEP
%token TIF TELSE TNOT TSEMI TRETURN TFOR
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TELLIPSIS
%token TPLUS TMINUS TMUL TDIV TAND TOR TBITAND TBITOR TTWIDLE TCARET
%token <string> TID
%token <ival> TINT

%type <field> field
%type <method> method
%type <stmt> stmt block
%type <expr> expr lvalue fieldref funcall methodcall relationexpr logicexpr
%type <expr> primary addexpr mulexpr bitexpr unaryexpr
%type <fqpath> fqpath
%type <stmtlist> stmtlist
%type <exprlist> arglist
%type <paramlist> paramlist
%type <type> type

%right ELSE TELSE

%start compileunit

%{
int yylex(YYSTYPE* lvalp, YYLTYPE* llocp, void* scanner);

void yyerror(YYLTYPE* locp, staple::ParserContext* context, const char* err);

#define scanner ctx->mScanner
#define currentClass ctx->mCurrentClass

using namespace staple;
%}

%%

compileunit
  : package class
  ;

package
  : TPACKAGE fqpath
  |
  ;

fqpath
  : TID { $$ = new FQPath(); $$->push_back(*$1); delete $1; }
  | fqpath TNAMESEP TID { $$->push_back(*$3); delete $3; }
  ;

class
  : TCLASS TID TLBRACE classparts TRBRACE
  | TCLASS TID TEXTENDS fqpath TLBRACE classparts TRBRACE
  | TCLASS TID TEXTENDS fqpath TIMPLEMENTS classlist TLBRACE classparts TRBRACE
  ;

classlist
  : fqpath
  | classlist TCOMMA fqpath
  ;

classparts
  : field { currentClass->addField($1); }
  | method { currentClass->addMethod($1); }
  |
  ;

field
  : type TID TSEMI { $$ = new Field(*$2); delete $2; }
  ;

method
  : type TID TLPAREN paramlist TRPAREN TLBRACE stmtlist TRBRACE
   { $$ = new Method(); }
  ;

paramlist
  : type TID { }
  | paramlist TCOMMA type TID { }
  | { $$ = new ParamList(); }
  ;

type
  : fqpath {}
  | fqpath TCARET {}
  | fqpath TMUL {}
  ;

stmt
	: lvalue TEQUAL expr TSEMI { $$ = new Assign($1, $3); $$->location = @$; }
  | funcall TSEMI {}
  | methodcall TSEMI {}
  | vardecl {}
	| TRETURN expr TSEMI { $$ = new Return($2); $$->location = @$; }
	| TIF TLPAREN expr TRPAREN stmt %prec ELSE { $$ = new IfStmt($3, $5); $$->location = @$; }
	| TIF TLPAREN expr TRPAREN stmt TELSE stmt { $$ = new IfStmt($3, $5, $7); $$->location = @$; }
	| TFOR TLPAREN stmt stmt stmt TRPAREN stmt {}
	| block
	;

stmtlist
	: stmtlist stmt { $1->push_back($2); }
	| { $$ = new StmtList(); }
	;

block
	: TLBRACE stmtlist TRBRACE { $$ = new Block($2); ctx->rootNode = $$; }
	;

lvalue
  : TID {}
  | funcall
  | fieldref
  | methodcall
  ;

fieldref
  : lvalue TDOT TID
  ;

funcall
  : TID TLPAREN arglist TRPAREN {}
  ;

arglist
	: arglist TCOMMA expr { $1->push_back($3); }
  | expr { $$->push_back($1); }
	| { $$ = new ExprList(); }
	;

methodcall
  : lvalue TDOT TID TLPAREN arglist TRPAREN
  ;

vardecl
  : type TID TSEMI
  ;

expr
  : relationexpr
  ;

relationexpr
  : logicexpr TCEQ logicexpr
  | logicexpr TCNE logicexpr
  | logicexpr TCLT logicexpr
  | logicexpr TCLE logicexpr
  | logicexpr TCGT logicexpr
  | logicexpr TCGE logicexpr
  | logicexpr
  ;

logicexpr
  : addexpr TAND addexpr
  | addexpr TOR addexpr
  | addexpr
  ;

addexpr
  : mulexpr TPLUS mulexpr
  | mulexpr TMINUS mulexpr
  | mulexpr
  ;

mulexpr
  : bitexpr TMUL bitexpr
  | bitexpr TDIV bitexpr
  | bitexpr
  ;

bitexpr
  : unaryexpr TBITAND unaryexpr
  | unaryexpr TBITOR unaryexpr
  | unaryexpr TCARET unaryexpr
  | unaryexpr
  ;

unaryexpr
  : TNOT primary {}
  | TMINUS primary {}
  | TTWIDLE primary {}
  | primary
  ;

primary
  : TLPAREN expr TRPAREN { $$ = $2; }
  | TINT {} // int literal
  | lvalue
  ;

%%

void yyerror(YYLTYPE* locp, staple::ParserContext* context, const char* err) {
  context->parseError(locp->first_line, locp->first_column, err);
}

ParserContext::ParserContext()
: mScanner(nullptr), mInputStream(nullptr) {

}

int ParserContext::readBytes(char* buf, const int max) {
    mInputStream->read(buf, max);
    int bytesRead = mInputStream->gcount();
    return bytesRead;
}

ParserContext::~ParserContext() {
}

bool ParserContext::parse(const staple::File& file) {
  std::ifstream inputFileStream(file.getAbsolutePath(), std::ifstream::in);
  if (!inputFileStream) {
      fprintf(stderr, "cannot open file: %s", file.getAbsolutePath().c_str());
      return false;
  }
  return parse(file.getName(), inputFileStream);
}

bool ParserContext::parse(const std::string& filepath) {
   std::ifstream inputFileStream(filepath.c_str(), std::ifstream::in);
     if (!inputFileStream) {
         fprintf(stderr, "cannot open file: %s", filepath.c_str());
         return false;
     }
     return parse(filepath.c_str(), inputFileStream);
}

bool ParserContext::parse(const std::string& streamName, std::istream& is) {
  mSuccess = true;
  mStreamName = streamName;
  mInputStream = &is;
  init_scanner();
  yyparse(this);
  destroy_scanner();
  return mSuccess;
}

void ParserContext::parseError(const int line, const int column, const char* errMsg) {
  mSuccess = false;
  fprintf(stderr, "%s:%d: %s", mStreamName.c_str(), line, errMsg);
}
