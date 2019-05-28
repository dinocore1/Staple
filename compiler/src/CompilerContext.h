
#ifndef STAPLE_COMPILERCONTEXT_H
#define STAPLE_COMPILERCONTEXT_H

namespace staple {

class CompilerContext {
public:
  CompilerContext();
  ParserContext parserContext;
  File inputFile;
  std::string outputFile;
  std::vector<File> includeDirs;
  NCompileUnit* rootNode;
  bool generateDebugSymobols;

  std::map<const FQPath, staple::Type*> mKnownTypes;
  std::map<staple::Node*, staple::Type*> mTypeTable;
  std::map<const FQPath, Node*> mParsedFiles;

  void setInputFile(const File&);
  void setOutputFile(const std::string&);
  void addIncludeDir(const File&);
  bool parse();

  void addError(const std::string& message, uint32_t line, uint32_t column);
  void addError(const std::string& message, const FileLocation& location = FileLocation::UNKNOWN);
  bool hasErrors() const;

  std::vector<CompilerMessage> mMessages;


};

} // namespace staple;

#endif //STAPLE_COMPILERCONTEXT_H
