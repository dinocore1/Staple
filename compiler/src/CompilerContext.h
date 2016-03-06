
#ifndef STAPLE_COMPILERCONTEXT_H
#define STAPLE_COMPILERCONTEXT_H



namespace staple {

class CompilerContext {
public:
  CompilerContext();
  ParserContext parserContext;
  File inputFile;
  std::vector<File> includeDirs;
  Node* rootNode;
  bool generateDebugSymobols;

  std::map<std::string, staple::ClassType*> mClasses;
  std::map<std::string, staple::FunctionType*> mFunctions;

  void setInputFile(const File&);
  void addIncludeDir(const File&);
  bool parse();


};

} // namespace staple;

#endif //STAPLE_COMPILERCONTEXT_H
