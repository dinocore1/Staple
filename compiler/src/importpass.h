
#ifndef STAPLE_IMPORTPASS_H
#define STAPLE_IMPORTPASS_H

#include <map>
#include <set>

#include "compilercontext.h"

namespace staple {
    using namespace std;

    class Pass1ClassVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        CompilerContext* mContext;
        NCompileUnit *mCompileUnit;
        static set<string> mPass1VisitedPaths;
        bool mSetImport;

    public:
        set<string> mFQFunctions;
        set<string> mFQClasses;
        vector<unique_ptr<Pass1ClassVisitor>> mImportVisitors;

        Pass1ClassVisitor(CompilerContext* context, bool setImport) : mContext(context), mSetImport(setImport)
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
        void visit(NCompileUnit* compileUnit) override;
        void visit(NFunctionPrototype* functionPrototype) override;
        void visit(NFunction* function) override;
        void visit(NMethodFunction* methodFunction) override;
        void visit(NField* field) override;
        void visit(NArgument* argument) override;
        void visit(NType* type) override;

    };

}

#endif //STAPLE_IMPORTPASS_H
