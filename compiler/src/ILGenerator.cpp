#include "stdafx.h"

#include <map>

using namespace std;

namespace staple {

class Location {

};

class IntConstant : public Location {
public:
  IntConstant(int64_t value)
   : mValue(value) {};

  int64_t mValue;

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

    void set(Node* n, Location* l) {
      mScope->table[n] = l;
    }

    Location* gen(Node* n) {
        visit(n);
        return mScope->table[n];
    }

    virtual void visit(IntLiteral* lit) {
      set(lit, new IntConstant(lit->mValue));
    }

    virtual void visit(Op* op) {
        Location* lleft = gen(op->mLeft);
        Location* lright = gen(op->mRight);
        Location* result = createTemp();

        set(op, result);
    }

    virtual void visit(Assign* assign) {
        Location* lright = gen(assign->mRight);
        Location* lleft = gen(assign->mLeft);

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
