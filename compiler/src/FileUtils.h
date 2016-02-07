#ifndef FILEUTILS_H_
#define FILEUTILS_H_

namespace staple {

class File {
public:

  File();
  File(const File& root, const std::string& filepath);
  File(const std::string& filepath);

  std::string getAbsolutePath() const;

private:
  std::vector<std::string> mPathParts;

};

} // namespace staple

#endif // FILEUTILS_H_
