
#ifndef _STAPLE_STAPLETYPE_H_
#define _STAPLE_STAPLETYPE_H_

#include <llvm/Support/Casting.h>
#include <llvm/IR/Type.h>

namespace staple {

    using namespace std;

    class StapleMethodFunction;

    enum StapleKind {
        SK_Class,
        SK_Function,
        SK_Method,
        SK_Array,
        SK_Pointer,
        SK_Integer,
        SK_Float,
        SK_Void
    };

    class StapleType {
    private:
        const StapleKind mKind;

    protected:
        llvm::Type* mCachedType;

    public:
        StapleType(StapleKind k) : mKind(k), mCachedType(nullptr) {}

        StapleKind getKind() const { return mKind; }

        virtual llvm::Type* getLLVMType() = 0;

        static StapleType* getVoidType();
        static StapleType* getBoolType();
    };



    class StapleClass : public StapleType {
    private:
        string mName;
        StapleClass* mParent;
        vector<pair<string, StapleType*>> mFields;
        vector<pair<string, StapleMethodFunction*>> mMethods;


    public:
        StapleClass(const string& name)
        : StapleType(SK_Class), mName(name), mParent(nullptr) {}

        const string getClassName() const { return mName; }
        const string getSimpleName() const {
            size_t pos = mName.find_last_of('.');
            if(pos == string::npos) {
                return mName;
            } else {
                return mName.substr(pos+1);
            }
        }
        const StapleClass* getParent() const { return mParent; }

        StapleMethodFunction* addMethod(const string& name, StapleType* returnType, vector<StapleType*> argsType, bool isVarg);

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Class;
        }

        llvm::Type* getLLVMType();
    };

    class StapleFunction : public StapleType {
    friend class StapleClass;
    protected:
        StapleType* mReturnType;
        vector<StapleType*> mArgumentTypes;
        bool mIsVarg;

    public:
        StapleFunction(StapleType* returnType, vector<StapleType*> argsType, bool isVarg)
        : StapleType(SK_Function),
        mReturnType(returnType),
        mIsVarg(isVarg) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Function;
        }

        llvm::Type* getLLVMType();
    };

    class StapleMethodFunction : public StapleFunction {
    protected:
        StapleClass* mClass;

    public:
        StapleMethodFunction(StapleClass* classType, StapleType* returnType, vector<StapleType*> argsType, bool isVarg)
        : StapleFunction(returnType, argsType, isVarg),
        StapleType(SK_Method),
        mClass(classType) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Function || T->getKind() == SK_Method;
        }

        llvm::Type* getLLVMType();
    };

    class StapleArray : public StapleType {
    private:
        StapleType* mElementType;
        uint64_t mSize;

    public:
        StapleArray()
        : StapleType(SK_Array){}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Array;
        }

        llvm::Type* getLLVMType();
    };

    class StaplePointer : public StapleType {
    private:
        StapleType* mElementType;

    public:
        StaplePointer(StapleType* elementType)
        : StapleType(SK_Pointer), mElementType(elementType) {}

        const StapleType* getElementType() const { return mElementType; };

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Pointer;
        }

        llvm::Type* getLLVMType();
    };

    class StapleInt : public StapleType {
    private:
        uint8_t mWidth;

    public:
        StapleInt(uint8_t width)
        : StapleType(SK_Integer), mWidth(width) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Integer;
        }

        llvm::Type* getLLVMType();
    };

    const StapleInt BOOL_TYPE(1);
    const StapleInt BYTE_TYPE(8);
    const StapleInt INT_TYPE(32);


}


#endif //_STAPLE_STAPLETYPE_H_
