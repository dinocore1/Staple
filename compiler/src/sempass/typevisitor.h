
#ifndef STAPLE_TYPEVISITOR_H
#define STAPLE_TYPEVISITOR_H

#include <map>
#include <set>

namespace staple {
    using namespace std;

    /**
     * First pass type visitor recursively visits all import
     * files, parses them, and creates symbol objects for
     * functions and classes.
     */
    class Pass1TypeVisitor : public ASTVisitor {
        using ASTVisitor::visit;
    private:
        CompilerContext* mContext;

    public:
        set<string> mFQFunctions;
        set<string> mFQClasses;

        Pass1TypeVisitor(CompilerContext* context)
                : mContext(context) { }

        void visit(NCompileUnit* compileUnit);
    };

    /**
     * Second pass type visitor checks types of function
     * arguments and return types. This pass does not dive
     * into the function bodies.
     */
    class Pass2TypeVisitor : public ASTVisitor {
        using ASTVisitor::visit;
    private:
        CompilerContext *mContext;
        StapleClass *mCurrentClass;
        map<ASTNode *, StapleType *> mType;
        NCompileUnit *mCompileUnit;

        StapleType *getType(ASTNode *node) {
            node->accept(this);
            return mType[node];
        }

    public:
        Pass2TypeVisitor(CompilerContext *context) : mContext(context) { }
        void visit(NCompileUnit* compileUnit) override;
        void visit(NFunctionPrototype* functionPrototype) override;
        void visit(NFunction* function) override;
        void visit(NMethodFunction* methodFunction) override;
        void visit(NField* field) override;
        void visit(NArgument* argument) override;
        void visit(NType* type) override;

    };

    /**
     * Third pass type visitor checks all types of function statments
     */
    class Pass3TypeVisitor : public ASTVisitor {
        using ASTVisitor::visit;

    };

}

#endif //STAPLE_TYPEVISITOR_H
