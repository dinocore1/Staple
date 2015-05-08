

#include "LLVMStapleObject.h"
#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"

namespace staple {

    using namespace llvm;

    StructType* STPOBJ_INSTANCE_TYPE = StructType::create(getGlobalContext(), "stp_obj");
    StructType* STPOBJ_CLASSDEF_TYPE = StructType::create(getGlobalContext(), "stp_class");
    StructType* STPOBJ_VTABLE_TYPE = StructType::create(getGlobalContext(), "stp_obj_vtable");

    GlobalVariable* STPOBJ_CLASS_VALUE = nullptr;


    StructType* LLVMStapleObject::getStpObjVtableType() {
        if(STPOBJ_VTABLE_TYPE->isEmptyTy()) {
            STPOBJ_VTABLE_TYPE->setBody(
              PointerType::getUnqual(LLVMStapleObject::getKillFunctionType()),
              NULL
            );
        }
        return STPOBJ_VTABLE_TYPE;
    }


    StructType* LLVMStapleObject::getStpClassDefType() {
        if(STPOBJ_CLASSDEF_TYPE->isEmptyTy()) {
            STPOBJ_CLASSDEF_TYPE->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    PointerType::getUnqual(STPOBJ_CLASSDEF_TYPE), // parent class
                    LLVMStapleObject::getStpObjVtableType(), // vtable
                    NULL
            );
        }
        return STPOBJ_CLASSDEF_TYPE;
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
                    PointerType::getUnqual(LLVMStapleObject::getStpClassDefType()), // runtime class ptr
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
            return getStpClassDefType();
        }


        llvm::StructType* getObjectType(LLVMCodeGenerator* codeGenerator) {
            return getStpObjInstanceType();
        }


        llvm::StructType* getVtableType(LLVMCodeGenerator* codeGenerator) {
            return getStpObjVtableType();
        }

        llvm::GlobalVariable* getClassDefinition(LLVMCodeGenerator* codeGenerator) {
            if(STPOBJ_CLASS_VALUE == nullptr) {
                STPOBJ_CLASS_VALUE = new GlobalVariable(codeGenerator->mModule, getStpClassDefType(), true, GlobalValue::LinkageTypes::ExternalLinkage, nullptr, "stp_class_def");
            }
            return STPOBJ_CLASS_VALUE;
        }

        /*
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
        uint fieldIndex = 1; // start at index 1 to account for the ref counter
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

            Value* value = irBuilder.CreateConstGEP2_32(thisPtr, 0, 0);
            irBuilder.CreateStore(getClassDefinition(codeGenerator), value);


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

    GlobalVariable* LLVMStapleObject::getClassNameValue(LLVMCodeGenerator *codeGenerator) {
        if(mClassNameValue == nullptr) {
            string className = codeGenerator->createFunctionName(mClassType->getSimpleName());
            Constant* classNameValue = ConstantDataArray::getString(getGlobalContext(), className.c_str());
            mClassNameValue = new GlobalVariable(codeGenerator->mModule, classNameValue->getType(), true, GlobalValue::LinkageTypes::PrivateLinkage, classNameValue);
        }
        return mClassNameValue;
    }

    GlobalVariable* LLVMStapleObject::getClassDefinition(LLVMCodeGenerator *codeGenerator) {
        if(mClassDefValue == nullptr) {

            Value* parentPtr;
            if(mClassType->getParent() == nullptr) {
                parentPtr = ConstantPointerNull::get(PointerType::getUnqual(getStpClassDefType()));
            } else {
                LLVMStapleObject* parent = LLVMStapleObject::get(mClassType->getParent());
                parentPtr = parent->getClassDefinition(codeGenerator);
            }

            Constant* classDef = ConstantStruct::get(getClassDefType(codeGenerator),
                                  ConstantExpr::getPointerCast(getClassNameValue(codeGenerator), Type::getInt8PtrTy(getGlobalContext())),
                                  parentPtr,
                                  getClassVTableValue(codeGenerator),
                                  NULL);

            mClassDefValue = new GlobalVariable(codeGenerator->mModule, classDef->getType(), true, GlobalValue::LinkageTypes::PrivateLinkage, classDef);

        }
        return mClassDefValue;
    }

    void unrollVtableValues(StapleClass* stapleClass, vector<Constant*>& elements, LLVMCodeGenerator *codeGenerator) {

        if(stapleClass->getParent() != nullptr) {
            unrollVtableValues(stapleClass->getParent(), elements, codeGenerator);
        }

        for(StapleMethodFunction* methodFunction : stapleClass->getMethods()) {
            if(methodFunction->getType() == StapleMethodFunction::Type::Virtual) {
                string methodName = codeGenerator->createFunctionName(stapleClass->getSimpleName()) + "_" + methodFunction->getName();
                FunctionType* functionType = cast<FunctionType>(codeGenerator->getLLVMType(methodFunction));

                elements.push_back(codeGenerator->getModule()->getOrInsertFunction(methodName.c_str(), functionType));
            }
        }
    }

    Constant* LLVMStapleObject::getClassVTableValue(LLVMCodeGenerator *codeGenerator) {
        if(mClassVTableValue == nullptr) {

            vector<Constant*> methods;
            unrollVtableValues(mClassType, methods, codeGenerator);
            Constant* classVTable = ConstantStruct::get(getVtableType(codeGenerator),
                                                        methods);

            mClassVTableValue = classVTable;
        }
        return mClassVTableValue;
    }

    StructType* LLVMStapleObject::getClassDefType(LLVMCodeGenerator* codeGenerator) {
        if(mClassDefType == nullptr) {

            string classDefName = codeGenerator->createFunctionName(mClassType->getSimpleName()) + "_class";
            mClassDefType = StructType::create(getGlobalContext(), classDefName.c_str());

            mClassDefType->setBody(
                    Type::getInt8PtrTy(getGlobalContext()), // FQ class name
                    mClassType->getParent() != nullptr
                      ? PointerType::getUnqual(LLVMStapleObject::get(mClassType->getParent())->getClassDefType(codeGenerator))
                      : PointerType::getUnqual(STPOBJ_CLASSDEF_TYPE), // parent class ptr
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
            elements.push_back(PointerType::getUnqual(getClassDefType(codeGenerator))); // class def pointer
            elements.push_back(Type::getInt32Ty(getGlobalContext())); // refCounter

            unrollFields(mClassType, elements, codeGenerator);

            mObjectStruct->setBody(elements);
        }

        return mObjectStruct;
    }
} // namespace staple