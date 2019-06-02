#include "stdafx.h"

#include <iostream>
#include <sstream>

#ifdef _WIN32
static const char PATH_SEP = '\\';
#include <direct.h>
#define getcwd _getcwd

#define BUFSIZE 4096

static inline std::string normalize(const std::string& input)
{
  TCHAR buffer[BUFSIZE];
  GetFullPathName(input.c_str(), BUFSIZE, buffer, NULL);
  return std::string(buffer);
}

static inline bool isDir(const char* path) {
  DWORD dwAttrib = GetFileAttributesA(dirName_in.c_str());
  return (dwAttrib != INVALID_FILE_ATTRIBUTES &&
          (dwAttrib & FILE_ATTRIBUTE_DIRECTORY));
}

static inline bool isFile(const char* path) {
  DWORD dwAttrib = GetFileAttributesA(dirName_in.c_str());
  return (dwAttrib != INVALID_FILE_ATTRIBUTES &&
          !(dwAttrib & FILE_ATTRIBUTE_DIRECTORY));
}

#else
static const char PATH_SEP = '/';
#include <unistd.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>

#define BUFSIZE 4096

static inline std::string normalize(const std::string& input) {
  char buffer[BUFSIZE];
  realpath(input.c_str(), buffer);
  return std::string(buffer);
}

static inline bool isDir(const char* path) {
  struct stat statinfo;
  int rc = stat(path, &statinfo);
  if(rc == 0) {
    return S_ISDIR(statinfo.st_mode);
  } else {
    return false;
  }
}

static inline bool isFile(const char* path) {
  struct stat statinfo;
  int rc = stat(path, &statinfo);
  if(rc == 0) {
    return S_ISREG(statinfo.st_mode);
  } else {
    return false;
  }
}

#endif

using namespace std;

namespace staple {

static void processPath(const string& filepath, vector<string>& pathParts) {
  const size_t strLen = filepath.length();
  size_t i;
  size_t pos = 0;
  while((i = filepath.find_first_of(PATH_SEP, pos)) != string::npos) {
    string part = filepath.substr(pos, i - pos);
    if(part.length() > 0) {
      if(part.compare("..") == 0) {
        pathParts.pop_back();
      } if(part.compare(".") == 0) {

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
{
  char pathBuf[FILENAME_MAX];
  getcwd(pathBuf, FILENAME_MAX);
  mPath = std::string(pathBuf);
}

File::File(const File& root, const std::string& filepath)
{
  //this.path = fs.resolve(root.path, fs.normalize(child))
  mPath = root.mPath + PATH_SEP + filepath;
}

File::File(const File& root, const FQPath& path)
{
  stringbuf buf;
  ostream os(&buf);

  os << root.getPath();

  for(size_t i=0; i<path.getNumParts(); i++) {
    os << PATH_SEP;
    os << path.part(i);
  }

  mPath = buf.str();
}


File::File(const std::string& filepath)
 : mPath(filepath)
{}

File::File(const char* filepath)
 : mPath(filepath)
{}

bool File::isDirectory() const {
  std::string path = getAbsolutePath();
  return ::isDir(path.c_str());
}

bool File::isFile() const {
  std::string path = getAbsolutePath();
  return ::isFile(path.c_str());
}

std::string File::getName() const {
  size_t i = mPath.find_last_of(PATH_SEP);
  if(i == string::npos) {
    return mPath;
  } else {
    const size_t strLen = mPath.length();
    return mPath.substr(i, strLen - i);
  }
}

const std::string& File::getPath() const {
  return mPath;
}

std::string File::getAbsolutePath() const {
  return normalize(mPath);
}

/*
std::string File::getCanonicalPath() const {
  std::vector<std::string> pathParts;
  processPath(mPath, pathParts);

  stringbuf buf;
  ostream os(&buf);

  for(size_t i=0; i<pathParts.size(); i++) {
    os << PATH_SEP;
    os << pathParts[i];
  }

  return buf.str();
}
*/

} // namespace staple
