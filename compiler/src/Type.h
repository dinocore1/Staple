#ifndef TYPE_H_
#define TYPE_H_

namespace staple {

class Type {
public:

  enum TypeId {
    Void,
    Bool,
    Integer,
    Float,
    Function,
    Object,
    Array,
    Pointer,
    Unknown
  };

  Type();
  Type(TypeId id);

  virtual bool isAssignableFrom(Type* type) const = 0;
  virtual std::string toString() const = 0;

  TypeId mTypeId;

};

class IntegerType : public Type {
public:
  IntegerType(uint16_t width, int sign)
    : Type(TypeId::Integer), mWidth(width), mSigned(sign) {}

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;

  const bool mSigned;
  const uint16_t mWidth;

  static inline bool classof(const Type* T) {
    return T->mTypeId == Type::TypeId::Integer;
  }
};

class FloatType : public Type {
public:
  FloatType(uint16_t width)
    : Type(TypeId::Float), mWidth(width) {}

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;

  static inline bool classof(const Type* T) {
    return T->mTypeId == Type::TypeId::Float;
  }

  const uint16_t mWidth;
};

class PointerType : public Type {
public:
  PointerType(Type* baseType);

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;

  Type* mBase;
};

class ArrayType : public Type {
public:
  ArrayType(Type* baseType)
    : mBase(baseType) {}

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;
  const Type* mBase;
};

class FunctionType : public Type {
public:
  FunctionType(std::vector<Type*> params, Type* retType)
    : mParams(params), mReturnType(retType) {}

  std::vector<Type*> mParams;
  Type* mReturnType;

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;
};

class ClassType : public Type {
public:
  ClassType(const FQPath& fqName)
    : mFQName(fqName) {}

  FQPath mFQName;

  bool isAssignableFrom(Type* type) const;
  std::string toString() const;
};

class Primitives {
public:
  static const Type* Void;
  static const Type* Bool;
  static const IntegerType UInt8;
  static const IntegerType Int8;
  static const IntegerType UInt16;
  static const IntegerType Int16;
  static const IntegerType UInt32;
  static const IntegerType Int32;
  static const ClassType String;
};

} // namespace staple

#endif // TYPE_H_
