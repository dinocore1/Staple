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

File::File()
: mParent("") {
  char pathBuf[FILENAME_MAX];
  mPath = getcwd(pathBuf, FILENAME_MAX);
}

File::File(const File& root, const std::string& filepath)
: mParent(root.getPath()), mPath(filepath) {
}

File::File(const std::string& filepath) {
  char pathBuf[FILENAME_MAX];
  mParent = getcwd(pathBuf, FILENAME_MAX);
  mPath = filepath;
}

File::File(const char* filepath) {
  char pathBuf[FILENAME_MAX];
  mParent = getcwd(pathBuf, FILENAME_MAX);
  mPath = filepath;
}

std::string File::getName() const {
  size_t i = mPath.find_last_of(PATH_SEP);
  if(i == string::npos) {
    return mPath;
  } else {
    const size_t strLen = mPath.length();
    return mPath.substr(i + i, strLen - i);
  }
}

const std::string& File::getPath() const {
  return mPath;
}

std::string File::getAbsolutePath() const {
  return mParent + PATH_SEP + mPath;
}

std::string File::getCanonicalPath() const {
  std::vector<std::string> pathParts;
  processPath(mParent, pathParts);
  processPath(mPath, pathParts);

  stringbuf buf;
  ostream os (&buf);

  for(size_t i=0;i<pathParts.size();i++) {
    os << PATH_SEP;
    os << pathParts[i];
  }

  return buf.str();
}

} // namespace staple
