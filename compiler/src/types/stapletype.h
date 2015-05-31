
#ifndef _STAPLE_STAPLETYPE_H_
#define _STAPLE_STAPLETYPE_H_

#include <string>
#include <vector>

#include <llvm/Support/Casting.h>

typedef unsigned int uint;

namespace staple {

    using namespace std;

    class StapleMethodFunction;
    class StapleField;
    class StapleClass;

    enum StapleKind {
        SK_Class,
        SK_ClassDef,
        SK_Function,
        SK_Method,
        SK_Field,
        SK_Array,
        SK_Pointer,
        SK_Integer,
        SK_Float,
        SK_Void
    };

    class StapleType {
    private:
        const StapleKind mKind;

    public:
        StapleType(StapleKind k) : mKind(k) {}

        StapleKind getKind() const { return mKind; }

        virtual bool isAssignable(StapleType* type) = 0;

        static StapleType* getVoidType();
        static StapleType* getBoolType();
        static StapleType* getInt8Type();
        static StapleType* getInt16Type();
        static StapleType* getInt32Type();
        static StapleType* getInt64Type();
        static StapleType* getFloat32Type();
        static StapleType* getFloat64Type();
        static StapleType* getInt8PtrType();
    };

    class StapleFunction : public StapleType {
        friend class StapleClass;
    protected:
        StapleFunction(StapleKind kind, StapleType* returnType, vector<StapleType*> argsType, bool isVarg)
                : StapleType(kind), mReturnType(returnType), mArgumentTypes(argsType), mIsVarg(isVarg){}

        StapleType* mReturnType;
        vector<StapleType*> mArgumentTypes;
        bool mIsVarg;

    public:
        StapleFunction(StapleType* returnType, vector<StapleType*> argsType, bool isVarg)
                : StapleType(SK_Function), mReturnType(returnType), mArgumentTypes(argsType), mIsVarg(isVarg) {}

        StapleType* getReturnType() const { return mReturnType; }
        const vector<StapleType*> getArguments() const { return mArgumentTypes; }
        bool getIsVarg() const { return mIsVarg; }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Function || T->getKind() == SK_Method;
        }

        bool isAssignable(StapleType* type);
    };

    class StapleMethodFunction : public StapleFunction {
    public:
        enum Type {
            Static,
            Virtual
        };

    protected:
        StapleClass* mClass;
        string mName;
        Type mType;

    public:
        StapleMethodFunction(StapleClass* classType, const string& name,
                             StapleType* returnType, vector<StapleType*> argsType, bool isVarg,
                             Type type = Type::Virtual)
                : StapleFunction(SK_Method, returnType, argsType, isVarg), mClass(classType), mName(name), mType(type) {}

        StapleClass* getClass() const { return mClass; }
        const string& getName() const { return mName; }
        const Type getType() const { return mType; }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Method;
        }

        bool isAssignable(StapleType* type);
    };

    class StapleClassDef : public StapleType {
    private:
        StapleClass* mClass;

    public:
        StapleClassDef(StapleClass* classType) : StapleType(SK_ClassDef), mClass(classType) {}

        StapleClass* getClass() const { return mClass; }

        bool isAssignable(StapleType* type) {
            return llvm::isa<StapleClassDef>(type);
        }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_ClassDef;
        }
    };


    class StapleClass : public StapleType {
    private:

        const string mName;
        StapleClass* mParent;
        vector<StapleField*> mFields;
        vector<StapleMethodFunction*> mMethods;


    public:

        /**
         * name - FQ class name (i.e. org.staple.MyClass)
         */
        StapleClass(const string& name, StapleClass* parent = nullptr);


        /**
         * return - FQ classname
         */
        const string getClassName() const { return mName; }

        /**
         * return - class name (i.e. MyClass)
         */
        const string getSimpleName() const {
            size_t pos = mName.find_last_of('.');
            if(pos == string::npos) {
                return mName;
            } else {
                return mName.substr(pos+1);
            }
        }

        StapleClass* getParent() const { return mParent; }
        void setParent(StapleClass* parent);

        StapleMethodFunction* addMethod(const string& name, StapleType* returnType, vector<StapleType*> argsType,
                                        bool isVarg, StapleMethodFunction::Type type = StapleMethodFunction::Type::Virtual);
        const vector<StapleMethodFunction*> getMethods() const { return mMethods; }
        StapleMethodFunction* getMethod(const string& name, int& index) const;

        StapleField* addField(const string& name, StapleType* type);
        const vector<StapleField*> getFields() const { return mFields; }
        StapleField* getField(const string& name, uint& index) const;


        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Class;
        }

        bool isAssignable(StapleType* type);
    };

    class StapleField : public StapleType {
    protected:
        StapleClass* mClass;
        string mName;
        StapleType* mType;

    public:
        StapleField(StapleClass* classType, const string& name, StapleType* type)
        : StapleType(SK_Field), mClass(classType), mName(name), mType(type) {}

        const string& getName() const { return mName; }
        StapleType* getElementType() const { return mType; }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Field;
        }

        bool isAssignable(StapleType* type);
    };

    class StapleArray : public StapleType {
    private:
        StapleType* mElementType;
        uint64_t mSize;

    public:
        StapleArray(StapleType* elementType, uint64_t size)
        : StapleType(SK_Array), mElementType(elementType), mSize(size) {}

        const StapleType* getElementType() const {
            return mElementType;
        }

        const uint64_t getSize() const {
            return mSize;
        }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Array;
        }

        bool isAssignable(StapleType* type);
    };

    class StaplePointer : public StapleType {
    private:
        StapleType* mElementType;

    public:
        StaplePointer(StapleType* elementType)
        : StapleType(SK_Pointer), mElementType(elementType) {}

        StapleType* getElementType() const { return mElementType; };

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Pointer;
        }


        bool isAssignable(StapleType* type);
    };

    class StapleInt : public StapleType {
    private:
        const uint8_t mWidth;

    public:
        StapleInt(uint8_t width)
        : StapleType(SK_Integer), mWidth(width) {}

        uint8_t getWidth() const { return mWidth; }

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Integer;
        }

        bool isAssignable(StapleType* type);
    };

    class StapleFloat : public StapleType {
    public:
        enum Type {
            f16,
            f32,
            f64
        };

        StapleFloat(Type type)
        : StapleType(SK_Float), mType(type) {}

        static bool classof(const StapleType *T) {
            return T->getKind() == SK_Float;
        }

        bool isAssignable(StapleType* type);

    private:
        Type mType;
    };



}


#endif //_STAPLE_STAPLETYPE_H_
