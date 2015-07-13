
%define api.pure
%locations
%defines
%error-verbose
%parse-param { staple::ParserContext* context }
%lex-param { void* scanner  }

%{
#include <iostream>
#include <sstream>

#define YYDEBUG 1

#include "parsercontext.h"

using namespace staple;
using namespace std;



%}

%code requires {

#include <iostream>
#include <vector>

namespace staple {

class CodeGenContext;

class ASTNode;
class ASTVisitor;

class NStatement;
class NExpression;
class NExpressionStatement;
class NVariableDeclaration;
class NClassDeclaration;
class NType;
class NReturn;
class NField;
class NFunction;
class NCompileUnit;
class NAssignment;
class NArrayElementPtr;
class NIdentifier;
class NIntLiteral;
class NBlock;
class NArgument;
class NFunctionPrototype;
class NMemberAccess;
class NFunctionCall;
class NStringLiteral;
class NNew;
class NSizeOf;
class NLoad;
class NMethodFunction;
class NMethodCall;
class NIfStatement;
class NBinaryOperator;
class NForLoop;

typedef std::vector<NStatement*> StatementList;
typedef std::vector<NExpression*> ExpressionList;
typedef std::vector<NVariableDeclaration*> VariableList;

}

}

/* Represents the many different ways we can access our data */
%union {
    uint64_t ival;
    double fval;
    staple::ASTNode *node;
    staple::NType *type;
    staple::NExpression *expr;
    staple::NStatement *stmt;
    staple::NVariableDeclaration *var_decl;
    std::vector<staple::NVariableDeclaration*> *varvec;
    std::vector<staple::NExpression*> *exprvec;
    std::string *string;
    staple::ASTNode *nodelist;
    staple::NClassDeclaration *class_decl;
    staple::NField *field;
    staple::NFunctionPrototype *prototype;
    std::vector<staple::NArgument*> *func_args;
    bool boolean;
    staple::NFunction *function;
    staple::NMethodFunction* method_function;
    staple::StatementList* stmtlist;
    int count;
}

/* Define our terminal symbols (tokens). This should
   match our tokens.l lex file. We also define the ASTNode type
   they represent.
 */
%token <ival> TINTEGER
%token <fval> TFLOAT
%token <string> TIDENTIFIER TSTRINGLIT
%token TPACKAGE TCLASS TRETURN TSEMI TEXTERN TELLIPSIS TIMPORT TEXTENDS
%token TIF TELSE TAT TNEW TSIZEOF TNOT
%token TCEQ TCNE TCLT TCLE TCGT TCGE TEQUAL
%token TLPAREN TRPAREN TLBRACE TRBRACE TLBRACKET TRBRACKET TCOMMA TDOT
%token TPLUS TMINUS TMUL TDIV TFOR

/* Define the type of node our nonterminal symbols represent.
   The types refer to the %union declaration above. Ex: when
   we call an ident (defined by union type ident) we are really
   calling an (NIdentifier*). It makes the compiler happy.
 */
%type <type> type
%type <stmtlist> stmts
%type <expr> expr compexpr multexpr addexpr ident literal unaryexpr primary p_1 lvalue callable arrayindex
%type <exprvec> expr_list
%type <stmt> stmt block var_decl exprstmt
%type <nodelist> class_members
%type <class_decl> class_decl
%type <field> field
%type <prototype> proto_func
%type <func_args> proto_args
%type <boolean> ellipse_arg
%type <function> global_func
%type <method_function> method
%type <count> numPointers
%type <string> namespace extends

%right ELSE TELSE

%start compileUnit

%{

#include "node.h"
int yylex(YYSTYPE* lvalp, YYLTYPE* llocp, void* scanner);

void yyerror(YYLTYPE* locp, ParserContext* context, const char* err)
{
  cout << locp->first_line << ":" << err << endl;
}

#define scanner context->scanner


NType* NType::GetPointerType(const std::string& name, int numPtrs)
{
       NType* retval = new NType();
       retval->name = name;
       retval->isArray = false;
       retval->numPointers = numPtrs;
       return retval;
}

NType* NType::GetArrayType(const std::string& name, int size)
{
       NType* retval = new NType();
       retval->name = name;
       retval->isArray = true;
       retval->size = size;
       return retval;
}



%}

