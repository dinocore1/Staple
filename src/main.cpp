#include <cstdio>
#include <iostream>
#include "codegen.h"
#include "node.h"
extern NBlock* programBlock;

extern "C" int yylex();
int yyparse();
extern "C" FILE *yyin;

using namespace std;

int main(int argc, char **argv)
{

    // open a file handle to a particular file:
    FILE *myfile = fopen("test.stp", "r");
    // make sure it's valid:
    if (!myfile) {
        cout << "I can't open a.snazzle.file!" << endl;
        return -1;
    }
    // set lex to read from it instead of defaulting to STDIN:
    yyin = myfile;

    // parse through the input until there is no more:
    do {
        yyparse();
    } while (!feof(yyin));

    CodeGenContext context;
    context.generateCode(*programBlock);
    return 0;
}
