#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/Verifier.h>
#include <llvm/IR/DerivedTypes.h>
#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/Module.h>
#include <llvm/IR/Function.h>

#include "stapletype.h"

namespace staple {

    using namespace std;
    using namespace llvm;

    class StapleVoidType : public StapleType {
    public:
        StapleVoidType() : StapleType(SK_Void) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Void;
        }

        llvm::Type* getLLVMType() {
            return llvm::Type::getVoidTy(getGlobalContext());
        }
    };

    const StapleVoidType VoidType;

    StapleType* StapleType::getVoidType() {
        return &VoidType;
    }

    const StapleInt BoolType(1);

    StapleType* StapleType::getBoolType() {
        return &BoolType;
    }

    //////// Staple Class ///////

    llvm::Type* StapleClass::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = StructType::create(getGlobalContext());
        }
        return mCachedType;
    }

    StapleMethodFunction* StapleClass::addMethod(const string& name, StapleType* returnType, vector<StapleType*> argsType, bool isVarg) {
        StapleMethodFunction* retval = new StapleMethodFunction(this, returnType, argsType, isVarg);
        mMethods.push_back(make_pair(name, retval));
        return retval;
    }


    llvm::Type* StapleArray::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = ArrayType::get(mElementType->getLLVMType(), mSize);
        }
        return mCachedType;
    }

    llvm::Type *StaplePointer::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = PointerType::getUnqual(mElementType->getLLVMType());
        }
        return mCachedType;
    }

    llvm::Type* StapleInt::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = Type::getIntNTy(getGlobalContext(), mWidth);
        }
        return mCachedType;
    }



}

