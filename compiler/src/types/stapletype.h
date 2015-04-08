
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
        StapleClass* mParent;
        string mName;

    public:
        StapleClass(const string& name)
        : StapleType(SK_Class), mName(name) {}

        const string getClassName() const { return mName; }

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