%%

compileUnit
        : { context->compileUnit = new NCompileUnit(); }
          header program
        ;

header
        : package includes
        ;

package
        : TPACKAGE namespace { context->compileUnit->package = *$2; delete $2; }

includes
        : includes import
        |
        ;

import
        : TIMPORT namespace { context->compileUnit->includes.push_back(*$2); delete $2; }
        ;

namespace
        : TIDENTIFIER { $$ = new std::string(*$1); delete $1; }
        | namespace TDOT TIDENTIFIER { (*$$)+="." + *$3; delete $3;  }
        ;

program
        : program class_decl { context->compileUnit->classes.push_back($2); }
        | program global_func { context->compileUnit->functions.push_back($2); }
        | program proto_func { context->compileUnit->externFunctions.push_back($2); }
        |
        ;

////// Extern Function Prototype //////

proto_func
        : TEXTERN type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN
         { $$ = new NFunctionPrototype(*$2, *$3, *$5, $6); delete $3; delete $5; }
        ;

////// Global Functions /////

global_func
        : type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN TLBRACE stmts TRBRACE
         { $$ = new NFunction(*$1, *$2, *$4, $5, *$8); delete $2; delete $4; delete $8; $$->location = @$; }
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
        : TCLASS TIDENTIFIER extends TLBRACE class_members TRBRACE
         { $$ = new NClassDeclaration(*$2, *$3, $5); delete $2; delete $3; delete $5; $$->location = @$; }
        ;

extends
        : { $$ = new std::string("obj"); }
        | TEXTENDS TIDENTIFIER { $$ = $2; delete $2; }
        ;

class_members
        : class_members field { $1->children.push_back($2); }
        | class_members method { $1->children.push_back($2); }
        | { $$ = new ASTNode(); }

field
        : type TIDENTIFIER TSEMI { $$ = new NField(*$1, *$2); delete $1; delete $2; $$->location = @$; }
        ;

method
        : type TIDENTIFIER TLPAREN proto_args ellipse_arg TRPAREN TLBRACE stmts TRBRACE
         { $$ = new NMethodFunction(*$1, *$2, *$4, $5, *$8); delete $2; delete $4; delete $8; $$->location = @$; }
        ;

///// Statements //////

block
        : TLBRACE stmts TRBRACE { $$ = new NBlock(*$2); delete $2; $$->location = @$; }
        ;

stmts
        : stmts stmt { $1->push_back($2); }
        | { $$ = new StatementList(); }
        ;


/// a statement is something that does not return a value. For example, var decalaration, if, for, while, etc...

stmt    : exprstmt TSEMI { $$ = $1; }
        | callable TSEMI { $$ = new NExpressionStatement($1); }
        | TRETURN expr TSEMI { $$ = new NReturn($2); $$->location = @1; }
        | TIF TLPAREN expr TRPAREN stmt { $$ = new NIfStatement($3, $5, NULL); $$->location = @$; } %prec ELSE
        | TIF TLPAREN expr TRPAREN stmt TELSE stmt { $$ = new NIfStatement($3, $5, $7); $$->location = @$; }
        | TFOR TLPAREN exprstmt TSEMI expr TSEMI exprstmt TRPAREN stmt { $$ = new NForLoop($3, $5, $7, $9); $$->location = @$; }
        | block
        ;

exprstmt
        : lvalue TEQUAL expr { $$ = new NAssignment($1, $3); $$->location = @$; }
        | var_decl
        ;

var_decl : type TIDENTIFIER { $$ = new NVariableDeclaration($1, *$2); delete $2; $$->location = @2; }
         | type TIDENTIFIER TEQUAL expr { $$ = new NVariableDeclaration($1, *$2, $4); delete $2; $$->location = @2; }
         ;

