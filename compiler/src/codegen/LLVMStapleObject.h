

#ifndef STAPLE_LLVMSTAPLEOBJECT_H
#define STAPLE_LLVMSTAPLEOBJECT_H

#include "../types/stapletype.h"
#include "LLVMCodeGenerator.h"

#include <map>

#include <llvm/IR/Function.h>
#include <llvm/IR/IRBuilder.h>

namespace staple {

    using namespace std;

    class LLVMCodeGenerator;

    class LLVMStapleObject {

    private:
        static map<StapleClass*, LLVMStapleObject*> Cache;
        static llvm::StructType* StpObjInstanceStruct;
        static llvm::StructType* getStpObjVtableType();
        static llvm::StructType* StpClassStruct;
        static llvm::StructType* getStpInstanceHeaderType();

    protected:
        StapleClass* mClassType;
        llvm::StructType* mClassDefType;
        llvm::StructType* mVtableType;
        llvm::StructType* mObjectStruct;
        llvm::StructType* mFieldsStruct;
        llvm::Function* mInitFunction;
        llvm::Function* mKillFunction;

        LLVMStapleObject(StapleClass* classType);

    public:

        static llvm::StructType* getStpObjInstanceType();
        static llvm::StructType* getStpRuntimeClassType();
        static llvm::FunctionType* getKillFunctionType();

        static LLVMStapleObject* get(StapleClass* classType);

        llvm::Value* getFieldPtr(const string& name, llvm::IRBuilder<>& irBuilder, llvm::Value* thisPtr);

        llvm::GlobalVariable* getClassDefinition(LLVMCodeGenerator* codeGenerator);

        virtual llvm::StructType* getClassDefType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::StructType* getVtableType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::Function* getInitFunction(LLVMCodeGenerator* codeGenerator);


    };

}


#endif //STAPLE_LLVMSTAPLEOBJECT_H
