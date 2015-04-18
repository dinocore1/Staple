
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

    StapleMethodFunction* StapleClass::addMethod(const string& name, StapleType* returnType,
                                                 vector<StapleType*> argsType, bool isVarg,
                                                StapleMethodFunction::Type type) {
        StapleMethodFunction* retval = new StapleMethodFunction(this, name, returnType, argsType, isVarg, type);
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

    StapleField* StapleClass::getField(const string &name, uint &index) const {
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

    bool StapleClass::isAssignable(StapleType *type) {
        bool retval = false;
        if(StapleClass* stpClass = dyn_cast<StapleClass>(type)) {
            retval = mName.compare(stpClass->mName) == 0;
        }

        return retval;
    }

    //// Staple Function ////

    bool StapleFunction::isAssignable(StapleType *type) {
        bool retval = false;
        if(StapleFunction* function = dyn_cast<StapleFunction>(type)) {
            retval = mReturnType->isAssignable(function->mReturnType);
            retval &= mArgumentTypes.size() == function->mArgumentTypes.size();

            if(retval) {
                for(int i=0;i<mArgumentTypes.size();i++){
                    retval &= mArgumentTypes[i]->isAssignable(function->mArgumentTypes[i]);
                }
            }
        }

        return retval;
    }

    ///// Staple Array ////

    bool StapleArray::isAssignable(StapleType *type) {
        bool retval = false;
        if(StapleArray* array = dyn_cast<StapleArray>(type)) {
            retval = mElementType->isAssignable(array->mElementType);
            retval &= mSize == array->mSize;
        }

        return retval;
    }

    //// Staple Pointer ////

    bool StaplePointer::isAssignable(StapleType *type) {
        if(StaplePointer* ptr = dyn_cast<StaplePointer>(type)) {
            return mElementType->isAssignable(ptr->mElementType);
        } else {
            return false;
        }
    }


    //// Staple Method ////

    bool StapleMethodFunction::isAssignable(StapleType *type) {
        bool retval = false;
        if(StapleMethodFunction* function = dyn_cast<StapleMethodFunction>(type)) {

            retval = mClass->isAssignable(function->mClass);
            retval &= mReturnType->isAssignable(function->mReturnType);
            retval &= mArgumentTypes.size() == function->mArgumentTypes.size();

            if(retval) {
                for(int i=0;i<mArgumentTypes.size();i++){
                    retval &= mArgumentTypes[i]->isAssignable(function->mArgumentTypes[i]);
                }
            }
        }

        return retval;
    }


    //// Staple Field ////

    bool StapleField::isAssignable(StapleType *type) {
        bool retval = mType->isAssignable(type);
        return retval;
    }

    //// Staple Int ////

    bool StapleInt::isAssignable(StapleType *type) {
        bool retval = isa<StapleInt>(type) || isa<StapleFloat>(type);
        return retval;

    }

    bool StapleFloat::isAssignable(StapleType *type) {
        bool retval = isa<StapleInt>(type) || isa<StapleFloat>(type);
        return retval;
    }


}

