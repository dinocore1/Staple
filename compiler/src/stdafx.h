#ifndef STDAFX_H_
#define STDAFX_H_

#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <map>

#include "Utils.h"

namespace staple {

class FQPath;
class Type;

class Visitor;
class NCompileUnit;
class Node;
class NClass;
class NField;
class NMethod;
class NType;
class NArrayType;
class NPointerType;
class NParam;
class NLocalVar;
class NStmt;
class NBlock;
class NExpr;
class Not;
class Neg;
class Id;

class ParserContext;

} //namespace staple

#include "staple_parser.hpp"

#include "Utils.h"
#include "ParserContext.h"
#include "Type.h"
#include "Node.h"
#include "CompilerContext.h"
#include "ILGenerator.h"


#endif // STDAFX_H_
