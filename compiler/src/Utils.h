#ifndef UTILS_H_
#define UTILS_H_

namespace staple {

class FQPath {
public:
  FQPath();
  FQPath(const std::vector< std::string >&);

  void add(const std::string&);

  std::string getShortName() const;
  std::string getPackageName() const;
  std::string getFullString() const;

  std::vector< std::string > mParts;
};

} // namespace staple

#endif // UTILS_H_
