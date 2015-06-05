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

    class ImportManager;

    class CompilerContext {
    private:
        //fully qualifed name class map
        map<string, StapleClass*> mClasses;

    public:
        string inputFilename;
        string outputFilename;
        bool debugSymobols;
        vector<string> searchPaths;

        NCompileUnit* mCompileUnit;
        Scope mRootScope;

        static StapleClass* getStpObjClass();
        static StapleClassDef* getStpObjClassDef();

        StapleClass *lookupClassName(const string &className);

        map<ASTNode*, StapleType*> typeTable;
        map<StapleType*, llvm::Type*> llvmType;

        ImportManager* mImportManager;

        CompilerContext();


    };

} // namespace staple

#endif /* COMPILERCONTEXT_H_ */