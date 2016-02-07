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
}

%union {
  int ival;
	std::string* string;
	staple::Stmt* stmt;
	staple::Expr* expr;
	StmtList* stmtlist;
	ExprList* exprlist;
}

%token TIF TELSE TNOT TSEMI TRETURN TFOR
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TELLIPSIS
%token TPLUS TMINUS TMUL TDIV TAND TOR TBITAND TBITOR
%token <string> TID
%token <ival> TINT

%type <stmt> stmt vardecl block
%type <expr> expr cmpexpr addexpr mulexpr unaryexpr primary funcall methodcall fieldref arrayref
%type <stmtlist> stmtlist
%type <exprlist> arglist

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
	: expr TEQUAL expr error TSEMI { $$ = new Assign($1, $3); $$->location = @$; }
	| funcall error TSEMI { $$ = new StmtExpr($1); }
	| methodcall error TSEMI { $$ = new StmtExpr($1); }
	| vardecl error TSEMI
	| TRETURN expr error TSEMI { $$ = new Return($2); $$->location = @$; }
	| TIF TLPAREN expr TRPAREN stmt %prec ELSE { $$ = new IfStmt($3, $5); $$->location = @$; }
	| TIF TLPAREN expr TRPAREN stmt TELSE stmt { $$ = new IfStmt($3, $5, $7); $$->location = @$; }
	| TFOR TLPAREN stmt stmt stmt TRPAREN stmt {}
	| block
	;

stmtlist
	: stmtlist stmt { $1->push_back($2); }
	| { $$ = new StmtList(); }
	;

vardecl
	: TID TID {}
	| TID TID TLBRACKET TINT TRBRACKET {}
	;

block
	: TLBRACE stmtlist TRBRACE { $$ = new Block($2); ctx->rootNode = $$; }
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
	: mulexpr TPLUS mulexpr { $$ = new Op(Op::Type::ADD, $1, $3); $$->location = @$; }
	| mulexpr TMINUS mulexpr { $$ = new Op(Op::Type::SUB, $1, $3); $$->location = @$; }
	| mulexpr
	;

mulexpr
	: unaryexpr TMUL unaryexpr { $$ = new Op(Op::Type::MUL, $1, $3); $$->location = @$; }
	| unaryexpr TDIV unaryexpr { $$ = new Op(Op::Type::DIV, $1, $3); $$->location = @$; }
	| unaryexpr
	;

unaryexpr
	: TNOT primary { $$ = new Not($2); $$->location = @$; }
	| TMINUS primary { $$ = new Neg($2); $$->location = @$; }
	| primary
	;

primary
	: TLPAREN expr TRPAREN { $$ = $2; }
	| TINT { $$ = new IntLiteral($1); $$->location = @$; }
	| TID { $$ = new Id(*$1); delete $1; $$->location = @$; }
	| fieldref
	| arrayref
	| funcall
	| methodcall
	;

funcall
	: TID TLPAREN arglist TRPAREN { $$ = new Call(*$1, $3); delete $1; $$->location = @$; }
	;

methodcall
	: primary TDOT TID TLPAREN arglist TRPAREN
	;

arrayref
	: primary TLBRACKET expr TRBRACKET
	;

fieldref
	: primary TDOT TID
	;

arglist
	: arglist TCOMMA expr { $1->push_back($3); }
	| { $$ = new ExprList(); }
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
  return parse(file.getFilename(), inputFileStream);
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
