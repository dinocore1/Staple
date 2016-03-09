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

  bool isAssignableFrom(Type* type) const {
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

  bool isAssignableFrom(Type* type) const {
    return isa<BoolType>(type);
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

const ClassType Primitives::String(FQPath("string"));

bool IntegerType::isAssignableFrom(Type* type) const {
  return isa<IntegerType>(type) || isa<FloatType>(type);
}

std::string IntegerType::toString() const {
  stringbuf buf;
  ostream os(&buf);
  os << "int" << mWidth;
  return buf.str();
}

bool ClassType::isAssignableFrom(Type* type) const {

}

std::string ClassType::toString() const {

}

bool ArrayType::isAssignableFrom(Type* type) const {

}

std::string ArrayType::toString() const {

}

bool FunctionType::isAssignableFrom(Type* type) const {

}

std::string FunctionType::toString() const {

}

} // namespace staple
