#ifndef UTILS_H_
#define UTILS_H_

namespace staple {

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

  void add(const std::string&);

  std::string getSimpleName() const;
  std::string getPackageName() const;
  std::string getFullString() const;
  size_t getNumParts() const;

private:
  std::vector< std::string > mParts;
};

} // namespace staple

#endif // UTILS_H_
