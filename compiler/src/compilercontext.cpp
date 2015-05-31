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


    //TODO: loop though all the imports and try to find the class
    for(string path : searchPaths) {
        if(sys::fs::is_directory(path)) {
            error_code ec;
            for(sys::fs::directory_iterator it(path, ec); !ec; it = it .increment(ec)) {

            }

        }
    }


    return nullptr;
}

}