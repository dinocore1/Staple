#include "stdafx.h"
#include "Utils.h"

#include <iostream>
#include <sstream>

using namespace std;

template < class ContainerT >
static void tokenize(const std::string& str, ContainerT& tokens,
              const std::string& delimiters = " ", bool trimEmpty = false)
{
   std::string::size_type pos, lastPos = 0;

   using value_type = typename ContainerT::value_type;
   using size_type  = typename ContainerT::size_type;

   while(true)
   {
      pos = str.find_first_of(delimiters, lastPos);
      if(pos == std::string::npos)
      {
         pos = str.length();

         if(pos != lastPos || !trimEmpty)
            tokens.push_back(value_type(str.data()+lastPos,
                  (size_type)pos-lastPos ));

         break;
      }
      else
      {
         if(pos != lastPos || !trimEmpty)
            tokens.push_back(value_type(str.data()+lastPos,
                  (size_type)pos-lastPos ));
      }

      lastPos = pos + 1;
   }
}

namespace staple {

  FQPath::FQPath() {}
  FQPath::FQPath(const std::vector<std::string>& parts)
  : mParts(parts) {}

  void FQPath::add(const std::string& part) {
    mParts.push_back(part);
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
