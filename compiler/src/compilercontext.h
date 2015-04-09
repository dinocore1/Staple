#ifndef COMPILERCONTEXT_H_
#define COMPILERCONTEXT_H_

#include <string>
#include <map>

#include "node.h"
#include "types/stapletype.h"

using namespace staple;

class CompilerContext {
public:
    std::string inputFilename;
    std::string outputFilename;

    std::string package;
    std::vector<std::string> includes;

    //fully qualifed name class map
    std::map<std::string, StapleClass*> classes;

    void defineClass(StapleClass* localClass);
    StapleClass* lookupClassName(const std::string& className);

    std::map<ASTNode*, StapleType*> typeTable;

    CompilerContext();


};

#endif /* COMPILERCONTEXT_H_ */