
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

    private:
        CompilerContext* mContext;
        NCompileUnit* mCompileUnit;
        StapleFunction* mCurrentFunctionType;
        StapleClass *mCurrentClass;
        Scope* scope;

        void push();
        void pop();
        void define(const string& name, StapleType* type);
        StapleType* resolve(const string& name);
        StapleType* getType(ASTNode* node);

    public:
        Pass3TypeVisitor(CompilerContext* ctx);

        void visit(NCompileUnit* compileUnit) override;
        void visit(NFunction* functionDecl) override;
        void visit(NArgument* argument) override;
        void visit(NType* type) override;
        void visit(NField* field) override;
        void visit(NExpressionStatement* expressionStatement) override;
        void visit(NMethodFunction* methodFunction) override;
        void visit(NReturn* returnexp) override;
        void visit(NVariableDeclaration* variableDeclaration) override;
        void visit(NIntLiteral* intLiteral) override;
        void visit(NStringLiteral* literal) override;
        void visit(NAssignment* assignment) override;
        void visit(NSizeOf* nsizeOf) override;
        void visit(NNew* newNode) override;
        void visit(NIdentifier* identifier) override;
        void visit(NArrayElementPtr* arrayElementPtr) override;
        void visit(NMemberAccess* memberAccess) override;
        void visit(NIfStatement* ifStatement) override;
        void visit(NForLoop* forLoop) override;
        void visit(NBinaryOperator* binaryOperator) override;
        void visit(NMethodCall* methodCall) override;
        void visit(NFunctionCall* functionCall) override;
        void visit(NLoad* load) override;
        void visit(NBlock* block) override;

    };

}

#endif //STAPLE_TYPEVISITOR_H
