
#ifndef SEMPASS_H_
#define SEMPASS_H_

#include "compilercontext.h"

namespace staple {

class SemPass {
friend class TypeVisitor;

private:
    unsigned int numErrors;

protected:
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