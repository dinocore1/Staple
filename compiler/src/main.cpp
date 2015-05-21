#include <cstdio>
#include <iostream>
#include <system_error>

#include "compilercontext.h"
#include "node.h"
#include "sempass.h"
#include "codegen/LLVMCodeGenerator.h"

#include <llvm/Support/FileSystem.h>
#include <llvm/Support/raw_ostream.h>
#include <llvm/Support/CommandLine.h>

extern "C" int yylex();
int yyparse();
extern "C" FILE *yyin;
extern "C" int yydebug;

char *filename;

using namespace std;
using namespace staple;

extern NCompileUnit* compileUnit;




cl::opt<string> OutputFilename("o", cl::desc("output filename"), cl::value_desc("filename"), cl::init("output.ll"));
cl::opt<string> InputFilename(cl::Positional, cl::desc("<input file>"), cl::Required);
cl::opt<bool> DebugSymbols("g", cl::desc("output debug symbols"));

int main(int argc, char **argv)
{
    cl::ParseCommandLineOptions(argc, argv);


    CompilerContext context;
    context.inputFilename = InputFilename;
    context.outputFilename = OutputFilename;
    context.debugSymobols = DebugSymbols;

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
