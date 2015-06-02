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


CompilerContext::CompilerContext() {

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


    defineClass(STP_OBJ_CLASS);
};

void CompilerContext::defineClass(StapleClass *localClass) {
    string fqClassName = localClass->getClassName();
    mClasses[fqClassName] = localClass;
}

std::string ReplaceString(std::string subject, const std::string& search,
                          const std::string& replace) {
    size_t pos = 0;
    while ((pos = subject.find(search, pos)) != std::string::npos) {
        subject.replace(pos, search.length(), replace);
        pos += replace.length();
    }
    return subject;
}

StapleClass* CompilerContext::lookupClassName(const std::string &className) {

    auto it = mClasses.find(className);
    if(it != mClasses.end()) {
        return it->second;
    }

    //try local package
    string localclassname = package + "." + className;
    it = mClasses.find(localclassname);
    if(it != mClasses.end()) {
        return it->second;
    }

    for(string package : includes) {
        string fqClassName = package + "." + className;
        it = mClasses.find(fqClassName);
        if(it != mClasses.end()) {
            return it->second;
        }
    }

    #define path_sep "/"

    for(string path : searchPaths) {
        if(sys::fs::is_directory(path)) {

            for(string import : includes){
                string srcFilePath = path + path_sep + ReplaceString(import, ".", path_sep) + ".stp";
                if(sys::fs::is_regular_file(srcFilePath)) {

                    ifstream inputFileStream(srcFilePath);
                    if (!inputFileStream) {
                        fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                    } else {

                        ParserContext parserContext(&inputFileStream);
                        yyparse(&parserContext);



                    }


                }

            }

            /*
            error_code ec;
            for(sys::fs::directory_iterator it(path, ec); !ec; it = it.increment(ec)) {

                sys::fs::directory_entry entry entry = it->directory_entry();
                string path = entry.path();


            }
             */

        }
    }


    return nullptr;
}

}