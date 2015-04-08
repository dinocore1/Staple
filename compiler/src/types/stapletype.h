
#ifndef _STAPLE_STAPLETYPE_H_
#define _STAPLE_STAPLETYPE_H_

#include <llvm/Support/Casting.h>
#include <llvm/IR/Type.h>

namespace staple {

    using namespace std;

    enum StapleKind {
        SK_Class,
        SK_Function,
        SK_Array,
        SK_Pointer,
        SK_Integer,
        SK_Float
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

    };

    class StapleClass : public StapleType {
    private:
        string mName;
        StapleClass* mParent;


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

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Class;
        }

        llvm::Type* getLLVMType();
    };

    class StapleFunction : public StapleType {
    private:
        StapleType* mReturnType;
        vector<StapleType*> mArgumentTypes;
        bool mIsValArgs;

    public:
        StapleFunction()
        : StapleType(SK_Function) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Function;
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
