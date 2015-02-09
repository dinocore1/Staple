
#ifndef OBJECTHELPER_H_
#define OBJECTHELPER_H_

#include "../codegen.h"

void loadFields(SClassType* classObj, std::vector<llvm::Type*>& elements);

class ObjectHelper {
private:
    static StructType* sStapleRuntimeClassStruct;

    bool mInitObjectStruct;
    Function* mInitFunction;
    GlobalVariable* mClassNameVar;
    GlobalVariable* mClassDefVar;

public:
    SClassType* classType;
    std::vector< std::pair<NMethodFunction*, Function*> > mMethods;

    ObjectHelper(SClassType* classType)
            : mInitObjectStruct(false)
            , mInitFunction(NULL)
            , mClassNameVar(NULL)
            , mClassDefVar(NULL)
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
            ArrayRef<Type*> args = {PointerType::getUnqual(getObjectType())};
            FunctionType* functionType = FunctionType::get(Type::getVoidTy(getGlobalContext()), args, false);

            Twine functionName(StringRef(classType->name), "_init");
            mInitFunction = Function::Create(functionType, Function::LinkageTypes::PrivateLinkage, functionName, context.module);

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

            context.Builder.CreateRetVoid();

            context.fpm->run(*mInitFunction);
        }

        return mInitFunction;
    }

    GlobalVariable* getClassDef(CodeGenContext& context) {

        if(mClassDefVar == NULL) {
            std::vector<llvm::Type*> types;
            types.push_back(getStapleRuntimeClassStruct());

            //vtable
            for(auto method : classType->methods) {
                types.push_back(llvm::PointerType::getUnqual(method.second->type));
            }

            StructType* runtimeStructType = StructType::create(types);


            Constant *classPre = ConstantStruct::get(getStapleRuntimeClassStruct(),
                    ConstantExpr::getBitCast(getClassNameValue(context), Type::getInt8PtrTy(getGlobalContext())),
                    ConstantPointerNull::get((PointerType *) getStapleRuntimeClassStruct()->getStructElementType(1)),
                    NULL);

            std::vector<Constant *> constants;
            constants.push_back(classPre);

            for (auto funPair : mMethods) {
                constants.push_back(funPair.second);
            }

            Constant *constantStruct = ConstantStruct::get(runtimeStructType, constants);

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
            elements.push_back(PointerType::getUnqual(   getStapleRuntimeClassStruct()   ));
            elements.push_back(Type::getInt32Ty(getGlobalContext()));

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

    static StructType* getStapleRuntimeClassStruct() {
        if(sStapleRuntimeClassStruct == NULL) {
            sStapleRuntimeClassStruct = StructType::create(llvm::getGlobalContext(), "stp_class");

            std::vector<llvm::Type*> elements;
            elements.push_back(llvm::Type::getInt8PtrTy(llvm::getGlobalContext()));
            elements.push_back(llvm::PointerType::getUnqual(sStapleRuntimeClassStruct));

            sStapleRuntimeClassStruct->setBody(elements);
        }

        return sStapleRuntimeClassStruct;
    }


};


#endif /* OBJECTHELPER_H_ */