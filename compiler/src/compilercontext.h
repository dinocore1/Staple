#ifndef COMPILERCONTEXT_H_
#define COMPILERCONTEXT_H_

#include <string>
#include <map>

#include "node.h"
#include "types/stapletype.h"

#include <llvm/IR/Type.h>

namespace staple {

    using namespace std;

    class CompilerContext {
    public:
        string inputFilename;
        string outputFilename;

        string package;
        vector<string> includes;

        //fully qualifed name class map
        map<string, StapleClass*> classes;

        void defineClass(StapleClass *localClass);

        StapleClass *lookupClassName(const string &className);

        map<ASTNode*, StapleType*> typeTable;
        map<StapleType*, llvm::Type*> llvmType;

        CompilerContext();


    };

} // namespace staple

#endif /* COMPILERCONTEXT_H_ */