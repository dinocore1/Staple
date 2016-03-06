#include "stdafx.h"

#include <map>
#include <set>
#include <memory>

using namespace std;

#include <llvm/IR/IRBuilder.h>

using namespace llvm;

namespace staple {

class Location {
public:
  virtual ~Location() {};
  virtual llvm::Value* getValue() = 0;
  virtual bool isAddress() = 0;

};

class LLVMValue : public Location {
public:
  LLVMValue(llvm::Value* value)
    : mValue(value) {}

  llvm::Value* getValue() {
    return mValue;
  }

  bool isAddress() {
    return false;
  }

protected:
  llvm::Value* mValue;
};

class LLVMAddress : public Location {
public:
  LLVMAddress(llvm::Value* address)
    : mAddress(address) {}

  llvm::Value* getValue() {
    return mAddress;
  }

  bool isAddress() {
    return true;
  }

protected:
  llvm::Value* mAddress;
};

class Scope {
public:
  Scope* mParent;

  Scope(Scope* scope)
    : mParent(scope) { }

  ~Scope() {
    for(Location* l : managedLocations) {
      delete l;
    }
  }


  void defineSymbol(const std::string& symbolName, Location* l) {
    symbolTable[symbolName] = l;
    managedLocations.insert(l);
  }

  Location* lookup(const std::string& symbolName) {
    auto it = symbolTable.find(symbolName);
    if(it == symbolTable.end() && mParent != nullptr) {
      return mParent->lookup(symbolName);
    } else {
      return (*it).second;
    }
  }

  map<Node*, Location*> locationTable;
  map<const std::string, Location*> symbolTable;
  set<Location*> managedLocations;


};

class ILGenVisitor : public Visitor {
public:
  using Visitor::visit;
  Scope* mScope;
  ILGenerator* mILGen;
  llvm::Function* mCurrentFunction;

  ILGenVisitor(ILGenerator* generator)
    : mScope(nullptr), mILGen(generator) {
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
    mScope->locationTable[n] = l;
    mScope->managedLocations.insert(l);
  }

  Location* gen(Node* n) {
    n->accept(this);
    return mScope->locationTable[n];
  }

  BasicBlock* getBasicBlock(NBlock* block) {
    IRBuilder<>::InsertPointGuard guard(mILGen->mIRBuilder);

    push();
    BasicBlock* basicBlock = BasicBlock::Create(getGlobalContext());
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);
    visitChildren(block);
    pop();

    return basicBlock;
  }

  void visit(NIfStmt* ifStmt) {
    Location* lcondition = gen(ifStmt->mCondition);

    if(ifStmt->mElseStmt == nullptr) {
      BasicBlock* thenBB = BasicBlock::Create(getGlobalContext(), "if.then", mCurrentFunction);
      BasicBlock* mergeBB = BasicBlock::Create(getGlobalContext(), "if.end");

      mILGen->mIRBuilder.CreateCondBr(getValue(lcondition), thenBB, mergeBB);

      mILGen->mIRBuilder.SetInsertPoint(thenBB);
      ifStmt->mThenStmt->accept(this);
      thenBB = mILGen->mIRBuilder.GetInsertBlock();
      bool mergeNeeded = false;
      if(thenBB->getTerminator() == nullptr || !isa<ReturnInst>(thenBB->getTerminator())) {
        mILGen->mIRBuilder.CreateBr(mergeBB);
        mergeNeeded = true;
      }

      if(mergeNeeded) {
        mCurrentFunction->getBasicBlockList().push_back(mergeBB);
        mILGen->mIRBuilder.SetInsertPoint(mergeBB);
      }

    } else {
      BasicBlock* thenBB = BasicBlock::Create(getGlobalContext(), "if.then", mCurrentFunction);
      BasicBlock* elseBB = BasicBlock::Create(getGlobalContext(), "if.else");
      BasicBlock* mergeBB = BasicBlock::Create(getGlobalContext(), "if.end");

      mILGen->mIRBuilder.CreateCondBr(getValue(lcondition), thenBB, elseBB);

      mILGen->mIRBuilder.SetInsertPoint(thenBB);
      ifStmt->mThenStmt->accept(this);
      thenBB = mILGen->mIRBuilder.GetInsertBlock();
      bool mergeNeeded = false;
      if(thenBB->getTerminator() == nullptr || !isa<ReturnInst>(thenBB->getTerminator())) {
        mILGen->mIRBuilder.CreateBr(mergeBB);
        mergeNeeded = true;
      }

      mCurrentFunction->getBasicBlockList().push_back(elseBB);
      mILGen->mIRBuilder.SetInsertPoint(elseBB);
      ifStmt->mElseStmt->accept(this);
      elseBB = mILGen->mIRBuilder.GetInsertBlock();

      if(elseBB->getTerminator() == nullptr || !isa<ReturnInst>(elseBB->getTerminator())) {
        mILGen->mIRBuilder.CreateBr(mergeBB);
        mergeNeeded = true;
      }

      if(mergeNeeded) {
        mCurrentFunction->getBasicBlockList().push_back(mergeBB);
        mILGen->mIRBuilder.SetInsertPoint(mergeBB);
      }
    }



  }

