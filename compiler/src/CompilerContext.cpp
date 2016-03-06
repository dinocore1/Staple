#include "stdafx.h"

#include "CompilerContext.h"

namespace staple {

CompilerContext::CompilerContext()
  : rootNode(NULL), generateDebugSymobols(false) {

}

void CompilerContext::setInputFile(const File& file) {
  inputFile = file;
}

void CompilerContext::addIncludeDir(const File& file) {
  includeDirs.push_back(file);
}

bool CompilerContext::parse() {
  bool retval = parserContext.parse(inputFile);
  rootNode = parserContext.rootNode;
  return retval;
}

} // namespace staple
