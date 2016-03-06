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
typedef std::vector<staple::NStmt*> StmtList;
typedef std::vector<staple::NParam*> ParamList;
typedef std::vector<std::string> FQPath;
}

%union {
  int ival;
  std::string* string;
  staple::Node* node;
  staple::NField* field;
  staple::NMethod* method;
  staple::NType* type;
  staple::NStmt* stmt;
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
%token TELLIPSIS TARRAYTYPE
%token TPLUS TMINUS TMUL TDIV TAND TOR TBITAND TBITOR TTWIDLE TCARET
%token <string> TID
%token <ival> TINT

%type <node> class functiondecl globalfunction
%type <field> field
%type <method> method
%type <stmt> stmt localvar block
%type <expr> expr lvalue fieldref arrayref funcall methodcall relationexpr logicexpr
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
  : { ctx->rootNode = new NCompileUnit(); }
    package body
  ;

package
  : TPACKAGE fqpath { ctx->rootNode->setPackage(*$2); delete $2; }
  |
  ;

body
  : body class { ctx->rootNode->add($2); }
  | body functiondecl { ctx->rootNode->add($2); }
  | body globalfunction { ctx->rootNode->add($2); }
  |
  ;

functiondecl
  : type TID TLPAREN paramlist TRPAREN TSEMI
    { $$ = new NFunctionDecl(*$2, $1, $4); delete $2; }
  ;

globalfunction
  : type TID TLPAREN paramlist TRPAREN TLBRACE stmtlist TRBRACE
    { $$ = new NFunction(*$2, $1, $4, $7); delete $2; }
  ;

fqpath
  : TID { $$ = new FQPath(); $$->push_back(*$1); delete $1; }
  | fqpath TNAMESEP TID { $$->push_back(*$3); delete $3; }
  ;

class
  : TCLASS TID TLBRACE classparts TRBRACE
    { $$ = new NClass(*$2); delete $2; }
  | TCLASS TID TEXTENDS fqpath TLBRACE classparts TRBRACE
    { $$ = new NClass(*$2); delete $2; }
  | TCLASS TID TEXTENDS fqpath TIMPLEMENTS classlist TLBRACE classparts TRBRACE
    { $$ = new NClass(*$2); delete $2; }
  ;

classlist
  : fqpath
  | classlist TCOMMA fqpath
  ;

classparts
  : field { currentClass->add($1); }
  | method { currentClass->add($1); }
  |
  ;

field
  : type TID TSEMI { $$ = new NField(*$2); delete $2; }
  ;

method
  : type TID TLPAREN paramlist TRPAREN TLBRACE stmtlist TRBRACE
   { $$ = new NMethod(*$2); delete $2; }
  ;

paramlist
  : type TID
    { $$ = new ParamList(); $$->push_back(new NParam(*$2, $1)); delete $2; }
  | paramlist TCOMMA type TID
    { $$->push_back(new NParam(*$4, $3)); delete $4; }
  | { $$ = new ParamList(); }
  ;

type
  : fqpath { $$ = new NType(*$1); }
  | type TCARET {}
  | type TMUL {}
  | type TARRAYTYPE
  ;

stmt
  : lvalue TEQUAL expr TSEMI { $$ = new Assign($1, $3); $$->location = @$; }
  | funcall TSEMI { $$ = (NStmt*)$1; }
  | methodcall TSEMI { $$ = (NStmt*)$1; }
  | localvar
  | TRETURN expr TSEMI { $$ = new Return($2); $$->location = @$; }
  | TIF TLPAREN expr TRPAREN stmt %prec ELSE { $$ = new NIfStmt($3, $5); $$->location = @$; }
  | TIF TLPAREN expr TRPAREN stmt TELSE stmt { $$ = new NIfStmt($3, $5, $7); $$->location = @$; }
  | TFOR TLPAREN stmt stmt stmt TRPAREN stmt {}
  | block
  ;

stmtlist
  : stmtlist stmt { $1->push_back($2); }
  | { $$ = new StmtList(); }
  ;

block
  : TLBRACE stmtlist TRBRACE { $$ = new NBlock($2); }
  ;

lvalue
  : TID { $$ = new NSymbolRef(*$1); delete $1; }
  | funcall
  | fieldref
  | methodcall
  | arrayref
  ;

fieldref
  : lvalue TDOT TID
  ;

arrayref
  : lvalue TLBRACKET expr TRBRACKET
  { $$ = new NArrayRef($1, $3); }
  ;

funcall
  : TID TLPAREN arglist TRPAREN { $$ = new NCall(*$1, $3); delete $1;}
  ;

arglist
  : arglist TCOMMA expr { $1->push_back($3); }
  | expr { $$ = new ExprList(); $$->push_back($1); }
  | { $$ = new ExprList(); }
  ;

methodcall
  : lvalue TDOT TID TLPAREN arglist TRPAREN
  ;

localvar
  : type TID TSEMI { $$ = new NLocalVar(*$2, $1); delete $2; }
  ;

expr
  : relationexpr
  ;

relationexpr
  : logicexpr TCEQ logicexpr { $$ = new NOperation(NOperation::Type::CMPEQ, $1, $3); }
  | logicexpr TCNE logicexpr { $$ = new NOperation(NOperation::Type::CMPNE, $1, $3); }
  | logicexpr TCLT logicexpr { $$ = new NOperation(NOperation::Type::CMPLT, $1, $3); }
  | logicexpr TCLE logicexpr { $$ = new NOperation(NOperation::Type::CMPLE, $1, $3); }
  | logicexpr TCGT logicexpr { $$ = new NOperation(NOperation::Type::CMPGT, $1, $3); }
  | logicexpr TCGE logicexpr { $$ = new NOperation(NOperation::Type::CMPGE, $1, $3); }
  | logicexpr
  ;

logicexpr
  : addexpr TAND addexpr
  | addexpr TOR addexpr
  | addexpr
  ;

addexpr
  : mulexpr TPLUS mulexpr { $$ = new NOperation(NOperation::Type::ADD, $1, $3); }
  | mulexpr TMINUS mulexpr { $$ = new NOperation(NOperation::Type::SUB, $1, $3); }
  | mulexpr
  ;

mulexpr
  : bitexpr TMUL bitexpr { $$ = new NOperation(NOperation::Type::MUL, $1, $3); }
  | bitexpr TDIV bitexpr { $$ = new NOperation(NOperation::Type::DIV, $1, $3); }
  | bitexpr
  ;

bitexpr
  : unaryexpr TBITAND unaryexpr
  | unaryexpr TBITOR unaryexpr
  | unaryexpr TCARET unaryexpr
  | unaryexpr
  ;

unaryexpr
  : TNOT primary { $$ = new NNot($2); }
  | TMINUS primary { $$ = new NNeg($2); }
  | TTWIDLE primary {}
  | primary
  ;

primary
  : TLPAREN expr TRPAREN { $$ = $2; }
  | TINT { $$ = new NIntLiteral($1); }
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
      fprintf(stderr, "cannot open file: %s\n", file.getAbsolutePath().c_str());
      return false;
  }
  return parse(file.getName(), inputFileStream);
}

bool ParserContext::parse(const std::string& filepath) {
   std::ifstream inputFileStream(filepath.c_str(), std::ifstream::in);
     if (!inputFileStream) {
         fprintf(stderr, "cannot open file: %s\n", filepath.c_str());
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
  fprintf(stderr, "%s:%d: %s\n", mStreamName.c_str(), line, errMsg);
}
