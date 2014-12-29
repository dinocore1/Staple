#include <cstdio>
#include <iostream>
#include "codegen.h"
#include "node.h"

#include "optionparser.h"
extern NCompileUnit* compileUnit;

extern "C" int yylex();
int yyparse();
extern "C" FILE *yyin;
extern "C" int yydebug;

using namespace std;

struct Arg: public option::Arg
{
    static option::ArgStatus Required(const option::Option& option, bool msg)
    {
        if(option.arg > 0){
            return option::ARG_OK;
        } else {
            fprintf(stderr, "Option: %s requires an argument\n", option.name);
            return option::ARG_ILLEGAL;
        }
    }
};

enum optionIndex { UNKNOWN, OUTPUT, INPUT };
const option::Descriptor usage[] =
{
    {UNKNOWN, 0, "", "", option::Arg::None, "USAGE: staple [-o] output.ll input.stp"},
    {OUTPUT, 0, "o", "output", Arg::Required, "-o <output.ll>, --output <output.ll> \tThe output LLVM file"},
    {UNKNOWN, 0, "", "", option::Arg::None, "<input.stp>\tThe input file"},
    { 0, 0, 0, 0, 0, 0 }
};

int main(int argc, char **argv)
{

    argc-=(argc>0); argv+=(argc>0); // skip program name argv[0] if present
    option::Stats stats(usage, argc, argv);
    option::Option options[stats.options_max], buffer[stats.buffer_max];
    option::Parser parse(usage, argc, argv, options, buffer);

    if (parse.error())
        return 1;

    if(argc == 0){
        int columns = getenv("COLUMNS")? atoi(getenv("COLUMNS")) : 80;
        option::printUsage(fwrite, stdout, usage, columns);
        return 0;
    }

    const char* inputfile = parse.nonOption(0);
    const char* outputfile;
    if(options[OUTPUT]) {
        outputfile = options[OUTPUT].name;
    } else {
        outputfile = "output.ll";
    }

    yydebug = 1;

    FILE *myfile = fopen(inputfile, "r");
    if (!myfile) {
        fprintf(stderr, "cannot open file: %s", inputfile);
        return -1;
    }
    // set lex to read from it instead of defaulting to STDIN:
    yyin = myfile;

    // parse through the input until there is no more:
    do {
        yyparse();
    } while (!feof(yyin));

    CodeGenContext context(inputfile);
    context.generateCode(*compileUnit);
    return 0;
}
