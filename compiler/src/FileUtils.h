#ifndef FILEUTILS_H_
#define FILEUTILS_H_

namespace staple {

class File {
public:

  File();
  File(const File& root, const std::string& filepath);
  File(const std::string& filepath);
  File(const char*);

  bool isDirectory() const;
  bool isFile() const;
  std::string getName() const;
  const std::string& getPath() const;
  std::string getAbsolutePath() const;
  std::string getCanonicalPath() const;


private:
  std::string mParent;
  std::string mPath;


};

} // namespace staple

#endif // FILEUTILS_H_
