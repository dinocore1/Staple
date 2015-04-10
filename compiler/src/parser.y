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

%code requires {

extern char *filename; /* current filename here for the lexer */



#if ! defined YYLTYPE && ! defined YYLTYPE_IS_DECLARED
typedef struct YYLTYPE
{
  int first_line;
  int first_column;
  int last_line;
  int last_column;
  char* filename;
} YYLTYPE;
# define yyltype YYLTYPE /* obsolescent; will be withdrawn */
# define YYLTYPE_IS_DECLARED 1
#endif


# define YYLLOC_DEFAULT(Current, Rhs, N)                               \
    do                                                                 \
      if (N)                                                           \
        {                                                              \
          (Current).first_line   = YYRHSLOC (Rhs, 1).first_line;       \
          (Current).first_column = YYRHSLOC (Rhs, 1).first_column;     \
          (Current).last_line    = YYRHSLOC (Rhs, N).last_line;        \
          (Current).last_column  = YYRHSLOC (Rhs, N).last_column;      \
          (Current).filename     = YYRHSLOC (Rhs, 1).filename;         \
        }                                                              \
      else                                                             \
        { /* empty RHS */                                              \
          (Current).first_line   = (Current).last_line   =             \
            YYRHSLOC (Rhs, 0).last_line;                               \
          (Current).first_column = (Current).last_column =             \
            YYRHSLOC (Rhs, 0).last_column;                             \
          (Current).filename  = NULL;                        /* new */ \
        }                                                              \
    while (0)
}



/* Represents the many different ways we can access our data */
%union {
    ASTNode *node;
    NType *type;
    NBlock *block;
    NExpression *expr;
    NStatement *stmt;
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
    NMethodFunction* method_function;
    int count;
}

/* Define our terminal symbols (tokens). This should
   match our tokens.l lex file. We also define the ASTNode type
   they represent.
 */
%token <string> TIDENTIFIER TINTEGER TDOUBLE TSTRINGLIT
%token <token> TCLASS TRETURN TSEMI TEXTERN TELLIPSIS TINCLUDE
%token <token> TIF TELSE TAT TNEW TSIZEOF TNOT
%token <token> TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token <token> TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token <token> TPLUS TMINUS TMUL TDIV

/* Define the type of node our nonterminal symbols represent.
   The types refer to the %union declaration above. Ex: when
   we call an ident (defined by union type ident) we are really
   calling an (NIdentifier*). It makes the compiler happy.
 */
%type <type> type
%type <block> block stmts
%type <expr> expr lhs compexpr multexpr addexpr ident literal unaryexpr primary base arrayindex
%type <exprvec> expr_list
%type <stmt> stmt stmtexpr var_decl
%type <token> comparison
%type <nodelist> class_members
%type <class_decl> class_decl
%type <field> field
%type <prototype> proto_func
%type <func_args> proto_args
%type <boolean> ellipse_arg
%type <function> global_func
%type <method_function> method
%type <count> numPointers
%type <string> package

%right "then" TELSE
%right "order" TMINUS
%left TAT TDOT

%start compileUnit

%%

compileUnit
        : { compileUnit = new NCompileUnit(); }
          includes program
        ;

includes
        : includes TINCLUDE package { compileUnit->mIncludes.push_back(*$3); delete $3; }
        |
        ;

package
        : TIDENTIFIER { $$ = new std::string(*$1); delete $1; }
        | package TDOT TIDENTIFIER { (*$$)+="."; (*$$)+=*$3; delete $3;  }
        ;

program
        : program class_decl { compileUnit->classes.push_back($2); }
        | program global_func { compileUnit->functions.push_back($2); }
        | program proto_func { compileUnit->externFunctions.push_back($2); }
        |
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
         { $$ = new NMethodFunction(*$1, *$2, *$4, $5, *$7); delete $2; delete $4; }
        ;

///// Statements //////

block
        : TLBRACE stmts TRBRACE { $$ = $2; }
        ;

stmts
        : stmts stmt { $1->statements.push_back($2); }
        | { $$ = new NBlock(); }
        ;

stmt    : stmtexpr TSEMI { $$ = $1; }
        | TRETURN expr TSEMI { $$ = new NReturn($2); }
        | TIF TLPAREN expr TRPAREN stmt { $$ = new NIfStatement($3, $5, NULL); } %prec "then"
        | TIF TLPAREN expr TRPAREN stmt TELSE stmt { $$ = new NIfStatement($3, $5, $7); }
        | block { $$ = $1; }
        ;


