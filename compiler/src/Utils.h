#ifndef UTILS_H_
#define UTILS_H_

namespace staple {

class FQPath;

class File {
public:

  File();
  File(const File& root, const std::string& filepath);
  File(const File& root, const FQPath& path);
  File(const std::string& filepath);
  File(const char*);

  bool isDirectory() const;
  bool isFile() const;
  std::string getName() const;
  const std::string& getPath() const;
  std::string getAbsolutePath() const;
  std::string getCanonicalPath() const;


private:
  std::string mPath;
};

class FileLocation {
public:
  static FileLocation UNKNOWN;
  FileLocation(const File& file, uint32_t line, uint32_t column);

  const File& mFile;
  const uint32_t mLine;
  const uint32_t mColumn;
};

class CompilerMessage {
public:
  enum Type {
    WARNING,
    ERROR
  };

  CompilerMessage(Type type, const std::string& msg,
                  const FileLocation& location);

  const Type mType;
  const std::string mMessage;
  const FileLocation mLocation;

  std::string toString() const;
};

class FQPath {
public:
  FQPath();
  FQPath(const std::vector< std::string >&);
  FQPath(const std::string&);

  void add(const std::string&);

  std::string getSimpleName() const;
  std::string getPackageName() const;
  std::string getFullString() const;
  size_t getNumParts() const;
  const std::string& part(size_t) const;
  bool operator< (const FQPath&) const;

private:
  std::vector< std::string > mParts;
};

} // namespace staple

#endif // UTILS_H_
