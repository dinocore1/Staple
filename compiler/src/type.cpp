#include <llvm/IR/LLVMContext.h>
#include "type.h"


SType* SType::get(llvm::Type *type) {
    if(type->isPointerTy()){
        SPointerType* retval = SPointerType::get(SType::get(type->getPointerElementType()));
        return retval;
    } else if(type->isArrayTy()) {
        SArrayType* retval = new SArrayType();
        retval->type = type;
        retval->size = type->getArrayNumElements();
        return retval;
    } else {
        SType *retval = new SType();
        retval->type = type;
        return retval;
    }

}

static bool isNumberType(SType* type) {
    return type->type->isIntegerTy() || type->type->isFloatingPointTy();
}

bool SType::isAssignable(SType *dest) {
    bool retval = false;

    if(isNumberType(this) && isNumberType(dest)){
        retval = true;
    }
    return retval;
}

SClassType::SClassType(const std::string &name)
: name(name), parent(NULL) {
    type = llvm::StructType::create(llvm::getGlobalContext());
    fields.push_back( std::make_pair(std::string("refcount"), SType::get(llvm::Type::getInt32Ty(llvm::getGlobalContext()))));
}

SClassType::SClassType(SClassType* parent, std::vector<std::pair<std::string, SType*>> fields,
        std::vector<std::pair<std::string, SMethodType*>> methods)
: parent(parent)
, fields(fields)
, methods(methods) {

    type = llvm::StructType::create(llvm::getGlobalContext());
}

static bool getClassFieldIndex(SClassType* classType, const std::string& name, int& index)
{
    bool found = classType->parent != NULL ? getClassFieldIndex(classType->parent, name, index) : false;
    if(!found) {
        for(auto it=classType->fields.begin();it!=classType->fields.end();it++) {
            if(name.compare((*it).first) == 0){
                return true;
            }
            index++;
        }
    }
    return found;
}

int SClassType::getFieldIndex(const std::string& name)
{
    int index = 0;
    if(getClassFieldIndex(this, name, index)) {
        return index;
    } else {
        return -1;
    }
}

static bool getClassMethodIndex(SClassType* classType, const std::string& name, int& index)
{
    bool found = classType->parent != NULL ? getClassMethodIndex(classType->parent, name, index) : false;
    if(!found) {
        for(auto it=classType->methods.begin();it!=classType->methods.end();it++) {
            if(name.compare((*it).first) == 0){
                return true;
            }
            index++;
        }
    }
    return found;
}

int SClassType::getMethodIndex(const std::string& name)
{
    int index = 0;
    if(getClassMethodIndex(this, name, index)) {
        return index;
    } else {
        return -1;
    }
}

SFunctionType* SClassType::getMethod(std::string name) {

    for(auto it=methods.begin();it!=methods.end();it++) {
        if(name.compare((*it).first) == 0){
            return (*it).second;
        }
    }
    return NULL;
}

bool SClassType::isAssignable(SType *dest) {
    bool retval = false;
    if(dest->isClassTy()) {
        SClassType* destClass = (SClassType*)dest;
        retval = name.compare(destClass->name) == 0;
    }
    return retval;
}

SFunctionType::SFunctionType(SType* retrunType, std::vector<SType *> args, bool isValArgs)
: returnType(retrunType)
, arguments(args)
, isValArgs(isValArgs) {
    std::vector<llvm::Type*> llvmArgs(args.size());
    for(std::vector<SType*>::iterator it = args.begin();it != args.end();it++){
        llvmArgs.push_back((*it)->type);
    }

    type = llvm::FunctionType::get(returnType->type, llvmArgs, isValArgs);
}

bool SFunctionType::isAssignable(SType *dest) {
}

SMethodType::SMethodType(SClassType* classType, SType* retrunType, std::vector<SType *> args, bool const isValArgs)
        : SFunctionType(retrunType, args, isValArgs)
        , classType(classType){

    std::vector<llvm::Type*> llvmArgs;

    //first argument is 'this'
    llvmArgs.push_back(llvm::PointerType::getUnqual(classType->type));

    for(std::vector<SType*>::iterator it = arguments.begin();it != arguments.end();it++){
        llvmArgs.push_back((*it)->type);
    }

    type = llvm::FunctionType::get(returnType->type, llvmArgs, isValArgs);
}

bool SArrayType::isAssignable(SType *dest) {
}

SArrayType* SArrayType::get(SType* elementType, unsigned int size) {
    SArrayType* retval = new SArrayType();
    retval->elementType = elementType;
    retval->size = size;
    retval->type = llvm::ArrayType::get(retval->elementType->type, size);
    return retval;
}

SPointerType* SPointerType::get(SType *base) {
    SPointerType* retval = new SPointerType(base);
    retval->type = llvm::PointerType::getUnqual(base->type);
    return retval;
}

bool SPointerType::isAssignable(SType *dest) {
    if(dest->isPointerTy()) {
        SPointerType* destPtr = (SPointerType*)dest;
        return elementType->isAssignable(destPtr->elementType);
    } else {
        return false;
    }
}
