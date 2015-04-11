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
        bool isAssignable(StapleType* type) {

        }
    };

    StapleVoidType VoidType;

    StapleType* StapleType::getVoidType() {
        return &VoidType;
    }

    StapleInt BoolType(1);

    StapleType* StapleType::getBoolType() {
        return &BoolType;
    }

    StapleInt Int8Type(8);

    StapleType* StapleType::getInt8Type() {
        return &Int8Type;
    }

    StapleInt Int16Type(16);

    StapleType* StapleType::getInt16Type() {
        return &Int16Type;
    }

    StapleInt Int32Type(32);

    StapleType* StapleType::getInt32Type() {
        return &Int32Type;
    }

    StapleInt Int64Type(64);

    StapleType* StapleType::getInt64Type() {
        return &Int64Type;
    }

    StapleFloat Float32Type(StapleFloat::Type::f32);

    StapleType* StapleType::getFloat32Type() {
        return &Float32Type;
    }

    StapleFloat Float64Type(StapleFloat::Type::f64);

    StapleType* StapleType::getFloat64Type() {
        return &Float64Type;
    }

    StaplePointer Int8PtrType(StapleType::getInt8Type());

    StapleType *StapleType::getInt8PtrType() {
        return &Int8PtrType;
    }

    StapleClass* BaseObject = nullptr;


    //////// Staple Class ///////

    StapleClass* StapleClass::getBaseObject() {
        if(BaseObject == nullptr) {
            BaseObject = new StapleClass("obj", nullptr);
            BaseObject->addField("refCount", StapleType::getInt32Type());
        }
        return BaseObject;
    }

    StapleClass::StapleClass(const string &name, StapleClass* parent)
    : StapleType(SK_Class), mName(name), mParent(parent) { }

    void StapleClass::setParent(StapleClass* parent) {
        mParent = parent == nullptr ? getBaseObject() : parent;
    }


    llvm::Type* StapleClass::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = StructType::create(getGlobalContext());
        }
        return mCachedType;
    }

    StapleMethodFunction* StapleClass::addMethod(const string& name, StapleType* returnType, vector<StapleType*> argsType, bool isVarg) {
        StapleMethodFunction* retval = new StapleMethodFunction(this, name, returnType, argsType, isVarg);
        mMethods.push_back(retval);
        return retval;
    }

    StapleMethodFunction* StapleClass::getMethod(const string &name, int &index) const {
        StapleMethodFunction* retval = nullptr;

        if(mParent != nullptr) {
            retval = mParent->getMethod(name, index);
        }

        if(retval == nullptr) {
            for(StapleMethodFunction* method : mMethods) {
                if(method->getName().compare(name) == 0) {
                    retval = method;
                }
                index++;
            }
        }

        return retval;
    }

    StapleField* StapleClass::addField(const string &name, StapleType *type) {
        StapleField* retval = new StapleField(this, name, type);
        mFields.push_back(retval);
        return retval;
    }

    StapleField* StapleClass::getField(const string &name, int &index) const {
        StapleField* retval = nullptr;

        if(mParent != nullptr) {
            retval = mParent->getField(name, index);
        }

        if(retval == nullptr) {
            for(StapleField* field : mFields) {
                if(field->getName().compare(name) == 0){
                    retval = field;
                    break;
                }
                index++;
            }
        }


        return retval;
    }


    llvm::Type* StapleField::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = mType->getLLVMType();
        }
        return mCachedType;
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

    bool StaplePointer::isAssignable(StapleType *type) {
        if(StaplePointer* ptr = dyn_cast<StaplePointer>(type)) {
            return mElementType->isAssignable(ptr->mElementType);
        } else {
            return false;
        }
    }

    llvm::Type* StapleInt::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = Type::getIntNTy(getGlobalContext(), mWidth);
        }
        return mCachedType;
    }

    llvm::Type* StapleFloat::getLLVMType() {
        if(mCachedType == nullptr) {
            switch(mType) {
                case Type::f16:
                    mCachedType = llvm::Type::getHalfTy(getGlobalContext());
                    break;

                case Type::f32:
                    mCachedType = llvm::Type::getFloatTy(getGlobalContext());
                    break;

                case Type::f64:
                    mCachedType = llvm::Type::getDoubleTy(getGlobalContext());
                    break;
            }
        }
        return mCachedType;
    }

}

