#include "stdafx.h"

#include <map>

using namespace std;

namespace staple {

class Location {

};

class IntConstant : public Location {
public:

};

class Scope {
public:
    Scope* mParent;

    Scope(Scope* scope)
     : mParent(scope) { }

    map<Node*, Location*> table;

    Location* createTemp() {
        return new Location();
    }


};

class ILGenVisitor : public Visitor {
public:
    using Visitor::visit;
    Scope* mScope;

    ILGenVisitor() {
        push();
    }

    void push() {
        mScope = new Scope(mScope);
    }

    void pop() {
        Scope* top = mScope;
        mScope = mScope->mParent;
        delete top;
    }

    Location* createTemp() {
        return mScope->createTemp();
    }

    Location* gen(Node* n) {
        visit(n);
        return mScope->table[n];
    }

    virtual void visit(IntLiteral* lit) {
        return new IntConstant(lit->mValue);
    }

    virtual void visit(Op* op) {
        Location* lleft = gen(op->mLeft);
        Location* lright = gen(op->mRight);
        Location* result = createTemp();
    }

    virtual void visit(Assign* assign) {
        Location* lright = gen(assign->mRight);

    }

};

ILGenerator::ILGenerator(Node *rootNode)
 : mRootNode(rootNode) {

}

void ILGenerator::generate() {
    ILGenVisitor visitor;
    visitor.visit(mRootNode);
}

} // namespace staple


