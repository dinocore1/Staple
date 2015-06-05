
#ifndef STAPLE_IMPORTPASS_H
#define STAPLE_IMPORTPASS_H

#include <set>

#include "compilercontext.h"

namespace staple {
    using namespace std;

    /*
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
     */

    class Pass1ClassVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        CompilerContext* mContext;
        static set<string> mPass1VisitedPaths;

    public:
        Pass1ClassVisitor(CompilerContext* context) : mContext(context)
        { }

        void visit(NCompileUnit* compileUnit);
    };

    class Pass2ClassVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        CompilerContext *mContext;
        static set<string> mPass2VisitedPaths;
        StapleClass *mCurrentClass;
        map<ASTNode *, StapleType *> mType;
        NCompileUnit *mCompileUnit;

        StapleType *getType(ASTNode *node) {
            node->accept(this);
            return mType[node];
        }

    public:
        Pass2ClassVisitor(CompilerContext *context) : mContext(context) { }
        void visit(NField* field);
        void visit(NType* type);
        void visit(NCompileUnit* compileUnit);
    };

}

#endif //STAPLE_IMPORTPASS_H
