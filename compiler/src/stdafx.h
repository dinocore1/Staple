#ifndef STDAFX_H_
#define STDAFX_H_

#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>

#include "FileUtils.h"

namespace staple {

class Visitor;
class NCompileUnit;
class Node;
class NClass;
class NField;
class NMethod;
class NType;
class NParam;
class NLocalVar;
class Stmt;
class Expr;
class Not;
class Neg;
class Id;

class ParserContext;

} //namespace staple

#include "staple_parser.hpp"

#include "ParserContext.h"
#include "Node.h"
#include "CompilerContext.h"
#include "ILGenerator.h"


#endif // STDAFX_H_
