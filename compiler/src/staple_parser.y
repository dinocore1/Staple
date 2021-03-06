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
typedef std::vector<staple::NExpr*> ExprList;
typedef std::vector<staple::NStmt*> StmtList;
typedef std::vector<staple::NParam*> ParamList;
}

%union {
  int ival;
  std::string* string;
  staple::Node* node;
  staple::NType* type;
  staple::NStmt* stmt;
  staple::NExpr* expr;
  StmtList* stmtlist;
  ExprList* exprlist;
  ParamList* paramlist;
  staple::FQPath* fqpath;
}

%token TIMPORT TPACKAGE TCLASS TEXTENDS TIMPLEMENTS TNAMESEP
%token TIF TELSE TNOT TSEMI TRETURN TFOR
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TELLIPSIS TARRAYTYPE
%token TPLUS TMINUS TMUL TDIV TAND TOR TBITAND TBITOR TTWIDLE TCARET
%token <string> TID TSTRINGLITERAL
%token <ival> TINT

%type <node> externFunctionDecl functionDecl classDecl classparts fieldDecl methodDecl
%type <stmt> stmt block
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
    imports package body
  ;

imports
  : imports import
  |
  ; 

import
  : TIMPORT fqpath TSEMI
  { ctx->rootNode->add( new NImport(*$2) ); delete $2; }
  ;

package
  : TPACKAGE fqpath TSEMI
  { ctx->rootNode->setPackage(*$2); delete $2; }
  |
  ;

body
  : body classDecl { ctx->rootNode->add($2); }
  | body externFunctionDecl { ctx->rootNode->add($2); }
  | body functionDecl { ctx->rootNode->add($2); }
  |
  ;

externFunctionDecl
  : type TID TLPAREN paramlist TRPAREN TSEMI
    { $$ = new NExternFunctionDecl(*$2, $1, $4, false); delete $2; delete $4; }
  | type TID TLPAREN paramlist TCOMMA TELLIPSIS TRPAREN TSEMI
    { $$ = new NExternFunctionDecl(*$2, $1, $4, true); delete $2; delete $4; }
  ;

functionDecl
  : type TID TLPAREN paramlist TRPAREN TLBRACE stmtlist TRBRACE
    { $$ = new NFunctionDecl(*$2, $1, $4, $7); delete $2; delete $4; $$->location = @$; }
  ;

fqpath
  : TID { $$ = new FQPath(); $$->add(*$1); delete $1; }
  | fqpath TNAMESEP TID { $$->add(*$3); delete $3; }
  ;

classDecl
  : TCLASS TID TLBRACE classparts TRBRACE
    { $$ = new NClassDecl(*$2, $4); delete $2; $$->location = @$; }
  | TCLASS TID TEXTENDS fqpath TLBRACE classparts TRBRACE
    { $$ = new NClassDecl(*$2, $6); delete $2; $$->location = @$; }
  | TCLASS TID TEXTENDS fqpath TIMPLEMENTS classlist TLBRACE classparts TRBRACE
    { $$ = new NClassDecl(*$2, $8); delete $2; $$->location = @$; }
  ;

classlist
  : fqpath
  | classlist TCOMMA fqpath
  ;

classparts
  : classparts fieldDecl { $1->add($2); }
  | classparts methodDecl { $1->add($2); }
  | { $$ = new Node(); }
  ;

fieldDecl
  : type TID TSEMI { $$ = new NFieldDecl($1, *$2); delete $2; $$->location = @$; }
  ;

methodDecl
  : type TID TLPAREN paramlist TRPAREN TLBRACE stmtlist TRBRACE
   { $$ = new NMethodDecl(*$2); delete $2; $$->location = @$; }
  ;

paramlist
  : type TID
    { $$ = new ParamList(); $$->push_back(new NParam(*$2, $1)); delete $2; }
  | paramlist TCOMMA type TID
    { $$->push_back(new NParam(*$4, $3)); delete $4; }
  | { $$ = new ParamList(); }
  ;

type
  : fqpath { $$ = new NNamedType(*$1); }
  | type TCARET { $$ = new NPointerType($1); }
  | type TMUL { $$ = new NPointerType($1); }
  | type TARRAYTYPE { $$ = new NArrayType($1); }
  ;

