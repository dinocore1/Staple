#include "stdafx.h"

#include <map>
#include <memory>

using namespace std;

#include <llvm/IR/IRBuilder.h>

using namespace llvm;

namespace staple {

class Location {
public:
    Value* llvmValue;

};

class IntConstant : public Location {
public:
  IntConstant(int64_t value)
   : mValue(value) {

  };

  int64_t mValue;
};

class Temp : public Location {
public:
    Temp() {};
};

class Scope {
public:
    Scope* mParent;

    Scope(Scope* scope)
     : mParent(scope) { }

    map<Node*, unique_ptr<Location>> table;
    vector<unique_ptr<Location>> temps;

    Location* createTemp() {
        Temp* retval = new Temp();
        temps.push_back(unique_ptr<Location>(retval));
        return retval;
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
      mScope->table[n] = unique_ptr<Location>(l);
    }

    Location* gen(Node* n) {
        n->accept(this);
        return mScope->table[n].get();
    }

    virtual void visit(Block* block) {
        push();
        visitChildren(block);
        pop();
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
    mRootNode->accept(&visitor);
}

} // namespace staple
