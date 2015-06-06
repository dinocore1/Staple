#ifndef COMPILERCONTEXT_H_
#define COMPILERCONTEXT_H_

#include <string>
#include <map>

#include "scope.h"
#include "node.h"
#include "parsercontext.h"
#include "types/stapletype.h"

#include <llvm/IR/Type.h>

namespace staple {

    using namespace std;



    class CompilerContext {
    private:
        unsigned int numErrors;

    public:
        string inputFilename;
        string outputFilename;
        bool debugSymobols;
        vector<string> searchPaths;

        NCompileUnit* mCompileUnit;
        Scope mRootScope;

        static StapleClass* getStpObjClass();
        static StapleClassDef* getStpObjClassDef();


        map<ASTNode*, StapleType*> typeTable;
        map<StapleType*, llvm::Type*> llvmType;


        CompilerContext();

        bool hasErrors();
        void logError(YYLTYPE location, const char* format, ...);
        void logWarning(YYLTYPE location, const char* format, ...);


    };

} // namespace staple

#endif /* COMPILERCONTEXT_H_ */