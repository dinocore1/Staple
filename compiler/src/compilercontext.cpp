#include <cstdarg>
#include <fstream>

#include "compilercontext.h"

#include <llvm/Support/FileSystem.h>

namespace staple {

using namespace llvm;
using namespace std;


    StapleClass* STP_OBJ_CLASS = new StapleClass("obj", nullptr);
    StapleClassDef* STP_OBJ_CLASSDEF = new StapleClassDef(STP_OBJ_CLASS);


    StapleClass* CompilerContext::getStpObjClass() {
        return STP_OBJ_CLASS;
    }

    StapleClassDef* CompilerContext::getStpObjClassDef() {
        return STP_OBJ_CLASSDEF;
    }


    CompilerContext::CompilerContext()
    : numErrors(0) {
        STP_OBJ_CLASS->addField("class", new StaplePointer(new StapleClassDef(STP_OBJ_CLASS)));
        //STP_OBJ_CLASS->addField("refCount", StapleType::getInt32Type());

        {
            vector<StapleType *> args{};
            STP_OBJ_CLASS->addMethod("init", StapleType::getVoidType(), args, false, StapleMethodFunction::Type::Static);
        }

        {
            vector<StapleType *> args{};
            STP_OBJ_CLASS->addMethod("kill", StapleType::getVoidType(), args, false, StapleMethodFunction::Type::Virtual);
        }

        mRootScope.table[STP_OBJ_CLASS->getClassName()] = STP_OBJ_CLASS;

    }


    bool CompilerContext::hasErrors() {
        return numErrors > 0;
    }

    void CompilerContext::logError(YYLTYPE location, const char *format, ...)
    {
        numErrors++;
        va_list argptr;
        va_start(argptr, format);

        fprintf(stderr, "%s:%d:%d: ", inputFilename.c_str(), location.first_line, location.first_column);
        fprintf(stderr, "error: ");
        vfprintf(stderr, format, argptr);
        va_end(argptr);
    }

    void CompilerContext::logWarning(YYLTYPE location, const char *format, ...)
    {
        va_list argptr;
        va_start(argptr, format);

        fprintf(stderr, "%s:%d:%d: ", inputFilename.c_str(), location.first_line, location.first_column);
        fprintf(stderr, "warning: ");
        vfprintf(stderr, format, argptr);
        va_end(argptr);
    }

}