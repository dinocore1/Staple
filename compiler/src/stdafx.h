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
class Node;
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
#include "VarCollector.h"
#include "ILGenerator.h"


#endif // STDAFX_H_
