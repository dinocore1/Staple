#include "compilercontext.h"


using namespace std;

CompilerContext::CompilerContext()
{

};

void CompilerContext::defineClass(SClassType *localClass) {
    string fqClassName = !package.empty() ? (package + "." + localClass->name) : localClass->name;
    classes[fqClassName] = localClass;
}

SClassType* CompilerContext::lookupClassName(const std::string &className) {

    SClassType* retval = NULL;
    //first try locally-defined classname
    string fqClassName = !package.empty() ? (package + "." + className) : className;
    auto it = classes.find(fqClassName);
    if(it != classes.end()) {
        retval = (*it).second;
    } else {
        //TODO: loop though all the imports and try to find the class
    }

    return retval;
}