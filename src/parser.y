%{
#include <cstdio>
#include "node.h"
NCompileUnit *compileUnit; /* the top level root node of our final AST */

extern int yylex();


#define YYDEBUG 1


extern int yylineno;
extern char* yytext;

void yyerror(const char *s)
{
    printf("%d: %s at: %s\n", yylineno, s, yytext);
    exit(-1);
}


%}

/* Represents the many different ways we can access our data */
%union {
    ASTNode *node;
    NType *type;
    NBlock *block;
    NExpression *expr;
    NStatement *stmt;
    NIdentifier *ident;
    NVariableDeclaration *var_decl;
    std::vector<NVariableDeclaration*> *varvec;
    std::vector<NExpression*> *exprvec;
    std::string *string;
    int token;
    ASTNode *nodelist;
    NClassDeclaration *class_decl;
    NField *field;
    NFunctionPrototype *prototype;
    std::vector<NArgument*> *func_args;
    bool boolean;
    NFunction *function;
}

/* Define our terminal symbols (tokens). This should
   match our tokens.l lex file. We also define the ASTNode type
   they represent.
 */
%token <string> TIDENTIFIER TINTEGER TDOUBLE TSTRINGLIT
%token <token> TCLASS TRETURN TSEMI TEXTERN TELLIPSIS
%token <token> TIF TELSE
%token <token> TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token <token> TLPAREN TRPAREN TLBRACE TRBRACE TCOMMA TDOT
%token <token> TPLUS TMINUS TMUL TDIV

/* Define the type of node our nonterminal symbols represent.
   The types refer to the %union declaration above. Ex: when
   we call an ident (defined by union type ident) we are really
   calling an (NIdentifier*). It makes the compiler happy.
 */
%type <type> type
%type <ident> ident
%type <expr> literal expr compexpr multexpr addexpr unaryexpr
%type <exprvec> call_args
%type <block> program stmts block
%type <stmt> stmt var_decl
%type <token> comparison
%type <nodelist> class_members
%type <class_decl> class_decl
%type <field> field
%type <prototype> proto_func
%type <func_args> proto_args
%type <boolean> ellipse_arg
%type <function> global_func method

%start program

%%

program
        : { compileUnit = new NCompileUnit(); }
        | program class_decl { compileUnit->classes.push_back($2); }
        | program global_func { compileUnit->functions.push_back($2); }
        | program proto_func { compileUnit->externFunctions.push_back($2); }
        ;

////// Extern Function Prototype //////

proto_func
        : TEXTERN type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN
         { $$ = new NFunctionPrototype(*$2, *$3, *$5, $6); delete $3; delete $5; }
        ;

////// Global Functions /////

global_func
        : type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN block
         { $$ = new NFunction(*$1, *$2, *$4, $5, *$7); delete $2; delete $4; }
        ;


ellipse_arg
        : { $$ = false; }
        | TELLIPSIS { $$ = true; }

proto_args
        : type { $$ = new std::vector<NArgument*>(); $$->push_back(new NArgument(*$1)); delete $1; }
        | type TIDENTIFIER { $$ = new std::vector<NArgument*>(); $$->push_back(new NArgument(*$1, *$2)); delete $1; delete $2; }
        | { $$ = new std::vector<NArgument*>(); }
        | proto_args TCOMMA type { $1->push_back(new NArgument(*$3)); delete $3; }
        | proto_args TCOMMA type TIDENTIFIER { $1->push_back(new NArgument(*$3, *$4)); delete $3; delete $4; }
        | proto_args TCOMMA { /*for the ellipse*/ }
        ;


////// Class Declaration /////

class_decl
        : TCLASS TIDENTIFIER TLBRACE class_members TRBRACE
         { $$ = new NClassDeclaration(*$2, $4); delete $2; delete $4; }
        ;

class_members
        : class_members field { $1->children.push_back($2); }
        | class_members method { $1->children.push_back($2); }
        | { $$ = new ASTNode(); }

field
        : type TIDENTIFIER TSEMI { $$ = new NField(*$1, *$2); delete $1; delete $2; }
        ;

method
        : type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN block
         { $$ = new NFunction(*$1, *$2, *$4, $5, *$7); delete $2; delete $4; }
        ;

///// Statements //////

block
        : TLBRACE stmts TRBRACE { $$ = $2; }
        | TLBRACE TRBRACE { $$ = new NBlock(); }
        ;

stmts
        : stmt { $$ = new NBlock(); $$->statements.push_back($<stmt>1); }
        | stmts stmt { $1->statements.push_back($<stmt>2); }
        ;

stmt    : var_decl TSEMI
        | expr TSEMI { $$ = new NExpressionStatement(*$1); }
        | TRETURN expr TSEMI { $$ = new NReturn(*$2); }
        | TIF TLPAREN expr TRPAREN stmt
        | TIF TLPAREN expr TRPAREN stmt TELSE stmt
        | block
        ;


var_decl : type ident { $$ = new NVariableDeclaration(*$1, *$2); }
         | type ident TEQUAL expr { $$ = new NVariableDeclaration(*$1, *$2, $4); }
         ;

type : TIDENTIFIER {  $$ = new NType(*$1, false); }
     | TIDENTIFIER TMUL { $$ = new NType(*$1, true); }
     ;

ident : TIDENTIFIER { $$ = new NIdentifier(*$1); delete $1; }
      ;

literal : TINTEGER { $$ = new NIntLiteral(*$1); delete $1; }
        | TDOUBLE { $$ = new NFloatLiteral(*$1); delete $1; }
        | TSTRINGLIT { std::string tmp = $1->substr(1, $1->length()-2); $$ = new NStringLiteral(tmp); delete $1; }
        ;
    
expr : ident TEQUAL expr { $$ = new NAssignment(*$<ident>1, *$3); }
     | compexpr { $$ = $1; }
     | TLPAREN expr TRPAREN { $$ = $2; }
     ;

compexpr
        : addexpr comparison addexpr { $$ = new NBinaryOperator(*$1, $2, *$3); }
        | addexpr { $$ = $1; }
        ;

comparison
        : TCEQ | TCNE | TCLT | TCLE | TCGT | TCGE
        ;

addexpr : multexpr TPLUS multexpr { $$ = new NBinaryOperator(*$1, $2, *$3); }
        | multexpr TMINUS multexpr { $$ = new NBinaryOperator(*$1, $2, *$3); }
        | multexpr { $$ = $1; }
        ;

multexpr : unaryexpr TMUL unaryexpr { $$ = new NBinaryOperator(*$1, $2, *$3); }
         | unaryexpr TDIV unaryexpr { $$ = new NBinaryOperator(*$1, $2, *$3); }
         | unaryexpr { $$ = $1; }
         ;

unaryexpr : ident { $<ident>$ = $1; }
          | literal { $$ = $1; }
          | ident TLPAREN call_args TRPAREN { $$ = new NMethodCall(*$1, *$3); delete $3; }
          ;
    
call_args : /*blank*/  { $$ = new ExpressionList(); }
          | expr { $$ = new ExpressionList(); $$->push_back($1); }
          | call_args TCOMMA expr  { $1->push_back($3); }
          ;



%%




