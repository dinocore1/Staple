
#ifndef SEMPASS_H_
#define SEMPASS_H_

#include "scope.h"
#include "compilercontext.h"

namespace staple {

class SemPass {
friend class TypeVisitor;

protected:
    CompilerContext* ctx;

public:
    SemPass(CompilerContext* ctx);
    void doIt();
};

}
#endif /* SEMPASS_H_ */