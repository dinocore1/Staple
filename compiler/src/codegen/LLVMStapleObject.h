

#ifndef STAPLE_LLVMSTAPLEOBJECT_H
#define STAPLE_LLVMSTAPLEOBJECT_H

#include "../types/stapletype.h"

#include <map>

#include <llvm/IR/Function.h>

namespace staple {

    using namespace std;

    class LLVMCodeGenerator;

    class LLVMStapleObject {

    private:
        static map<StapleClass*, LLVMStapleObject*> Cache;
        static llvm::StructType* StpObjInstanceStruct;
        static llvm::StructType* StpClassStruct;

        StapleClass* mClassType;
        llvm::StructType* mObjectStruct;
        llvm::Function* mInitFunction;

        LLVMStapleObject(StapleClass* classType);

    public:

        static llvm::StructType* getStpObjInstanceType();
        static llvm::StructType* getStpClassType();

        static LLVMStapleObject* get(StapleClass* classType);

        llvm::GlobalVariable* getClassDefinition(LLVMCodeGenerator* codeGenerator);

        llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator);

        llvm::Function* getInitFunction(LLVMCodeGenerator* codeGenerator);
        void emitInitFunction(LLVMCodeGenerator* codeGenerator);

        llvm::Function* getDestroyFunction(LLVMCodeGenerator* codeGenerator);
        void emitDestroyFunction(LLVMCodeGenerator* codeGenerator);


    };

}


#endif //STAPLE_LLVMSTAPLEOBJECT_H