stmt
  : lvalue TEQUAL expr TSEMI { $$ = new NAssign($1, $3); $$->location = @$; }
  | funcall TSEMI { $$ = (NStmt*)$1; $$->location = @$; }
  | methodcall TSEMI { $$ = (NStmt*)$1; $$->location = @$; }
  | type TID TSEMI { $$ = new NLocalVar(*$2, $1); delete $2; $$->location = @$; }
  | type TID TEQUAL expr TSEMI
    { $$ = new NLocalVar(*$2, $1, $4); delete $2; $$->location = @$; }
  | TRETURN expr TSEMI { $$ = new NReturn($2); $$->location = @$; }
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
  : TLBRACE stmtlist TRBRACE { $$ = new NBlock($2); $$->location = @$; }
  ;

lvalue
  : TID { $$ = new NSymbolRef(*$1); delete $1; $$->location = @$; }
  | funcall
  | fieldref { $$ = $1; }
  | methodcall
  | arrayref
  ;

fieldref
  : lvalue TDOT TID { $$ = new NFieldRef($1, *$3); delete $3; $$->location = @$; }
  ;

arrayref
  : lvalue TLBRACKET expr TRBRACKET
  { $$ = new NArrayRef(new NLoad($1), $3); $$->location = @$; }
  ;

funcall
  : TID TLPAREN arglist TRPAREN { $$ = new NCall(*$1, $3); delete $1; $$->location = @$; }
  ;

arglist
  : arglist TCOMMA expr { $1->push_back($3); }
  | expr { $$ = new ExprList(); $$->push_back($1); }
  | { $$ = new ExprList(); }
  ;

methodcall
  : lvalue TDOT TID TLPAREN arglist TRPAREN
  ;

expr
  : relationexpr
  ;

relationexpr
  : logicexpr TCEQ logicexpr { $$ = new NOperation(NOperation::Type::CMPEQ, $1, $3); $$->location = @$; }
  | logicexpr TCNE logicexpr { $$ = new NOperation(NOperation::Type::CMPNE, $1, $3); $$->location = @$; }
  | logicexpr TCLT logicexpr { $$ = new NOperation(NOperation::Type::CMPLT, $1, $3); $$->location = @$; }
  | logicexpr TCLE logicexpr { $$ = new NOperation(NOperation::Type::CMPLE, $1, $3); $$->location = @$; }
  | logicexpr TCGT logicexpr { $$ = new NOperation(NOperation::Type::CMPGT, $1, $3); $$->location = @$; }
  | logicexpr TCGE logicexpr { $$ = new NOperation(NOperation::Type::CMPGE, $1, $3); $$->location = @$; }
  | logicexpr
  ;

logicexpr
  : addexpr TAND addexpr
  | addexpr TOR addexpr
  | addexpr
  ;

addexpr
  : mulexpr TPLUS mulexpr { $$ = new NOperation(NOperation::Type::ADD, $1, $3); $$->location = @$; }
  | mulexpr TMINUS mulexpr { $$ = new NOperation(NOperation::Type::SUB, $1, $3); $$->location = @$; }
  | mulexpr
  ;

mulexpr
  : bitexpr TMUL bitexpr { $$ = new NOperation(NOperation::Type::MUL, $1, $3); $$->location = @$; }
  | bitexpr TDIV bitexpr { $$ = new NOperation(NOperation::Type::DIV, $1, $3); $$->location = @$; }
  | bitexpr
  ;

bitexpr
  : unaryexpr TBITAND unaryexpr
  | unaryexpr TBITOR unaryexpr
  | unaryexpr TCARET unaryexpr
  | unaryexpr
  ;

unaryexpr
  : TNOT primary { $$ = new NNot($2); $$->location = @$; }
  | TMINUS primary { $$ = new NNeg($2); $$->location = @$; }
  | TTWIDLE primary {}
  | primary
  ;

primary
  : TLPAREN expr TRPAREN { $$ = $2; }
  | TINT { $$ = new NIntLiteral($1); $$->location = @$; }
  | TSTRINGLITERAL { $$ = new NStringLiteral($1->substr(1, $1->length()-2)); delete $1; $$->location = @$; }
  | funcall
  | methodcall
  | fieldref { $$ = new NLoad($$); $$->location = @$; }
  | TID { $$ = new NSymbolRef(*$1); delete $1; $$->location = @$; $$ = new NLoad($$); $$->location = @$; }
  | arrayref { $$ = new NLoad($$); $$->location = @$; }
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