type
        : TIDENTIFIER numPointers { $$ = NType::GetPointerType(*$1, $2); delete $1; $$->location = @$; }
        | TIDENTIFIER TLBRACKET TINTEGER TRBRACKET { $$ = NType::GetArrayType(*$1, $3); delete $1; $$->location = @$; }
        ;

numPointers
        : numPointers TMUL { $$ += 1; }
        | { $$ = 0; }
        ;


// expr is something that always returns a value

expr
        : compexpr { $$ = $1; }
        ;

compexpr
        : addexpr TCEQ addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::Equal); $$->location = @$; }
        | addexpr TCNE addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::NotEqual); $$->location = @$; }
        | addexpr TCLT addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::LessThan); $$->location = @$; }
        | addexpr TCLE addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::LessThanEqual); $$->location = @$; }
        | addexpr TCGT addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::GreaterThan); $$->location = @$; }
        | addexpr TCGE addexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::GreaterThanEqual); $$->location = @$; }
        | addexpr { $$ = $1; }
        ;

addexpr : multexpr TPLUS multexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::Add); $$->location = @$; }
        | multexpr TMINUS multexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::Sub); $$->location = @$; }
        | multexpr { $$ = $1; }
        ;

multexpr : unaryexpr TMUL unaryexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::Mul); $$->location = @$; }
         | unaryexpr TDIV unaryexpr { $$ = new NBinaryOperator($1, $3, NBinaryOperator::Operator::Div); $$->location = @$; }
         | unaryexpr { $$ = $1; }
         ;

unaryexpr
        : TNOT primary { $$ = new NNot($2); $$->location = @$; }
        | TMINUS primary { $$ = new NNegitive($2); $$->location = @$; }
        | primary
        ;

primary
        : TLPAREN expr TRPAREN { $$ = $2; }
        | literal { $$ = $1; }
        | TSIZEOF TLPAREN type TRPAREN { $$ = new NSizeOf($3); $$->location = @$; }
        | TNEW TIDENTIFIER { $$ = new NNew(*$2); delete $2; $$->location = @$; }
        | TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NFunctionCall(*$1, *$3); $$->location = @$; delete $1; delete $3; }
        | p_1 { $$ = new NLoad($1); }
        ;

p_1
        : p_1 TDOT TIDENTIFIER { $$ = new NMemberAccess(new NLoad($1), *$3); delete $3; $$->location = @$; }
        | p_1 TDOT TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NMethodCall(new NLoad($1), *$3, *$5); delete $3; delete $5; $$->location = @$; }
        | p_1 TAT arrayindex { $$ = new NArrayElementPtr(new NLoad($1), $3); $$->location = @$; }
        | ident
        ;

callable
        : TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NFunctionCall(*$1, *$3); $$->location = @$; delete $1; delete $3; }
        | lvalue
        ;

lvalue
        : ident
        | lvalue TDOT TIDENTIFIER { $$ = new NMemberAccess(new NLoad($1), *$3); delete $3; $$->location = @$; }
        | lvalue TAT arrayindex { $$ = new NArrayElementPtr(new NLoad($1), $3); $$->location = @$; }
        | lvalue TDOT TIDENTIFIER TLPAREN expr_list TRPAREN { $$ = new NMethodCall(new NLoad($1), *$3, *$5); delete $3; delete $5; $$->location = @$; }
        ;

literal : TINTEGER { $$ = new NIntLiteral($1); $$->location = @$; }
        | TFLOAT { $$ = new NFloatLiteral($1); $$->location = @$; }
        | TSTRINGLIT { $$ = new NStringLiteral(*$1); delete $1; $$->location = @$; }
        ;


ident
        : TIDENTIFIER { $$ = new NIdentifier(*$1); delete $1; $$->location = @$; }
        ;

expr_list
        : expr { $$ = new ExpressionList(); $$->push_back($1); }
        | expr_list TCOMMA expr { $$->push_back($3); }
        | { $$ = new ExpressionList(); }
        ;


arrayindex
        : ident { $$ = $1; }
        | TINTEGER { $$ = new NIntLiteral($1); $$->location = @$; }
        | TLPAREN expr TRPAREN { $$ = $2; }
        ;


%%




