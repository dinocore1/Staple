#ifndef COMPILERCONTEXT_H_
#define COMPILERCONTEXT_H_

#include <string>
#include <map>

#include "node.h"
#include "type.h"

class CompilerContext {
public:
    std::string inputFilename;
    std::string outputFilename;

    std::string package;
    std::vector<std::string> includes;

    //fully qualifed name class map
    std::map<std::string, SClassType*> classes;

    void defineClass(SClassType* localClass);
    SClassType* lookupClassName(const std::string& className);

    std::map<ASTNode*, SType*> typeTable;

    CompilerContext();


};

#endif /* COMPILERCONTEXT_H_ */