var_decl : type TIDENTIFIER { $$ = new NVariableDeclaration($1, *$2); delete $2; $$->location = @2; }
         | type TIDENTIFIER TEQUAL expr { $$ = new NVariableDeclaration($1, *$2, $4); delete $2; $$->location = @2; }
         ;

type
        : TIDENTIFIER numPointers { $$ = NType::GetPointerType(*$1, $2); delete $1; $$->location = @$; }
        | TIDENTIFIER TLBRACKET TINTEGER TRBRACKET { $$ = NType::GetArrayType(*$1, atoi($3->c_str())); delete $1; delete $3; $$->location = @$; }
        ;

numPointers
        : numPointers TMUL { $$ += 1; }
        | { $$ = 0; }
        ;

ident
        : TIDENTIFIER { $$ = new NIdentifier(*$1); delete $1; $$->location = @$; }
        ;

literal : TINTEGER { $$ = new NIntLiteral(*$1); delete $1; $$->location = @$; }
        | TDOUBLE { $$ = new NFloatLiteral(*$1); delete $1; $$->location = @$; }
        | TSTRINGLIT { std::string tmp = $1->substr(1, $1->length()-2); $$ = new NStringLiteral(tmp); delete $1; $$->location = @$; }
        ;


stmtexpr
        : var_decl
        | TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NExpressionStatement(new NFunctionCall(*$1, *$3)); delete $1; delete $3; $$->location = @$; }
        | lhs TEQUAL expr { $$ = new NAssignment($1, $3); $$->location = @$; }
        ;

lhs
        : ident
        | lhs TDOT TIDENTIFIER { $$ = new NMemberAccess(new NLoad($1), *$3); delete $3; }
        | lhs TDOT TIDENTIFIER TLPAREN expr_list TRPAREN
        | lhs TAT arrayindex { $$ = new NArrayElementPtr(new NLoad($1), $3); } /* array access */
        ;

expr
        : TSIZEOF type { $$ = new NSizeOf($2); }
        | TNEW TIDENTIFIER { $$ = new NNew(*$2); delete $2; }
        | compexpr { $$ = $1; }
        ;

compexpr
        : addexpr comparison addexpr { $$ = new NBinaryOperator($1, $2, $3); $$->location = @$; }
        | addexpr { $$ = $1; }
        ;

comparison
        : TCEQ | TCNE | TCLT | TCLE | TCGT | TCGE
        ;

addexpr : multexpr TPLUS multexpr { $$ = new NBinaryOperator($1, $2, $3); $$->location = @$; }
        | multexpr TMINUS multexpr { $$ = new NBinaryOperator($1, $2, $3); $$->location = @$; }
        | multexpr { $$ = $1; }
        ;

multexpr : unaryexpr TMUL unaryexpr { $$ = new NBinaryOperator($1, $2, $3); $$->location = @$; }
         | unaryexpr TDIV unaryexpr { $$ = new NBinaryOperator($1, $2, $3); $$->location = @$; }
         | unaryexpr { $$ = $1; }
         ;

unaryexpr
        : TNOT primary { $$ = new NNot($2); }
        | TMINUS primary { $$ = new NNegitive($2); }
        | primary
        ;

primary
        : TLPAREN expr_list TRPAREN { if($2->size() == 1) { $$ = (*$2)[0]; } } %prec "order"
        | literal { $$ = $1; }
        | base { $$ = $1; }
        | TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NFunctionCall(*$1, *$3); delete $1; delete $3; }
        | TLPAREN expr_list TRPAREN TMINUS TCGT stmt /* anonymous function */
        ;

expr_list
        : expr { $$ = new ExpressionList(); $$->push_back($1); }
        | expr_list TCOMMA expr { $$->push_back($3); }
        | { $$ = new ExpressionList(); }
        ;


arrayindex
        : ident { $$ = new NLoad($1); }
        | TINTEGER { $$ = new NIntLiteral(*$1); delete $1; }
        | TLPAREN expr TRPAREN { $$ = $2; }
        ;

base
        : ident { $$ = new NLoad($1); }
        | base TAT arrayindex { $$ = new NLoad(new NArrayElementPtr($1, $3)); }
        | base TDOT TIDENTIFIER { $$ = new NLoad(new NMemberAccess($1, *$3)); delete $3; }
        | base TDOT TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NMethodCall($1, *$3, *$5); delete $3; delete $5; }
        ;



%%




