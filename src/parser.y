%{
#include <cstdio>
#include "node.h"
NBlock *programBlock; /* the top level root node of our final AST */

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
    NBlock *block;
    NExpression *expr;
    NStatement *stmt;
    NIdentifier *ident;
    NVariableDeclaration *var_decl;
    std::vector<NVariableDeclaration*> *varvec;
    std::vector<NExpression*> *exprvec;
    std::string *string;
    int token;
}

/* Define our terminal symbols (tokens). This should
   match our tokens.l lex file. We also define the ASTNode type
   they represent.
 */
%token <string> TIDENTIFIER TINTEGER TDOUBLE
%token <token> TRETURN TSEMI
%token <token> TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token <token> TLPAREN TRPAREN TLBRACE TRBRACE TCOMMA TDOT
%token <token> TPLUS TMINUS TMUL TDIV

/* Define the type of node our nonterminal symbols represent.
   The types refer to the %union declaration above. Ex: when
   we call an ident (defined by union type ident) we are really
   calling an (NIdentifier*). It makes the compiler happy.
 */
%type <ident> ident
%type <expr> numeric expr multexpr addexpr unaryexpr
%type <varvec> func_decl_args
%type <exprvec> call_args
%type <block> program stmts block
%type <stmt> stmt var_decl func_decl
%type <token> comparison

%start program

%%

program : stmts { programBlock = $1; }
        ;
        
stmts : stmt { $$ = new NBlock(); $$->statements.push_back($<stmt>1); }
      | stmts stmt { $1->statements.push_back($<stmt>2); }
      ;

stmt : var_decl TSEMI
     | func_decl
     | expr TSEMI { $$ = new NExpressionStatement(*$1); }
     ;

block : TLBRACE stmts TRBRACE { $$ = $2; }
      | TLBRACE TRBRACE { $$ = new NBlock(); }
      ;

var_decl : ident ident { $$ = new NVariableDeclaration(*$1, *$2); }
         | ident ident TEQUAL expr { $$ = new NVariableDeclaration(*$1, *$2, $4); }
         ;
        
func_decl : ident ident TLPAREN func_decl_args TRPAREN block 
            { $$ = new NFunctionDeclaration(*$1, *$2, *$4, *$6); delete $4; }
          ;
    
func_decl_args : /*blank*/  { $$ = new VariableList(); }
          | var_decl { $$ = new VariableList(); $$->push_back($<var_decl>1); }
          | func_decl_args TCOMMA var_decl { $1->push_back($<var_decl>3); }
          ;

ident : TIDENTIFIER { $$ = new NIdentifier(*$1); delete $1; }
      ;

numeric : TINTEGER { $$ = new NInteger(atol($1->c_str())); delete $1; }
        | TDOUBLE { $$ = new NDouble(atof($1->c_str())); delete $1; }
        ;
    
expr : ident TEQUAL expr { $$ = new NAssignment(*$<ident>1, *$3); }
     | ident TLPAREN call_args TRPAREN { $$ = new NMethodCall(*$1, *$3); delete $3; }
     | addexpr { $$ = $1; }
     | TLPAREN expr TRPAREN { $$ = $2; }
     | TRETURN expr { $$ = new NReturn(*$2); }
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
          | numeric { $$ = $1; }
          ;
    
call_args : /*blank*/  { $$ = new ExpressionList(); }
          | expr { $$ = new ExpressionList(); $$->push_back($1); }
          | call_args TCOMMA expr  { $1->push_back($3); }
          ;

comparison : TCEQ | TCNE | TCLT | TCLE | TCGT | TCGE 
           | TPLUS | TMINUS | TMUL | TDIV
           ;

%%




