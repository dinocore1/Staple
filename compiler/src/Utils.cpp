#include "stdafx.h"
#include "Utils.h"

#include <iostream>
#include <sstream>

using namespace std;

template < class ContainerT >
static void tokenize(const std::string& str, ContainerT& tokens,
                     const std::string& delimiters = " ", bool trimEmpty = false) {
  std::string::size_type pos, lastPos = 0;

  using value_type = typename ContainerT::value_type;
  using size_type  = typename ContainerT::size_type;

  while(true) {
    pos = str.find_first_of(delimiters, lastPos);
    if(pos == std::string::npos) {
      pos = str.length();

      if(pos != lastPos || !trimEmpty)
        tokens.push_back(value_type(str.data()+lastPos,
                                    (size_type)pos-lastPos));

      break;
    } else {
      if(pos != lastPos || !trimEmpty)
        tokens.push_back(value_type(str.data()+lastPos,
                                    (size_type)pos-lastPos));
    }

    lastPos = pos + 1;
  }
}

namespace staple {

FileLocation FileLocation::UNKNOWN("null", -1, -1);

FileLocation::FileLocation(const File& file,
                           uint32_t line, uint32_t column)
  : mFile(file), mLine(line), mColumn(column) { }

CompilerMessage::CompilerMessage(Type type,
                                 const std::string& msg, const FileLocation& location)
  : mType(type), mMessage(msg), mLocation(location) { }

static std::string getTypeString(CompilerMessage::Type type) {
  switch(type) {
  case CompilerMessage::Type::ERROR:
    return "error";

  case CompilerMessage::Type::WARNING:
    return "warn";
  }
}

std::string CompilerMessage::toString() const {
  stringbuf buf;
  ostream os(&buf);

  os << getTypeString(mType) << " " << mLocation.mFile.getAbsolutePath();
  os << " " << mLocation.mLine << ":" << mLocation.mColumn;
  os << ": " << mMessage;

  return buf.str();
}

FQPath::FQPath() {}
FQPath::FQPath(const std::vector<std::string>& parts)
  : mParts(parts) {}

FQPath::FQPath(const std::string& str) {
  tokenize(str, mParts, ".", true);
}

void FQPath::add(const std::string& part) {
  mParts.push_back(part);
}

size_t FQPath::getNumParts() const {
  return mParts.size();
}

std::string FQPath::getSimpleName() const {
  return mParts[mParts.size()-1];
}

std::string FQPath::getFullString() const {
  stringbuf buf;
  ostream os(&buf);

  for(size_t i=0; i<mParts.size(); i++) {
    os << ".";
    os << mParts[i];
  }

  return buf.str();
}

} // namespace staple
