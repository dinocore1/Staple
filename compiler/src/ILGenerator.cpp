#include "stdafx.h"

#include <map>
#include <memory>

using namespace std;

#include <llvm/IR/IRBuilder.h>

using namespace llvm;

namespace staple {

class Location {
public:
    virtual llvm::Value* getValue() = 0;

};

class LLVMValue : public Location {
public:
    LLVMValue(llvm::Value* value)
    : mValue(value) {}

    llvm::Value* getValue() {
        return mValue;
    }

protected:
    llvm::Value* mValue;
};

class Scope {
public:
    Scope* mParent;

    Scope(Scope* scope)
     : mParent(scope) { }

    map<Node*, unique_ptr<Location>> table;


};

class ILGenVisitor : public Visitor {
public:
    using Visitor::visit;
    Scope* mScope;
    ILGenerator* mILGen;

    ILGenVisitor(ILGenerator *generator)
    : mILGen(generator) {
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

    void set(Node* n, Location* l) {
      mScope->table[n] = unique_ptr<Location>(l);
    }

    Location* gen(Node* n) {
        n->accept(this);
        return mScope->table[n].get();
    }

    virtual void visit(Block* block) {

        std::vector<llvm::Type*> argTypes;
        argTypes.push_back(llvm::IntegerType::getInt32Ty(getGlobalContext()));

        FunctionType* ftype = FunctionType::get(mILGen->mIRBuilder.getVoidTy(), argTypes, false);
        Function* blah = Function::Create(ftype, Function::LinkageTypes::ExternalLinkage, "main", &mILGen->mModule);

        push();

        BasicBlock* basicBlock = BasicBlock::Create(getGlobalContext());
        mILGen->mIRBuilder.SetInsertPoint(basicBlock);

        visitChildren(block);
        pop();

        set(block, new LLVMValue(basicBlock));
    }

    virtual void visit(IntLiteral* lit) {
        llvm::Value* value = mILGen->mIRBuilder.getInt(APInt(32, lit->mValue, true));
      set(lit, new LLVMValue(value));
    }

    virtual void visit(Op* op) {
        Location* lleft = gen(op->mLeft);
        Location* lright = gen(op->mRight);

        Location* result;

        switch(op->mOp) {
            case Op::Type::ADD:
                result = new LLVMValue(mILGen->mIRBuilder.CreateAdd(lleft->getValue(), lright->getValue()));
                break;
        }

        set(op, result);
    }

    virtual void visit(Assign* assign) {
        Location* lright = gen(assign->mRight);
        Location* lleft = gen(assign->mLeft);


    }

};

ILGenerator::ILGenerator(CompilerContext* ctx)
 : mCtx(ctx),
   mIRBuilder(getGlobalContext()),
   mModule(ctx->inputFile.getAbsolutePath().c_str(), getGlobalContext())
{
    if(ctx->generateDebugSymobols) {
        mModule.addModuleFlag(llvm::Module::Warning, "Dwarf Version", 4);
        mModule.addModuleFlag(llvm::Module::Error, "Debug Info Version", llvm::DEBUG_METADATA_VERSION);
        mDIBuider = new DIBuilder(mModule);
    }
}

void ILGenerator::generate() {
    ILGenVisitor visitor(this);
    mCtx->rootNode->accept(&visitor);

    if(mCtx->generateDebugSymobols) {
        mDIBuider->finalize();
    }

    mModule.dump();
}

} // namespace staple
