#ifndef TYPE_H_
#define TYPE_H_

namespace staple {

  class Type {
  public:
    Type();
  };

  class IntegerType : public Type {
  public:
    IntegerType(uint16_t width, int sign)
    : mWidth(width), mSigned(sign) {}

    const bool mSigned;
    const uint16_t mWidth;
  };

  class FloatType : public Type {
  public:
    FloatType(uint16_t width)
    : mWidth(width) {}

    const uint16_t mWidth;
  };

  class PointerType : public Type {
  public:
    PointerType(Type* baseType)
    : mBase(baseType) {}

    Type* mBase;
  };

  class ArrayType : public Type {
  public:
    ArrayType(Type* baseType)
    : mBase(baseType) {}

    const Type* mBase;
  };

  class FunctionType : public Type {
  public:
    FunctionType(std::vector<Type*> params, Type* retType)
    : mParams(params), mReturnType(retType) {}

    std::vector<Type*> mParams;
    Type* mReturnType;
  };

  class ClassType : public Type {
  public:
    ClassType(const FQPath& fqName)
    : mFQName(fqName) {}

    FQPath mFQName;
  };

  class Primitives {
  public:
    static const Type Void;
    static const Type Bool;
    static const IntegerType UInt8;
    static const IntegerType Int8;
    static const IntegerType UInt16;
    static const IntegerType Int16;
    static const IntegerType UInt32;
    static const IntegerType Int32;
  };

}


#endif // TYPE_H_
