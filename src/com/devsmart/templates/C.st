group C;

code_unit(packagename, code) ::= <<
#include "stapleruntime.h"

<code>
>>

class_def(name, superclass, fields, code) ::= <<
typedef struct stpl_<name; separator="_"> {
	<fields; separator=",\n">
} stpl_<name; separator="_">;

<code>
>>

bool_field_def(name) ::= "BOOL <name>"
int_field_def(name) ::= "int <name>"
obj_field_def(name) ::= "STP_OBJ_PTR(<name>)"

bool_arg(name) ::= "BOOL <name>"
int_arg(name) ::= "int <name>"
obj_arg(name) ::= "STP_OBJ_PTR(<name>)"

bool_var(name) ::= "BOOL <name>"
int_var(name) ::= "int <name>"
obj_var(name) ::= "STP_OBJ_PTR(<name>)"

void_return_def() ::= "void"
int_return_def() ::= "int"
bool_return_def() ::= "BOOL"
obj_return_def() ::= "void*"

basic_block(code) ::= <<
{
	<code>
}
>>

assignment(lside, rside) ::= "<lside> = <rside>"

statement(code) ::= "<code>;"

varref(name) ::= "<name>"

add(lside, rside) ::= "<lside> + <rside>"
subtract(lside, rside) ::= "<lside> - <rside>"
multiply(lside, rside) ::= "<lside> * <rside>"
divide(lside, rside) ::= "<lside> / <rside>"
int_literal(value) ::= "<value>"

method(name, return, formals, code) ::= <<
<return> <name; separator="_">(<formals; separator=", ">) <code>
>>

call(obj, mname, args) ::= <<<mname>(<args; separator=", ">)>>