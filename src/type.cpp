#include <llvm/IR/LLVMContext.h>
#include "type.h"

SType* SType::get(llvm::Type *type) {
    SType* retval = new SType();
    retval->type = type;
    return retval;
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

static void loadFields(SClassType* classObj, std::vector<llvm::Type*>& elements)
{
    if(classObj->parent != NULL){
        loadFields(classObj->parent, elements);
    }
    for(auto it = classObj->fields.begin();it!=classObj->fields.end();it++){
        elements.push_back((*it)->type);
    }
}

SClassType::SClassType(SClassType* parent, std::vector<SType*> fields,
        std::vector<llvm::FunctionType*> methods)
: parent(parent)
, fields(fields)
, methods(methods) {

    std::vector<llvm::Type*> elements;
    loadFields(this, elements);

    type = llvm::StructType::get(llvm::getGlobalContext(), elements);
}

bool SClassType::isAssignable(SType *dest) {

}

SFunctionType::SFunctionType(SType* retrunType, std::vector<SType *> args, bool isValArgs)
: returnType(retrunType)
, arguments(args) {
    std::vector<llvm::Type*> llvmArgs(args.size());
    for(std::vector<SType*>::iterator it = args.begin();it != args.end();it++){
        llvmArgs.push_back((*it)->type);
    }

    type = llvm::FunctionType::get(returnType->type, llvmArgs, isValArgs);
}

bool SFunctionType::isAssignable(SType *dest) {
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

}

bool SPointerType::isAssignable(SType *dest) {
}