#include <cstdio>
#include <iostream>
#include <system_error>

#include "compilercontext.h"
#include "node.h"
#include "sempass.h"
#include "codegen/LLVMCodeGenerator.h"

#include <llvm/Support/FileSystem.h>
#include <llvm/Support/raw_ostream.h>

#include "optionparser.h"

extern "C" int yylex();
int yyparse();
extern "C" FILE *yyin;
extern "C" int yydebug;

char *filename;

using namespace std;
using namespace staple;

extern NCompileUnit* compileUnit;


struct Arg : public option::Arg
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

enum optionIndex { UNKNOWN, PACKAGE, OUTPUT, INPUT, DEBUG };
const option::Descriptor usage[] =
{
    {UNKNOWN, 0, "", "", option::Arg::None, "USAGE: stp [-o] output.ll input.stp\n\n"
                                                    "Options:"},
    {PACKAGE, 0, "p", "package", Arg::Required, "-p <package name>, --package <package name> \tThe package name"},
    {OUTPUT, 0, "o", "output", Arg::Required, "-o <output.ll>, --output <output.ll> \tThe output LLVM file"},
    {DEBUG, 0, "g", "debug", Arg::None, "-g\toutput debug symbols"},
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

    CompilerContext context;
    context.inputFilename = parse.nonOption(0);

    if(options[OUTPUT]) {
        context.outputFilename = options[OUTPUT].arg;
    } else {
        context.outputFilename = "output.ll";
    }

    if(options[PACKAGE]) {
        context.package = options[PACKAGE].arg;
    } else {
        context.package = "";
    }

    context.debugSymobols = options[DEBUG] ? true : false;

    //yydebug = 1;

    FILE *myfile = fopen(context.inputFilename.c_str(), "r");
    if (!myfile) {
        fprintf(stderr, "cannot open file: %s", context.inputFilename.c_str());
        return -1;
    }
    // set lex to read from it instead of defaulting to STDIN:
    yyin = myfile;

    // parse through the input until there is no more:
    do {
        yyparse();
    } while (!feof(yyin));

    staple::SemPass semPass(context);
    semPass.doSemPass(*compileUnit);

    if(semPass.hasErrors()) {
        exit(1);
    }

    LLVMCodeGenerator codeGenerator(&context);
    codeGenerator.generateCode(compileUnit);

    //CodeGenContext codeGen(context);
    //codeGen.generateCode(*compileUnit);

    std::string errorCode;
    raw_fd_ostream output(context.outputFilename.c_str(), errorCode, sys::fs::OpenFlags::F_None);

    codeGenerator.getModule()->print(output, NULL);

    /**
    * could also output to llvm bitcode using:
    * #include <llvm/Bitcode/ReaderWriter.h>
    *   WriteBitcodeToFile
    */

    return 0;
}
