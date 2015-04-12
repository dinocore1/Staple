

#ifndef _STAPLE_PASS1CLASSVISITOR_H_
#define _STAPLE_PASS1CLASSVISITOR_H_

#include "../sempass.h"

namespace staple {

    using namespace std;

    class Pass1ClassVisitor : public ASTVisitor {

    private:
        CompilerContext* mContext;

    public:
        Pass1ClassVisitor(CompilerContext* ctx)
        : mContext(ctx) {}

        using ASTVisitor::visit;

        void visit(NClassDeclaration* classDeclaration) {

            string fqClassName = !mContext->package.empty() ? (mContext->package + "." + classDeclaration->name) : classDeclaration->name;
            StapleClass* stpClass = new StapleClass(fqClassName);
            mContext->typeTable[classDeclaration] = stpClass;
            mContext->defineClass(stpClass);

        }

    };

}


#endif //_STAPLE_PASS1CLASSVISITOR_H_
