

class SemPass {
private:
    unsigned int numErrors;
public:
    SemPass();

    void doSemPass(NCompileUnit& root);
    bool hasErrors();
    void logError(YYLTYPE location, const char* format, ...);
    void logWarning(YYLTYPE location, const char* format, ...);
};