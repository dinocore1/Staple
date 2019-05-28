#include "stdafx.h"

#include "CompilerContext.h"

namespace staple {

CompilerContext::CompilerContext()
  : outputFile("-"), rootNode(NULL), generateDebugSymobols(false) {

}

void CompilerContext::setInputFile(const File& file) {
  inputFile = file;
}

void CompilerContext::setOutputFile(const std::string& str) {
  outputFile = str;
}

void CompilerContext::addIncludeDir(const File& file) {
  includeDirs.push_back(file);
}

bool CompilerContext::parse() {
  bool retval = parserContext.parse(inputFile);
  if(retval) {
    rootNode = parserContext.rootNode;
    rootNode->mFile = inputFile;
  }
  return retval;
}

void CompilerContext::addError(const std::string& message, uint32_t line, uint32_t column) {
  FileLocation location(inputFile, line, column);
  CompilerMessage msg(CompilerMessage::Type::ERROR, message, location);
  mMessages.push_back(msg);
}

void CompilerContext::addError(const std::string& message, const FileLocation& location) {
  CompilerMessage msg(CompilerMessage::Type::ERROR, message, location);
  mMessages.push_back(msg);
}

bool CompilerContext::hasErrors() const {
  for(const CompilerMessage& msg : mMessages) {
    if(msg.mType == CompilerMessage::Type::ERROR) {
      return true;
    }
  }
  return false;
}

} // namespace staple
