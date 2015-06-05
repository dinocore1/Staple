
#ifndef SEMPASS_H_
#define SEMPASS_H_

#include "scope.h"
#include "compilercontext.h"

namespace staple {

class ImportManager;

class SemPass {
friend class TypeVisitor;

private:
    unsigned int numErrors;

protected:
    ImportManager * mImportPass;
    CompilerContext& ctx;

public:
    SemPass(CompilerContext& ctx);

    void doSemPass(NCompileUnit& root);
    bool hasErrors();
    void logError(YYLTYPE location, const char* format, ...);
    void logWarning(YYLTYPE location, const char* format, ...);
};

}
#endif /* SEMPASS_H_ */