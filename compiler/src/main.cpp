#include <cstdio>
#include <iostream>
#include <fstream>
#include <system_error>

extern int yydebug;


#include "compilercontext.h"
#include "node.h"
#include "parsercontext.h"
#include "sempass.h"
#include "codegen/LLVMCodeGenerator.h"

#include <llvm/Support/FileSystem.h>
#include <llvm/Support/raw_ostream.h>
#include <llvm/Support/CommandLine.h>



using namespace std;
using namespace staple;


cl::opt<string> OutputFilename("o", cl::desc("output filename"), cl::value_desc("filename"), cl::init("output.ll"));
cl::opt<string> InputFilename(cl::Positional, cl::desc("<input file>"), cl::Required);
cl::opt<bool> DebugSymbols("g", cl::desc("output debug symbols"));
cl::list<std::string> IncludePaths("I", cl::Prefix, cl::desc("include filepath root"), cl::ZeroOrMore);

int main(int argc, char **argv)
{
    cl::ParseCommandLineOptions(argc, argv);


    CompilerContext context;
    context.inputFilename = InputFilename;
    context.outputFilename = OutputFilename;
    context.debugSymobols = DebugSymbols;
    context.searchPaths = IncludePaths;

    yydebug = 1;

    ifstream inputFileStream(context.inputFilename.c_str(), std::ifstream::binary);
    if (!inputFileStream) {
        fprintf(stderr, "cannot open file: %s", context.inputFilename.c_str());
        return -1;
    }

    ParserContext parserContext(&inputFileStream);

    yyparse(&parserContext);


    context.package = parserContext.compileUnit->package;
    context.includes = parserContext.compileUnit->includes;

    staple::SemPass semPass(context);
    semPass.doSemPass(*parserContext.compileUnit);

    if(semPass.hasErrors()) {
        exit(1);
    }

    LLVMCodeGenerator codeGenerator(&context);
    codeGenerator.generateCode(parserContext.compileUnit);

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
