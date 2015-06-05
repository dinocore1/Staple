
#ifndef STAPLE_IMPORTPASS_H
#define STAPLE_IMPORTPASS_H

#include <set>

#include "compilercontext.h"

namespace staple {
    using namespace std;

    class ScopeTreeNode;

    bool treenodecomp(const ScopeTreeNode* lhs, const ScopeTreeNode* rhs);

    class ScopeTreeNode {
    public:
        const string name;
        const ScopeTreeNode* parent;
        set<ScopeTreeNode*, bool(*)(const ScopeTreeNode*, const ScopeTreeNode*)> mChildren;

        Scope* scope;

        ScopeTreeNode(ScopeTreeNode* parent, const string& name, Scope* scope);

    };

    class ImportManager {
    private:
        CompilerContext* mContext;
        set<string> mVisitedPaths;
        ScopeTreeNode* mScopeTreeRoot;

    public:
        ImportManager(CompilerContext* ctx);

        ScopeTreeNode* getScope(const string& path);
        StapleType* resolveClassType(NCompileUnit* startingCompileUnit, const string& className);
    };
}

#endif //STAPLE_IMPORTPASS_H
