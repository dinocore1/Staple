#include "compilercontext.h"

namespace staple {

using namespace std;


    StapleClassDef* STP_OBJ_CLASSDEF = new StapleClassDef();
    StapleClass* STP_OBJ_CLASS = new StapleClass("obj", nullptr);

    StapleClass* CompilerContext::getStpObjClass() {
        return STP_OBJ_CLASS;
    }

    StapleClassDef* CompilerContext::getStpObjClassDef() {
        return STP_OBJ_CLASSDEF;
    }


CompilerContext::CompilerContext() {

    STP_OBJ_CLASS->addField("class", new StaplePointer(STP_OBJ_CLASSDEF));
    STP_OBJ_CLASS->addField("refCount", StapleType::getInt32Type());

    vector<StapleType*> args { };
    STP_OBJ_CLASS->addMethod("kill", StapleType::getVoidType(), args, false);

    defineClass(STP_OBJ_CLASS);
};

void CompilerContext::defineClass(StapleClass *localClass) {
    string fqClassName = localClass->getClassName();
    mClasses[fqClassName] = localClass;
}

StapleClass* CompilerContext::lookupClassName(const std::string &className) {

    StapleClass* retval = NULL;
    //first try locally-defined classname
    string fqClassName = !package.empty() ? (package + "." + className) : className;
    auto it = mClasses.find(fqClassName);
    if(it != mClasses.end()) {
        retval = (*it).second;
    } else {
        //TODO: loop though all the imports and try to find the class
    }

    return retval;
}

}