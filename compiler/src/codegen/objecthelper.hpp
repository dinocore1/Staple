
#ifndef OBJECTHELPER_H_
#define OBJECTHELPER_H_


void loadFields(SClassType* classObj, std::vector<llvm::Type*>& elements);

class ObjectHelper {
private:
    static StructType* sStapleRuntimeClassStruct;
    static StructType* sGenericObjectType;

    bool mInitObjectStruct;
    Function* mInitFunction;
    Function* mDestroyFunction;
    GlobalVariable* mClassNameVar;
    GlobalVariable* mClassDefVar;
    StructType* mVirtualTableType;

public:
    SClassType* classType;
    std::vector< std::pair<NMethodFunction*, Function*> > mMethods;

    ObjectHelper(SClassType* classType)
            : mInitObjectStruct(false)
            , mInitFunction(NULL)
            , mDestroyFunction(NULL)
            , mClassNameVar(NULL)
            , mClassDefVar(NULL)
            , mVirtualTableType(NULL)
            , classType(classType) {}

    GlobalVariable* getClassNameValue(CodeGenContext& context) {
        if(mClassNameVar == NULL) {
            Constant *className = ConstantDataArray::getString(getGlobalContext(), classType->name.c_str());
            mClassNameVar = new GlobalVariable(*context.module, className->getType(), true, GlobalValue::LinkageTypes::PrivateLinkage, className);
        }
        return mClassNameVar;
    }

    Function* getInitFunction(CodeGenContext& context) {
        if(mInitFunction == NULL) {
            FunctionType *functionType = FunctionType::get(
                    Type::getVoidTy(getGlobalContext()),
                    std::vector<Type *>{PointerType::getUnqual(getObjectType())},
                    false);

            Twine functionName(StringRef(classType->name), "_init");
            mInitFunction = Function::Create(functionType, Function::LinkageTypes::ExternalLinkage, functionName, context.module);
        }
        return mInitFunction;
    }

    void emitInitFunction(CodeGenContext& context) {
        getInitFunction(context);
        BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", mInitFunction);
        context.Builder.SetInsertPoint(bblock);

        Value* thisValue = mInitFunction->arg_begin();

        //set class def pointer
        Value* classDefValue = context.Builder.CreateGEP(thisValue, std::vector<Value*>{
                context.Builder.getInt32(0),
                context.Builder.getInt32(0)
        });
        classDefValue = context.Builder.CreateBitCast(classDefValue, PointerType::getUnqual(getClassDef(context)->getType()));
        context.Builder.CreateStore(getClassDef(context), classDefValue);

        //set refcount = 1
        Value* refCount = context.Builder.CreateGEP(thisValue, std::vector<Value*>{
                context.Builder.getInt32(0),
                context.Builder.getInt32(1)});
        context.Builder.CreateStore(context.Builder.getInt32(1), refCount);

        //set member initial values
        int count = 0;
        for(auto field : classType->fields) {
            if(field.second->isPointerTy() && ((SPointerType*)field.second)->elementType->isClassTy()) {
                SPointerType* pointerType = (SPointerType*)field.second;
                SClassType* fieldClass = (SClassType*)pointerType->elementType;

                Value* nullValue = ConstantPointerNull::get(PointerType::getUnqual(fieldClass->type));

                Value* objPtr = getFieldPtrValue(context, thisValue, count);
                context.Builder.CreateStore(nullValue, objPtr);
            } else if(field.second->isIntTy()) {
                IntegerType* intType = (IntegerType*)field.second->type;

                Value* objPtr = getFieldPtrValue(context, thisValue, count);
                Value* zeroValue = ConstantInt::get(intType, 0, false);
                context.Builder.CreateStore(zeroValue, objPtr);
            }
            count++;
        }

        context.Builder.CreateRetVoid();

        context.fpm->run(*mInitFunction);
    }

    Function* getDestroyFunction(CodeGenContext& context) {
        if(mDestroyFunction == NULL) {
            FunctionType* functionType = FunctionType::get(
                    Type::getVoidTy(getGlobalContext()),
                    std::vector<Type*>{PointerType::getUnqual(getObjectType())},
                    false);

            Twine functionName(StringRef(classType->name), "_dest");
            mDestroyFunction = Function::Create(functionType, Function::LinkageTypes::ExternalLinkage, functionName, context.module);
        }
        return mDestroyFunction;
    }

    void emitDestroyFunction(CodeGenContext& context) {
        getDestroyFunction(context);
        BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", mDestroyFunction);
        context.Builder.SetInsertPoint(bblock);

        Value* thisValue = mDestroyFunction->arg_begin();

        int count = 0;
        for(auto field : classType->fields) {
            if(field.second->isPointerTy() && ((SPointerType*)field.second)->elementType->isClassTy()) {
                SPointerType* pointerType = (SPointerType*)field.second;
                SClassType* fieldClass = (SClassType*)pointerType->elementType;

                Value* objPtr = getFieldPtrValue(context, thisValue, count);
                objPtr = context.Builder.CreateLoad(objPtr);


                Function* release = context.getRelease();
                objPtr = context.Builder.CreatePointerCast(objPtr, PointerType::getUnqual(ObjectHelper::getGenericObjType()));
                context.Builder.CreateCall(release, std::vector<Value*>{objPtr});
            }
            count++;
        }

        Function* freeFun = context.getFree();
        Value* value = context.Builder.CreatePointerCast(thisValue, Type::getInt8PtrTy(getGlobalContext()));
        context.Builder.CreateCall(freeFun, std::vector<Value*>({value}));

        context.Builder.CreateRetVoid();

        context.fpm->run(*mDestroyFunction);
    }

