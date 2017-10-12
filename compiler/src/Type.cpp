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

class VargType : public Type {
public:
    VargType()
     : Type(TypeId::VArgs) {}
     
     bool isAssignableFrom(const Type* type) const {
         return true;
     }
     
     static inline bool classof(const Type* T) {
         return T->mTypeId == Type::TypeId::VArgs;
     }
     
     std::string toString() const {
         return "...";
     }
};

VoidType VOIDTYPE;
BoolType BOOLTYPE;
VargType VARGTYPE;

const Type* Primitives::Void(&VOIDTYPE);
const Type* Primitives::Bool(&BOOLTYPE);
const Type* Primitives::Vargs(&VARGTYPE);
const IntegerType Primitives::UInt8(8, false);
const IntegerType Primitives::Int8(8, true);
const IntegerType Primitives::UInt16(16, false);
const IntegerType Primitives::Int16(16, true);
const IntegerType Primitives::UInt32(32, false);
const IntegerType Primitives::Int32(32, true);
const ArrayType Primitives::StringLiteral(&Primitives::UInt8);

bool IntegerType::isAssignableFrom(const Type* type) const {
  return isa<IntegerType>(type) || isa<FloatType>(type);
}

std::string IntegerType::toString() const {
  stringbuf buf;
  ostream os(&buf);
  os << "int" << mWidth;
  return buf.str();
}

bool ClassType::isAssignableFrom(const Type* type) const {

}

std::string ClassType::toString() const {

}

ArrayType::ArrayType(const Type* baseType)
  : Type(TypeId::Array), mBase(baseType) {}

bool ArrayType::isAssignableFrom(const Type* type) const {

}

std::string ArrayType::toString() const {

}

bool FunctionType::isAssignableFrom(const Type* type) const {

}

std::string FunctionType::toString() const {

}

PointerType::PointerType(Type* baseType)
 : Type(TypeId::Pointer), mBase(baseType) {}

bool PointerType::isAssignableFrom(const Type* type) const {
    if(isa<PointerType>(type)) {
        const PointerType* ptrType = reinterpret_cast<const PointerType*>(type);
        return mBase->isAssignableFrom(ptrType->mBase);
        
    } else if(isa<ArrayType>(type)) {
        const ArrayType* arrayType = reinterpret_cast<const ArrayType*>(type);
        return mBase->isAssignableFrom(arrayType->mBase);
    } else {
        return false;
    }
}

std::string PointerType::toString() const {
  return mBase->toString() + "*";
}

} // namespace staple
