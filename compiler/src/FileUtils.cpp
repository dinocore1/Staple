#include "stdafx.h"
#include "FileUtils.h"

#include <iostream>
#include <sstream>

#ifdef _WIN32
  static const char PATH_SEP = '\\';
  #include <direct.h>
  #define getcwd _getcwd
#else
  static const char PATH_SEP = '/';
  #include <unistd.h>
#endif

using namespace std;

namespace staple {

static void processPath(const string& filepath, vector<string>& pathParts) {
  const size_t strLen = filepath.length();
  size_t i;
  size_t pos = 0;
  while( (i = filepath.find_first_of(PATH_SEP, pos)) != string::npos) {
    string part = filepath.substr(pos, i - pos);
    if(part.length() > 0) {
      if(part.compare("..") == 0) {
        pathParts.pop_back();
      } else {
        pathParts.push_back(part);
      }
    }
    pos = i+1;
  }
  string part = filepath.substr(pos, strLen - pos);
  if(part.length() > 0) {
    pathParts.push_back(part);
  }
}

File::File() {
  char pathBuf[FILENAME_MAX];
  processPath(getcwd(pathBuf, FILENAME_MAX), mPathParts);
}

File::File(const File& root, const std::string& filepath) {
  mPathParts = root.mPathParts;
  processPath(filepath, mPathParts);
}

File::File(const std::string& filepath)
 : File() {
  processPath(filepath, mPathParts);
}

std::string File::getFilename() const {
  return mPathParts.back();
}

std::string File::getAbsolutePath() const {
  stringbuf buf;
  ostream os (&buf);

  for(size_t i=0;i<mPathParts.size();i++) {
    os << PATH_SEP;
    os << mPathParts[i];
  }

  return buf.str();
}

} // namespace staple
