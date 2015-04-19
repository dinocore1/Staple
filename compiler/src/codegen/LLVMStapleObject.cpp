

#include "LLVMStapleObject.h"
#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"

namespace staple {

    using namespace llvm;

    StructType* STPOBJ_CLASS_TYPE = StructType::create(getGlobalContext(), "stp_class");
    StructType* STPOBJ_INSTANCE_TYPE = StructType::create(getGlobalContext(), "stp_obj");

    StructType* LLVMStapleObject::getStpRuntimeClassType() {
        if(STPOBJ_CLASS_TYPE->isEmptyTy()) {
            STPOBJ_CLASS_TYPE->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    PointerType::getUnqual(LLVMStapleObject::getKillFunctionType()),
                    NULL
            );
        }
        return STPOBJ_CLASS_TYPE;
    }

    FunctionType* LLVMStapleObject::getKillFunctionType() {
        vector<Type*> args {STPOBJ_INSTANCE_TYPE};
        FunctionType* retval = FunctionType::get(
                Type::getVoidTy(getGlobalContext()),
                args,
                false
        );

        return retval;
    }


    StructType* LLVMStapleObject::getStpObjInstanceType() {
        if(STPOBJ_INSTANCE_TYPE->isEmptyTy()){
            STPOBJ_INSTANCE_TYPE->setBody(
                    PointerType::getUnqual(LLVMStapleObject::getStpRuntimeClassType()),
                    Type::getInt32Ty(getGlobalContext()), //refCount
                    NULL
            );
        }
        return STPOBJ_INSTANCE_TYPE;
    }

    class LLVMBaseObject : public LLVMStapleObject {

    public:
        LLVMBaseObject() : LLVMStapleObject(CompilerContext::getStpObjClass()) {

        }

        Function* getInitFunction(LLVMCodeGenerator *codeGenerator) {
            if(mInitFunction == nullptr) {
                FunctionType *functionType = FunctionType::get(
                        Type::getVoidTy(getGlobalContext()),
                        vector<Type *>{PointerType::getUnqual(STPOBJ_INSTANCE_TYPE)},
                        false
                );

                mInitFunction = Function::Create(functionType,
                                                 Function::LinkageTypes::ExternalLinkage,
                                                 "obj_init",
                                                 &codeGenerator->mModule);

            }

            return mInitFunction;
        }

        llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator) {
            return STPOBJ_INSTANCE_TYPE;
        }
    };

    map<StapleClass*, LLVMStapleObject*> createCache() {
        map<StapleClass*, LLVMStapleObject*> retval;
        retval[CompilerContext::getStpObjClass()] = new LLVMBaseObject();
        return retval;
    }

    map<StapleClass*, LLVMStapleObject*> LLVMStapleObject::Cache = createCache();

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

    Value* LLVMStapleObject::getFieldPtr(const string& name, llvm::IRBuilder<> &irBuilder, llvm::Value *thisPtr) {
        uint fieldIndex = 0;
        mClassType->getField(name, fieldIndex);
        return irBuilder.CreateConstGEP2_32(thisPtr, 0, fieldIndex);

    }


    Function *LLVMStapleObject::getInitFunction(LLVMCodeGenerator *codeGenerator) {
        if(mInitFunction == nullptr) {
            FunctionType* functionType = FunctionType::get(
                    Type::getVoidTy(getGlobalContext()),
                    vector<Type*>{PointerType::getUnqual(getObjectType(codeGenerator))},
                    false
            );

            string functionName = codeGenerator->createFunctionName(mClassType->getSimpleName() + "_init");
            mInitFunction = Function::Create(functionType, Function::LinkageTypes::ExternalLinkage, functionName, &codeGenerator->mModule);

            BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", mInitFunction);
            IRBuilder<> irBuilder(bblock);

            Value* thisPtr = mInitFunction->arg_begin();

            if(mClassType->getParent() != nullptr) {
                LLVMStapleObject* parentStapleObj = LLVMStapleObject::get(mClassType->getParent());

                Type* destType = PointerType::getUnqual(parentStapleObj->getObjectType(codeGenerator));
                Value* superPtr = irBuilder.CreatePointerCast(thisPtr, destType);

                irBuilder.CreateCall(parentStapleObj->getInitFunction(codeGenerator), superPtr);
            }

            for(StapleField* field : mClassType->getFields()) {
                StapleType* fieldType = field->getElementType();
                if(StapleInt* intType = dyn_cast<StapleInt>(fieldType)) {
                    irBuilder.CreateStore(irBuilder.getIntN(intType->getWidth(), 0),
                                          getFieldPtr(field->getName(), irBuilder, thisPtr));
                } else if(StaplePointer* ptrType = dyn_cast<StaplePointer>(fieldType)) {
                    irBuilder.CreateStore(ConstantPointerNull::get(PointerType::getUnqual(codeGenerator->getLLVMType(ptrType->getElementType()))),
                                          getFieldPtr(field->getName(), irBuilder, thisPtr));

                }
            }

            irBuilder.CreateRetVoid();
        }

        return mInitFunction;
    }

    void unrollFields(StapleClass* stapleClass, vector<Type*>& elements, LLVMCodeGenerator *codeGenerator) {
        if(stapleClass->getParent() != nullptr) {
            unrollFields(stapleClass->getParent(), elements, codeGenerator);
        }
        for(StapleType* stapleType : stapleClass->getFields()) {
            elements.push_back(codeGenerator->getLLVMType(stapleType));
        }
    }

    llvm::StructType* LLVMStapleObject::getObjectType(LLVMCodeGenerator *codeGenerator) {
        if(mObjectStruct == nullptr) {
            mObjectStruct = StructType::create(getGlobalContext(), codeGenerator->createFunctionName(mClassType->getSimpleName()));

            vector<Type*> elements;
            unrollFields(mClassType, elements, codeGenerator);

            mObjectStruct->setBody(elements);
        }

        return mObjectStruct;
    }
} // namespace staple