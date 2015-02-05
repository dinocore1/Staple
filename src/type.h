#ifndef STYPE_H_
#define STYPE_H_

#include <llvm/IR/DerivedTypes.h>
#include <utility>

class SFunctionType;
class SMethodType;

llvm::StructType* getStapleRuntimeClassStruct();

class SType {
private:

public:
    llvm::Type* type;

    static SType* get(llvm::Type* type);

    virtual bool isFunctionTy() { return false; }
    virtual bool isPointerTy() { return false; }
    virtual bool isClassTy() { return false; }
    virtual bool isArrayTy() { return type->isArrayTy(); }
    virtual bool isIntTy() { return type->isIntegerTy(); }
    virtual bool isAssignable(SType *dest);
};

class SClassType : public SType {
    llvm::StructType* runtimeStructType;

public:
    std::string name;
    SClassType* parent;
    std::vector<std::pair<std::string, SType*>> fields;
    std::vector<std::pair<std::string, SMethodType*>> methods;

    SClassType(const std::string& name);

    SClassType(SClassType* parent,
            std::vector<std::pair<std::string, SType*>> fields,
            std::vector<std::pair<std::string, SMethodType*>> methods
    );

    virtual bool isClassTy() { return true; };
    virtual bool isAssignable(SType *dest);

    int getFieldIndex(const std::string& name);
    int getMethodIndex(const std::string& name);

    void createLLVMClass();
    llvm::StructType* getRuntimeStructType();

    SFunctionType* getMethod(std::string name);
};

class SIntType : public SType {
public:
    unsigned int width;

    virtual bool isAssignable(SType *dest);
};

class SFloatType : public SType {
public:
    virtual bool isAssignable(SType *dest);
};

class SFunctionType : public SType {
public:
    SType *returnType;
    std::vector<SType *> arguments;
    bool isValArgs;

    SFunctionType(SType* retrunType, std::vector<SType *> args, bool isValArgs);

    virtual bool isFunctionTy() { return true; }

    virtual bool isAssignable(SType *dest);
};

class SMethodType : public SFunctionType {
public:
    SClassType* classType;

    SMethodType(SClassType *classType, SType *returnType, std::vector<SType *> args, bool const isVarg);

};


class SArrayType : public SType {
public:
    SType *elementType;
    uint64_t size;

    virtual bool isAssignable(SType *dest);

    static SArrayType* get(SType* elementType, unsigned int size);
};

class SPointerType : public SType {
    SPointerType(SType* elementType) : elementType(elementType) {}
public:
    SType *elementType;

    virtual bool isPointerTy() { return true; }
    virtual bool isAssignable(SType *dest);

    static SPointerType* get(SType* base);
};

#endif /* STYPE_H_ */