    static Value* getFieldPtrValue(CodeGenContext& context, Value* thisPtr, uint32_t fieldIndex) {
        Value* retval = context.Builder.CreateGEP(thisPtr, std::vector<Value*>{
                context.Builder.getInt32(0),
                context.Builder.getInt32(fieldIndex+1)
        });
        return retval;
    }


    StructType* getVTableType() {
        if(mVirtualTableType == NULL) {
            std::vector<Type *> elements;

            //destructor is always first virtual function
            FunctionType* destructorType = FunctionType::get(
                    Type::getVoidTy(getGlobalContext()),
                    std::vector<Type*>{PointerType::getUnqual(getObjectType())},
                    false);

            elements.push_back(PointerType::getUnqual(destructorType));

            //vtable
            for (auto method : classType->methods) {
                elements.push_back(PointerType::getUnqual(method.second->type));
            }

            {
                char name[512];
                snprintf(name, 512, "%s_vtable_type", classType->name.c_str());
                mVirtualTableType = StructType::create(elements, name);
            }

        }
        return mVirtualTableType;
    }

    Value* getVirtualFunction(CodeGenContext& context, Value* thisPtr, uint32_t functionIndex) {
        Value* retval = context.Builder.CreateGEP(thisPtr, std::vector<Value*>{
                context.Builder.getInt32(0),
                context.Builder.getInt32(0)
        });
        retval = context.Builder.CreateBitCast(retval, PointerType::getUnqual(getClassDef(context)->getType()));
        retval = context.Builder.CreateLoad(retval);


        retval = context.Builder.CreateGEP(retval, std::vector<Value*>{
                context.Builder.getInt32(0),
                context.Builder.getInt32(1),
                context.Builder.getInt32(functionIndex)
        });

        retval = context.Builder.CreateLoad(retval);

        return retval;

    }


    GlobalVariable* getClassDef(CodeGenContext& context) {

        if(mClassDefVar == NULL) {
            std::vector<llvm::Type*> types;
            types.push_back(getStapleRuntimeClassStruct());
            types.push_back(getVTableType());


            StructType* runtimeStructType = StructType::create(types);

            Constant *classPre = ConstantStruct::get(getStapleRuntimeClassStruct(),
                    ConstantExpr::getBitCast(getClassNameValue(context), Type::getInt8PtrTy(getGlobalContext())),
                    ConstantPointerNull::get((PointerType *) getStapleRuntimeClassStruct()->getStructElementType(1)),
                    NULL);


            std::vector<Constant *> constants;
            constants.push_back(getDestroyFunction(context));
            for (auto funPair : mMethods) {
                constants.push_back(funPair.second);
            }

            Constant* vtable = ConstantStruct::get(getVTableType(), constants);


            Constant *constantStruct = ConstantStruct::get(runtimeStructType,
                    classPre,
                    vtable,
                    NULL
                    );

            mClassDefVar = new GlobalVariable(
                    *context.module,
                    constantStruct->getType(),
                    true,
                    GlobalVariable::LinkageTypes::PrivateLinkage,
                    constantStruct
            );
        }

        return mClassDefVar;
    }

    StructType* getObjectType() {
        if(!mInitObjectStruct) {

            std::vector<Type*> elements;
            elements.push_back(PointerType::getUnqual(getStapleRuntimeClassStruct()));

            loadFields(classType, elements);

            ((StructType*)classType->type)->setBody(elements);

            {
                char name[512];
                snprintf(name, 512, "%s_obj", classType->name.c_str());
                ((StructType*)classType->type)->setName(name);
            }

            mInitObjectStruct = true;
        }
        return (StructType*)classType->type;
    }

    static StructType* getGenericObjType() {
        if(sGenericObjectType == NULL) {
            sGenericObjectType = StructType::create(getGlobalContext(), "stp_obj");

            sGenericObjectType->setBody(std::vector<Type*>{
                    PointerType::getUnqual(getStapleRuntimeClassStruct()),
                    Type::getInt32Ty(getGlobalContext())});
        }
        return sGenericObjectType;
    }

    static StructType* getStapleRuntimeClassStruct() {
        if(sStapleRuntimeClassStruct == NULL) {
            sStapleRuntimeClassStruct = StructType::create(llvm::getGlobalContext(), "stp_class");

            std::vector<llvm::Type*> elements;
            elements.push_back(Type::getInt8PtrTy(getGlobalContext()));
            elements.push_back(PointerType::getUnqual(sStapleRuntimeClassStruct));

            sStapleRuntimeClassStruct->setBody(elements);
        }

        return sStapleRuntimeClassStruct;
    }


};


#endif /* OBJECTHELPER_H_ */