  void visit(Return* returnStmt) {
    Location* expr = gen(returnStmt->mExpr);
    mILGen->mIRBuilder.CreateRet(getValue(expr));
  }

  void visit(NBlock* block) {
    push();

    BasicBlock* basicBlock = BasicBlock::Create(getGlobalContext());
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);

    visitChildren(block);

    pop();

    set(block, new LLVMValue(basicBlock));
  }

  void visit(NLocalVar* localVar) {
    llvm::Type* type = llvm::IntegerType::getInt32Ty(getGlobalContext());

    AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type, 0);
    mScope->defineSymbol(localVar->mName, new LLVMAddress(alloc));
  }

  void visit(NSymbolRef* symbolRef) {
    Location* l = mScope->lookup(symbolRef->mName);
    set(symbolRef, l);
  }

  void visit(NArrayRef* arrayRef) {
    Location* base = gen(arrayRef->mBase);
    Location* index = gen(arrayRef->mIndex);

    llvm::Value* value = mILGen->mIRBuilder.CreateGEP(
      base->getValue(), getValue(index));

    set(arrayRef, new LLVMValue(value));
  }

  virtual void visit(NIntLiteral* lit) {
    llvm::Value* value = mILGen->mIRBuilder.getInt(APInt(32, lit->mValue, true));
    set(lit, new LLVMValue(value));
  }

  llvm::Value* getValue(Location* l) {
    return l->isAddress() ? mILGen->mIRBuilder.CreateLoad(l->getValue()) : l->getValue();
  }

  virtual void visit(NOperation* op) {
    Location* lleft = gen(op->mLeft);
    Location* lright = gen(op->mRight);

    llvm::Value* lvalue = getValue(lleft);
    llvm::Value* rvalue = getValue(lright);

    Location* result;

    switch(op->mOp) {
    case NOperation::Type::ADD:
      result = new LLVMValue(mILGen->mIRBuilder.CreateAdd(lvalue, rvalue));
      break;

    case NOperation::Type::SUB:
      result = new LLVMValue(mILGen->mIRBuilder.CreateSub(lvalue, rvalue));
      break;

    case NOperation::Type::CMPEQ:
      result = new LLVMValue(mILGen->mIRBuilder.CreateICmpEQ(lvalue, rvalue));
      break;
    }

    set(op, result);
  }

  virtual void visit(Assign* assign) {
    Location* lright = gen(assign->mRight);
    Location* lleft = gen(assign->mLeft);

    mILGen->mIRBuilder.CreateStore(getValue(lright), lleft->getValue());
  }

  void visit(NCall* call) {

    llvm::Function* func = mILGen->mModule.getFunction(call->mName);
    std::vector<llvm::Value*> args;
    for(Expr* argExp : call->mArgList) {
      Location* argLoc = gen(argExp);
      args.push_back(getValue(argLoc));
    }

    Location* result = new LLVMValue(mILGen->mIRBuilder.CreateCall(func, args));
    set(call, result);

  }

  void visit(NFunction* function) {

    std::vector<llvm::Type*> argTypes;

    for(NParam* param : *function->mParams) {
      //TODO: replace this with actual types not just ints
      argTypes.push_back(llvm::IntegerType::getInt32Ty(getGlobalContext()));
    }

    FunctionType* ftype = FunctionType::get(llvm::IntegerType::getInt32Ty(getGlobalContext()), argTypes,
                                            false);

    mCurrentFunction = Function::Create(ftype,
                                        Function::LinkageTypes::ExternalLinkage,
                                        function->mName, &mILGen->mModule);

    if(function->mStmts != NULL) {
      push();

      BasicBlock* basicBlock = BasicBlock::Create(getGlobalContext(), "", mCurrentFunction);
      mILGen->mIRBuilder.SetInsertPoint(basicBlock);

      Function::arg_iterator AI = mCurrentFunction->arg_begin();
      const size_t numArgs = function->mParams->size();
      for(size_t i=0; i<numArgs; i++,++AI) {
        NParam* param = function->mParams->at(i);
        llvm::Type* type = AI->getType();
        AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type, 0);
        mILGen->mIRBuilder.CreateStore(AI, alloc);
        mScope->defineSymbol(param->mName, new LLVMAddress(alloc));
      }


      for(NStmt* stmt : *function->mStmts) {
        stmt->accept(this);
      }
      pop();
    }



  }

};

