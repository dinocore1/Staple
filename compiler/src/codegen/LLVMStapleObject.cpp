

#include "LLVMStapleObject.h"
#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"

namespace staple {

    using namespace llvm;

    StructType* STPOBJ_CLASS_TYPE = StructType::create(getGlobalContext(), "stp_class");
    StructType* STPOBJ_INSTANCE_TYPE = StructType::create(getGlobalContext(), "stp_obj");

    FunctionType* getDestroyFunction() {

        vector<Type*> args {STPOBJ_INSTANCE_TYPE};

        FunctionType* retval = FunctionType::get(
                Type::getVoidTy(getGlobalContext()),
                args,
                false
        );

        return retval;
    }

    StructType* LLVMStapleObject::getStpClassType() {
        if(STPOBJ_CLASS_TYPE->isEmptyTy()) {
            STPOBJ_CLASS_TYPE->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    PointerType::getUnqual(getDestroyFunction())
                    NULL
            );
        }
        return STPOBJ_CLASS_TYPE;
    }



    StructType* LLVMStapleObject::getStpObjInstanceType() {
        if(STPOBJ_INSTANCE_TYPE->isEmptyTy()){
            STPOBJ_INSTANCE_TYPE->setBody(
                    PointerType::getUnqual(LLVMStapleObject::getStpClassType()),
                    Type::getInt32Ty(getGlobalContext()),
                    NULL
            );
        }
        return STPOBJ_INSTANCE_TYPE;
    }

    map<StapleClass*, LLVMStapleObject*> LLVMStapleObject::Cache;

    LLVMStapleObject::LLVMStapleObject(StapleClass *classType)
    : mClassType(classType), mObjectStruct(nullptr), mInitFunction(nullptr) {

    }

    LLVMStapleObject* LLVMStapleObject::get(StapleClass* classType) {

        LLVMStapleObject* retval = nullptr;
        auto it = Cache.find(classType);
        if(it == Cache.end()) {
            retval = new LLVMStapleObject(classType);
            Cache[classType] = retval;
        } else {
            retval = it->second;
        }

        return retval;
    }


    Function *LLVMStapleObject::getInitFunction(LLVMCodeGenerator *codeGenerator) {
        if(mInitFunction == nullptr) {
            FunctionType* functionType = FunctionType::get(
                    Type::getVoidTy(getGlobalContext()),
                    vector<Type*>{PointerType::getUnqual(codeGenerator->getLLVMType(mClassType))},
                    false
            );

            string functionName = codeGenerator->createFunctionName(mClassType->getSimpleName() + "_init");
            mInitFunction = Function::Create(functionType, Function::LinkageTypes::ExternalLinkage, functionName, &codeGenerator->mModule);
        }

        return mInitFunction;
    }

    llvm::StructType* LLVMStapleObject::getObjectType(LLVMCodeGenerator *codeGenerator) {
        if(mObjectStruct == nullptr) {
            mObjectStruct = StructType::create(getGlobalContext(), codeGenerator->createFunctionName(mClassType->getSimpleName()));

            vector<Type*> elements;
            for(StapleType* stapleType : mClassType->getFields()) {
                elements.push_back(codeGenerator->getLLVMType(stapleType));
            }
            mObjectStruct->setBody(elements);
        }

        return mObjectStruct;
    }
} // namespace staple