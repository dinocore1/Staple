
#ifndef STAPLE_COMPILERCONTEXT_H
#define STAPLE_COMPILERCONTEXT_H



namespace staple {

class CompilerContext {
public:
    CompilerContext();
    ParserContext parserContext;
    File inputFile;
    Node* rootNode;
    bool generateDebugSymobols;

    void setInputFile(const File&);
    bool parse();


};

} // namespace staple;

#endif //STAPLE_COMPILERCONTEXT_H
