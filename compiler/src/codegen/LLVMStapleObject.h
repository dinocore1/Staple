

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
        map<StapleMethodFunction*, llvm::Function*> mMethodMap;

    protected:
        StapleClass* mClassType;
        llvm::StructType* mClassDefType;
        llvm::GlobalVariable* mClassNameValue;
        llvm::GlobalVariable* mClassDefValue;
        llvm::Constant* mClassVTableValue;
        llvm::StructType* mVtableType;
        llvm::StructType* mObjectStruct;
        llvm::StructType* mFieldsStruct;
        llvm::Function* mInitFunction;
        llvm::Function* mKillFunction;

        LLVMStapleObject(StapleClass* classType);

    public:

        static llvm::StructType* getStpObjInstanceType();
        static llvm::StructType* getStpClassDefType();
        static llvm::StructType* getStpObjVtableType();
        static llvm::FunctionType* getKillFunctionType();
        static llvm::GlobalVariable* getStpClassValue();

        static llvm::Function* getStoreStrongFunction(Module* module);

        static LLVMStapleObject* get(StapleClass* classType);

        llvm::Value* getFieldPtr(const string& name, llvm::IRBuilder<>& irBuilder, llvm::Value* thisPtr);

        virtual llvm::GlobalVariable* getClassDefinition(LLVMCodeGenerator* codeGenerator);
        llvm::GlobalVariable* getClassNameValue(LLVMCodeGenerator* codeGenerator);
        llvm::Constant* getClassVTableValue(LLVMCodeGenerator* codeGenerator);

        virtual llvm::StructType* getClassDefType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::StructType* getVtableType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator);
        virtual llvm::Function* getInitFunction(LLVMCodeGenerator* codeGenerator);


    };

}


#endif //STAPLE_LLVMSTAPLEOBJECT_H
