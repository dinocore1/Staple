#include "stdafx.h"

#include <iostream>
#include <sstream>

using namespace std;
using namespace llvm;

namespace staple {

Type::Type()
  : mTypeId(TypeId::Unknown) { }

Type::Type(TypeId id)
  : mTypeId(id) { }

class VoidType : public Type {
public:
  VoidType()
    : Type(TypeId::Void) {}

  bool isAssignableFrom(const Type* type) const {
    return isa<VoidType>(type);
  }

  std::string toString() const {
    return "void";
  }

  static inline bool classof(const Type* T) {
    return T->mTypeId == Type::TypeId::Void;
  }
};

class BoolType : public Type {
public:
  BoolType()
    : Type(TypeId::Bool) {}

  bool isAssignableFrom(const Type* type) const {
    return isa<BoolType>(type) || isa<IntegerType>(type);
  }

  std::string toString() const {
    return "bool";
  }

  static inline bool classof(const Type* T) {
    return T->mTypeId == Type::TypeId::Bool;
  }
};

VoidType VOIDTYPE;
BoolType BOOLTYPE;

const Type* Primitives::Void(&VOIDTYPE);
const Type* Primitives::Bool(&BOOLTYPE);
const IntegerType Primitives::UInt8(8, false);
const IntegerType Primitives::Int8(8, true);
const IntegerType Primitives::UInt16(16, false);
const IntegerType Primitives::Int16(16, true);
const IntegerType Primitives::UInt32(32, false);
const IntegerType Primitives::Int32(32, true);
const IntegerType Primitives::Int64(64, true);
const ArrayType Primitives::StringLiteral(&Primitives::UInt8);

bool IntegerType::isAssignableFrom(const Type* type) const {
  if(isa<PointerType>(type)) {
    const PointerType* ptrType = cast<PointerType>(type);
    return isAssignableFrom(ptrType->mBase);
  } else {
    return isa<IntegerType>(type) || isa<FloatType>(type);
  }
}

std::string IntegerType::toString() const {
  stringbuf buf;
  ostream os(&buf);
  os << "int" << mWidth;
  return buf.str();
}

bool ClassType::isAssignableFrom(const Type* type) const {
  //TODO: 
  return false;
}

std::string ClassType::toString() const {
  return "class " + mFQName.getFullString();
}

ArrayType::ArrayType(const Type* baseType)
  : Type(TypeId::Array), mBase(baseType) {}

bool ArrayType::isAssignableFrom(const Type* type) const {

}

std::string ArrayType::toString() const {
  return "array";
}

FunctionType::FunctionType(const std::vector<Type*>& params, Type* retType)
 : Type(TypeId::Function), mParams(params), mReturnType(retType)
 {}

bool FunctionType::isAssignableFrom(const Type* type) const {

}

std::string FunctionType::toString() const {
  return "function";
}

PointerType::PointerType(Type* baseType)
 : Type(TypeId::Pointer), mBase(baseType) {}

bool PointerType::isAssignableFrom(const Type* type) const {
    if(isa<PointerType>(type)) {
        const PointerType* ptrType = cast<PointerType>(type);
        return mBase->isAssignableFrom(ptrType->mBase);
        
    } else if(isa<ArrayType>(type)) {
        const ArrayType* arrayType = cast<ArrayType>(type);
        return mBase->isAssignableFrom(arrayType->mBase);
    } else {
        return false;
    }
}

std::string PointerType::toString() const {
  return mBase->toString() + "*";
}

} // namespace staple
