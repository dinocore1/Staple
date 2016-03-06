#include "stdafx.h"

namespace staple {

  Type::Type() {}

  const Type Primitives::Void;
  const Type Primitives::Bool;

  const IntegerType Primitives::UInt8(8, false);
  const IntegerType Primitives::Int8(8, true);
  const IntegerType Primitives::UInt16(16, false);
  const IntegerType Primitives::Int16(16, true);
  const IntegerType Primitives::UInt32(32, false);
  const IntegerType Primitives::Int32(32, true);

} // namespace staple
