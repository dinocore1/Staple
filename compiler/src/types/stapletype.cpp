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

    llvm::Type *StapleClass::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = StructType::create(getGlobalContext());
        }
        return mCachedType;
    }

    llvm::Type *StapleArray::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = ArrayType::get(mElementType->getLLVMType(), mSize);
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

