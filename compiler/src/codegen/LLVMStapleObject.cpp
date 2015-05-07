

#include "LLVMStapleObject.h"
#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"

namespace staple {

    using namespace llvm;

    StructType* STPOBJ_INSTANCE_TYPE = StructType::create(getGlobalContext(), "stp_obj");
    StructType* STPOBJ_CLASS_TYPE = StructType::create(getGlobalContext(), "stp_class");
    StructType* STPOBJ_VTABLE_TYPE = StructType::create(getGlobalContext(), "stp_obj_vtable");

    StructType* LLVMStapleObject::getStpObjVtableType() {
        if(STPOBJ_VTABLE_TYPE->isEmptyTy()) {
            STPOBJ_VTABLE_TYPE->setBody(
              PointerType::getUnqual(LLVMStapleObject::getKillFunctionType()),
              NULL
            );
        }
        return STPOBJ_VTABLE_TYPE;
    }

    StructType* LLVMStapleObject::getStpRuntimeClassType() {
        if(STPOBJ_CLASS_TYPE->isEmptyTy()) {
            STPOBJ_CLASS_TYPE->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    PointerType::getUnqual(STPOBJ_CLASS_TYPE), // parent class
                    LLVMStapleObject::getStpObjVtableType(), // vtable
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

    Function* LLVMStapleObject::getStoreStrongFunction(Module *module) {
        Function* retval = module->getFunction("stp_storeStrong");
        if(retval == NULL) {
            FunctionType *ftype = FunctionType::get(Type::getVoidTy(getGlobalContext()),
                                                    std::vector<Type*>{
                                                            PointerType::getUnqual(PointerType::getUnqual(getStpObjInstanceType())),
                                                            PointerType::getUnqual(getStpObjInstanceType())
                                                    },
                                                    false);
            retval = Function::Create(ftype, GlobalValue::ExternalLinkage, "stp_storeStrong", module);
        }

        return retval;
    }


    StructType* LLVMStapleObject::getStpObjInstanceType() {
        if(STPOBJ_INSTANCE_TYPE->isEmptyTy()){
            STPOBJ_INSTANCE_TYPE->setBody(
                    PointerType::getUnqual(LLVMStapleObject::getStpRuntimeClassType()), // runtime class ptr
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
                                                 "stp_obj_init",
                                                 &codeGenerator->mModule);

            }

            return mInitFunction;
        }

        llvm::StructType* getClassDefType(LLVMCodeGenerator* codeGenerator) {
            return getStpRuntimeClassType();
        }


        llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator) {
            return getStpObjInstanceType();
        }

        /*

        llvm::StructType* getVtableType(LLVMCodeGenerator* codeGenerator) {
            if(mVtableType == nullptr) {
                mVtableType = StructType::create(getGlobalContext());
            }

            return mVtableType;
        }


        llvm::StructType* getFieldType(LLVMCodeGenerator* codeGenerator) {
            if(mFieldsStruct == nullptr) {
                mFieldsStruct = StructType::create(getGlobalContext());
                mFieldsStruct->setBody(
                        Type::getInt32Ty(getGlobalContext()),
                        NULL);
            }
            return mFieldsStruct;
        }


         */
    };

    map<StapleClass*, LLVMStapleObject*> createCache() {
        map<StapleClass*, LLVMStapleObject*> retval;
        retval[CompilerContext::getStpObjClass()] = new LLVMBaseObject();
        return retval;
    }

    map<StapleClass*, LLVMStapleObject*> LLVMStapleObject::Cache = createCache();

    LLVMStapleObject::LLVMStapleObject(StapleClass *classType)
    : mClassType(classType), mClassDefType(nullptr), mVtableType(nullptr), mObjectStruct(nullptr),
      mFieldsStruct(nullptr), mInitFunction(nullptr), mKillFunction(nullptr)
    {

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
                    irBuilder.CreateStore(irBuilder.getInt(APInt(intType->getWidth(), 0)),
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

    StructType* LLVMStapleObject::getClassDefType(LLVMCodeGenerator* codeGenerator) {
        if(mClassDefType == nullptr) {

            string classDefName = codeGenerator->createFunctionName(mClassType->getSimpleName()) + "_class";
            mClassDefType = StructType::create(getGlobalContext(), classDefName.c_str());

            mClassDefType->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    mClassType->getParent() != nullptr
                      ? PointerType::getUnqual(LLVMStapleObject::get(mClassType->getParent())->getClassDefType(codeGenerator))
                      : PointerType::getUnqual(STPOBJ_CLASS_TYPE), // parent class ptr
                    getVtableType(codeGenerator),
                    NULL);

        }
        return mClassDefType;
    }

    void unrollVtable(StapleClass* stapleClass, vector<Type*>& elements, LLVMCodeGenerator *codeGenerator) {

        if(stapleClass->getParent() != nullptr) {
            unrollVtable(stapleClass->getParent(), elements, codeGenerator);
        }

        for(StapleMethodFunction* methodFunction : stapleClass->getMethods()) {
            if(methodFunction->getType() == StapleMethodFunction::Type::Virtual) {
                elements.push_back(PointerType::getUnqual(codeGenerator->getLLVMType(methodFunction)));
            }
        }
    }

    StructType* LLVMStapleObject::getVtableType(LLVMCodeGenerator *codeGenerator) {
        if(mVtableType == nullptr) {

            string vtableName = codeGenerator->createFunctionName(mClassType->getSimpleName()) + "_vtable";

            mVtableType = StructType::create(getGlobalContext(), vtableName.c_str());

            vector<Type*> vtable;
            unrollVtable(mClassType, vtable, codeGenerator);

            mVtableType->setBody(vtable);
        }

        return mVtableType;
    }

    void unrollFields(StapleClass* stapleClass, vector<Type*>& elements, LLVMCodeGenerator *codeGenerator) {

        if(stapleClass->getParent() != nullptr) {
            unrollFields(stapleClass->getParent(), elements, codeGenerator);
        }

        for(StapleField* fieldType : stapleClass->getFields()) {
            if(fieldType->getName().compare("class") != 0) {
                elements.push_back(codeGenerator->getLLVMType(fieldType));
            }
        }
    }

    llvm::StructType* LLVMStapleObject::getObjectType(LLVMCodeGenerator *codeGenerator) {
        if(mObjectStruct == nullptr) {
            mObjectStruct = StructType::create(getGlobalContext(), codeGenerator->createFunctionName(mClassType->getSimpleName()));

            vector<Type*> elements;
            elements.push_back(PointerType::getUnqual(getClassDefType(codeGenerator)));
            unrollFields(mClassType, elements, codeGenerator);

            mObjectStruct->setBody(elements);
        }

        return mObjectStruct;
    }
} // namespace staple