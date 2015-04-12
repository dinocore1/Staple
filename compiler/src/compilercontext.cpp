#include "compilercontext.h"

namespace staple {

using namespace std;

CompilerContext::CompilerContext()
{

};

void CompilerContext::defineClass(StapleClass *localClass) {
    string fqClassName = localClass->getClassName();
    classes[fqClassName] = localClass;
}

StapleClass* CompilerContext::lookupClassName(const std::string &className) {

    StapleClass* retval = NULL;
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

}