ILGenerator::ILGenerator(CompilerContext* ctx)
  : mCtx(ctx),
    mIRBuilder(getGlobalContext()),
    mModule(ctx->inputFile.getAbsolutePath().c_str(), getGlobalContext()) {
  if(ctx->generateDebugSymobols) {
    mModule.addModuleFlag(llvm::Module::Warning, "Dwarf Version", 4);
    mModule.addModuleFlag(llvm::Module::Error, "Debug Info Version", llvm::DEBUG_METADATA_VERSION);
    mDIBuider = new DIBuilder(mModule);
  }
}

class ForwardDeclareMethodVisitor : public Visitor {
private:
  ILGenerator* mILGen;

public:
  using Visitor::visit;

  ForwardDeclareMethodVisitor(ILGenerator* ir)
    : mILGen(ir) {}

  void visit(NFunctionDecl* funDecl) {
    std::vector<llvm::Type*> argTypes;

    for(NParam* param : *funDecl->mParams) {
      //TODO: replace this with actual types not just ints
      argTypes.push_back(llvm::IntegerType::getInt32Ty(getGlobalContext()));
    }

    FunctionType* ftype = FunctionType::get(llvm::IntegerType::getInt32Ty(getGlobalContext()), argTypes,
                                            false);

    Function::Create(ftype,
                     Function::LinkageTypes::ExternalLinkage,
                     funDecl->mName, &mILGen->mModule);

  }
};

void ILGenerator::generate() {
  ForwardDeclareMethodVisitor fdv(this);
  mCtx->rootNode->accept(&fdv);

  ILGenVisitor visitor(this);
  mCtx->rootNode->accept(&visitor);

  if(mCtx->generateDebugSymobols) {
    mDIBuider->finalize();
  }

  /*
      std::vector<llvm::Type*> argTypes;
      argTypes.push_back(llvm::IntegerType::getInt32Ty(getGlobalContext()));

      FunctionType* ftype = FunctionType::get(mILGen->mIRBuilder.getVoidTy(), argTypes, false);
      Function* blah = Function::Create(ftype, Function::LinkageTypes::ExternalLinkage, "main", &mILGen->mModule);
  */

  mModule.dump();
}

} // namespace staple
