
#ifndef STAPLE_IMPORTPASS_H
#define STAPLE_IMPORTPASS_H

#include <set>

#include "compilercontext.h"

namespace staple {
    using namespace std;

    class ImportPass {
    private:
        CompilerContext* mContext;
        set<string> mVisitedPaths;

    public:
        ImportPass(CompilerContext* ctx);

        void doIt();
    };
}

#endif //STAPLE_IMPORTPASS_H
