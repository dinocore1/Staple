#include "stdafx.h"

#include <map>
#include <set>
#include <memory>

using namespace std;

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/BasicBlock.h>
#include <llvm/Support/raw_ostream.h>
#include <llvm/Support/FileSystem.h>

using namespace llvm;

namespace staple {

llvm::Type* ILGenerator::getLLVMType(Node* n) {
  return getLLVMType(mCtx->mTypeTable[n]);
}

llvm::Type* ILGenerator::getLLVMType(Type* t) {
  switch(t->mTypeId) {
  case Type::Void:
    return llvm::Type::getVoidTy(mLLVMCtx);

  case Type::Bool:
    return llvm::Type::getInt1Ty(mLLVMCtx);

  case Type::Integer: {
    IntegerType* intType = cast<IntegerType>(t);
    return llvm::IntegerType::get(mLLVMCtx, intType->mWidth);
  }

  case Type::Float:
    return llvm::Type::getFloatTy(mLLVMCtx);

  case Type::Pointer: {
    PointerType* ptrType = cast<PointerType>(t);
    return llvm::PointerType::get(getLLVMType(ptrType->mBase), 0);
  }

  case Type::Object: {
    auto it = mTypeCache.find(t);
    if(it != mTypeCache.end()) {
      return it->second;
    }

    ClassType* classType = cast<ClassType>(t);

    std::vector<llvm::Type*> body;
    for(auto it = classType->mFields.begin(); it != classType->mFields.end(); it++) {
      body.push_back(getLLVMType(it->second));
    }

    llvm::StructType* structType = llvm::StructType::create(mLLVMCtx);
    structType->setBody(body);

    mTypeCache[t] = structType;
    return structType;
  }

  }
}

class Scope {
public:
  Scope* mParent;

  Scope(Scope* scope)
    : mParent(scope) { }

  ~Scope() {
  }


  void defineSymbol(const std::string& symbolName, llvm::Value* l) {
    symbolTable[symbolName] = l;
    managedLocations.insert(l);
  }

  llvm::Value* lookup(const std::string& symbolName) {
    auto it = symbolTable.find(symbolName);
    if(it != symbolTable.end()) {
      return (*it).second;
    }

    if(mParent != nullptr) {
      return mParent->lookup(symbolName);
    }

    return nullptr;
  }

  map<Node*, llvm::Value*> locationTable;
  map<const std::string, llvm::Value*> symbolTable;
  set<llvm::Value*> managedLocations;


};

class ILGenVisitor : public Visitor {
public:
  using Visitor::visit;
  Scope* mScope;
  ILGenerator* mILGen;
  llvm::Function* mCurrentFunction;
  llvm::Value* mCurrentFunctionReturnValueAddress;
  llvm::BasicBlock* mCurrentFunctionReturnBB;

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

  void set(Node* n, llvm::Value* l) {
    mScope->locationTable[n] = l;
    mScope->managedLocations.insert(l);
  }

  llvm::Value* gen(Node* n) {
    n->accept(this);
    return mScope->locationTable[n];
  }

  BasicBlock* getBasicBlock(NBlock* block) {
    IRBuilder<>::InsertPointGuard guard(mILGen->mIRBuilder);

    push();
    BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx);
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);
    visitChildren(block);
    pop();

    return basicBlock;
  }

  void visit(NCompileUnit* compileUnit) {
    visitChildren(compileUnit);
  }

  void visit(NClass* classDecl) {

  }

  void visit(NIfStmt* ifStmt) {
    BasicBlock* thenBB = BasicBlock::Create(mILGen->mLLVMCtx, "", mCurrentFunction);
    BasicBlock* elseBB = BasicBlock::Create(mILGen->mLLVMCtx);
    BasicBlock* endBB = BasicBlock::Create(mILGen->mLLVMCtx);
    bool needsEndBlock = false;

    llvm::Value* lcondition = gen(ifStmt->mCondition);
    mILGen->mIRBuilder.CreateCondBr(lcondition, thenBB, elseBB);

    //Codegen Then block
    mILGen->mIRBuilder.SetInsertPoint(thenBB);
    ifStmt->mThenStmt->accept(this);
    //thenBB = mILGen->mIRBuilder.GetInsertBlock();
    if(thenBB->getTerminator() == nullptr) {
      mILGen->mIRBuilder.CreateBr(endBB);
      needsEndBlock = true;
    }

    //Codegen Else block
    mCurrentFunction->getBasicBlockList().push_back(elseBB);
    mILGen->mIRBuilder.SetInsertPoint(elseBB);
    if(ifStmt->mElseStmt != nullptr) {
      ifStmt->mElseStmt->accept(this);
      //elseBB = mILGen->mIRBuilder.GetInsertBlock();
    }

    if(elseBB->getTerminator() == nullptr) {
      mILGen->mIRBuilder.CreateBr(endBB);
      needsEndBlock = true;
    }

    if(needsEndBlock) {
      //Emit the merge block
      mCurrentFunction->getBasicBlockList().push_back(endBB);
      mILGen->mIRBuilder.SetInsertPoint(endBB);
    }

  }

  void visit(Return* returnStmt) {
    llvm::Value* expr = gen(returnStmt->mExpr);
    mILGen->mIRBuilder.CreateStore(expr, mCurrentFunctionReturnValueAddress);
    mILGen->mIRBuilder.CreateBr(mCurrentFunctionReturnBB);
    //mILGen->mIRBuilder.CreateRet(getValue(expr));
  }

  void visit(NBlock* block) {
    push();

    BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx);
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);

    visitChildren(block);

    pop();

    set(block, basicBlock);
  }

  void visit(NLocalVar* localVar) {
    llvm::Type* type = mILGen->getLLVMType(localVar->mType);

    AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type);
    mScope->defineSymbol(localVar->mName, alloc);

    if(localVar->mInitializer) {
      llvm::Value* vright = gen(localVar->mInitializer);
      mILGen->mIRBuilder.CreateStore(vright, alloc);
    }

  }

  void visit(NSymbolRef* symbolRef) {
    llvm::Value* l = mScope->lookup(symbolRef->mName);
    set(symbolRef, l);
  }

  void visit(NArrayRef* arrayRef) {
    llvm::Value* base = gen(arrayRef->mBase);
    llvm::Value* index = gen(arrayRef->mIndex);

    llvm::Value* value = mILGen->mIRBuilder.CreateGEP(
                           base, index);

    set(arrayRef, value);
  }

  void visit(NFieldRef* n) {
    llvm::Value* base = gen(n->mBase);

    llvm::Value* value = mILGen->mIRBuilder.CreateStructGEP(base, 0);
    set(n, value);
  }

  virtual void visit(NIntLiteral* lit) {
    llvm::Value* value = mILGen->mIRBuilder.getInt(APInt(32, lit->mValue, true));
    set(lit, value);
  }

  void visit(NStringLiteral* strLit) {
    StringRef value(strLit->mStr);

    llvm::ArrayType* arrayType = llvm::ArrayType::get(llvm::IntegerType::get(
                                   mILGen->mModule.getContext(), 8), value.size()+1);

    GlobalVariable* gvar_array_str = new GlobalVariable(mILGen->mModule, arrayType, true,
        GlobalValue::PrivateLinkage, 0, ".str");
    Constant* const_array = ConstantDataArray::getString(mILGen->mModule.getContext(), value, true);
    gvar_array_str->setInitializer(const_array);

    std::vector<Constant*> const_ptr_indices;
    const_ptr_indices.push_back(llvm::ConstantInt::get(mILGen->mModule.getContext(), APInt(32, 0,
                                false)));
    const_ptr_indices.push_back(llvm::ConstantInt::get(mILGen->mModule.getContext(), APInt(32, 0,
                                false)));

    Constant* const_ptr = ConstantExpr::getGetElementPtr(arrayType, gvar_array_str, const_ptr_indices);

    set(strLit, const_ptr);
  }

  virtual void visit(NOperation* op) {
    llvm::Value* lvalue = gen(op->mLeft);
    llvm::Value* rvalue = gen(op->mRight);

    llvm::Value* result;

    switch(op->mOp) {
    case NOperation::Type::ADD:
      result = mILGen->mIRBuilder.CreateAdd(lvalue, rvalue);
      break;

    case NOperation::Type::SUB:
      result = mILGen->mIRBuilder.CreateSub(lvalue, rvalue);
      break;

    case NOperation::Type::CMPEQ:
      result = mILGen->mIRBuilder.CreateICmpEQ(lvalue, rvalue);
      break;

    case NOperation::Type::CMPLT:
      result = mILGen->mIRBuilder.CreateICmpSLT(lvalue, rvalue);
      break;

    case NOperation::Type::CMPLE:
      result = mILGen->mIRBuilder.CreateICmpSLE(lvalue, rvalue);
      break;

    case NOperation::Type::CMPGT:
      result = mILGen->mIRBuilder.CreateICmpSGT(lvalue, rvalue);
      break;

    case NOperation::Type::CMPGE:
      result = mILGen->mIRBuilder.CreateICmpSGE(lvalue, rvalue);
      break;

    }

    set(op, result);
  }

  virtual void visit(Assign* assign) {
    llvm::Value* lright = gen(assign->mRight);
    llvm::Value* lleft = gen(assign->mLeft);

    mILGen->mIRBuilder.CreateStore(lright, lleft);
  }

  void visit(NCall* call) {

    llvm::Function* func = mILGen->mModule.getFunction(call->mName);
    std::vector<llvm::Value*> args;
    for(Expr* argExp : call->mArgList) {
      llvm::Value* argLoc = gen(argExp);
      args.push_back(argLoc);
    }

    llvm::Value* result = mILGen->mIRBuilder.CreateCall(func, args);
    set(call, result);

  }

  void visit(NLoad* load) {
    llvm::Value* l = gen(load->mExpr);
    set(load, mILGen->mIRBuilder.CreateLoad(l));
  }


  void visit(NFunctionDecl* function) {

    std::vector<llvm::Type*> argTypes;

    for(NParam* param : function->mParams) {
      argTypes.push_back(mILGen->getLLVMType(param->mType));
    }

    llvm::Type* returnType = mILGen->getLLVMType(function->mReturnType);

    llvm::FunctionType* ftype = llvm::FunctionType::get(returnType, argTypes, false);

    mCurrentFunction = Function::Create(ftype,
                                        Function::LinkageTypes::ExternalLinkage,
                                        function->mName, &mILGen->mModule);

    if(function->mStmts != NULL) {
      push();

      BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx, "", mCurrentFunction);
      mILGen->mIRBuilder.SetInsertPoint(basicBlock);

      //preamble
      if(function->mReturnType) {
        AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(returnType);
        mCurrentFunctionReturnValueAddress = alloc;
      }

      Function::arg_iterator AI = mCurrentFunction->arg_begin();
      const size_t numArgs = function->mParams.size();
      for(size_t i=0; i<numArgs; i++,++AI) {
        llvm::Type* type = argTypes[i];
        AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type);
        mILGen->mIRBuilder.CreateStore(&*AI, alloc);
        mScope->defineSymbol(function->mParams.at(i)->mName, alloc);
      }

      mCurrentFunctionReturnBB = BasicBlock::Create(mILGen->mLLVMCtx);

      for(NStmt* stmt : *function->mStmts) {
        stmt->accept(this);
      }

      if(basicBlock->getTerminator() == nullptr) {
        mILGen->mIRBuilder.CreateBr(mCurrentFunctionReturnBB);
      }

      //postamble
      mCurrentFunction->getBasicBlockList().push_back(mCurrentFunctionReturnBB);
      mILGen->mIRBuilder.SetInsertPoint(mCurrentFunctionReturnBB);
      LoadInst* returnValue = mILGen->mIRBuilder.CreateLoad(mCurrentFunctionReturnValueAddress);
      mILGen->mIRBuilder.CreateRet(returnValue);


      pop();
    }



  }

};

ILGenerator::ILGenerator(CompilerContext* ctx)
  : mCtx(ctx),
    mLLVMCtx(),
    mIRBuilder(mLLVMCtx),
    mModule(ctx->inputFile.getAbsolutePath().c_str(), mLLVMCtx) {
  if(ctx->generateDebugSymobols) {
    mModule.addModuleFlag(llvm::Module::Warning, "Dwarf Version", 4);
    mModule.addModuleFlag(llvm::Module::Error, "Debug Info Version", llvm::DEBUG_METADATA_VERSION);
    mDIBuider = new DIBuilder(mModule);
  }
}

class StructVisitor : public Visitor {
public:
  StructVisitor(ILGenerator* ig)
    : mILGen(ig), mCurrentPackage(nullptr)
  {}

  void visit(NCompileUnit* compileUnit) {
    FQPath* oldPackage = mCurrentPackage;
    mCurrentPackage = &compileUnit->mPackage;
    visitChildren(compileUnit);
    mCurrentPackage = oldPackage;
  }

  void visit(NClassDecl* n) {
    FQPath fqName = *mCurrentPackage;
    fqName.add(n->mName);

    llvm::StructType* structType = dyn_cast<llvm::StructType>(mILGen->getLLVMType(n));
    structType->setName(fqName.getFullString());
  }

private:
  ILGenerator* mILGen;
  FQPath* mCurrentPackage;
};

class ForwardDeclareMethodVisitor : public Visitor {
private:
  ILGenerator* mILGen;

public:
  using Visitor::visit;

  ForwardDeclareMethodVisitor(ILGenerator* ir)
    : mILGen(ir) {}

  void visit(NCompileUnit* compileUnit) {
    visitChildren(compileUnit);
  }

  void visit(NImport* n) {
    visitChildren(n);
  }

  void visit(NExternFunctionDecl* funDecl) {
    std::vector<llvm::Type*> argTypes;

    for(NParam* param : funDecl->mParams) {
      argTypes.push_back(mILGen->getLLVMType(param->mType));
    }

    llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::IntegerType::getInt32Ty(
                                  mILGen->mLLVMCtx), argTypes,
                                funDecl->mIsVarg);

    llvm::Function::Create(ftype,
                           Function::LinkageTypes::ExternalLinkage,
                           funDecl->mName, &mILGen->mModule);

  }
};

void ILGenerator::generate() {

  StructVisitor sv(this);
  mCtx->rootNode->accept(&sv);

  ForwardDeclareMethodVisitor fdv(this);
  mCtx->rootNode->accept(&fdv);

  ILGenVisitor visitor(this);
  mCtx->rootNode->accept(&visitor);

  if(mCtx->generateDebugSymobols) {
    mDIBuider->finalize();
  }

  /*
      std::vector<llvm::Type*> argTypes;
      argTypes.push_back(llvm::IntegerType::getInt32Ty(TheContext));

      FunctionType* ftype = FunctionType::get(mILGen->mIRBuilder.getVoidTy(), argTypes, false);
      Function* blah = Function::Create(ftype, Function::LinkageTypes::ExternalLinkage, "main", &mILGen->mModule);
  */

  std::error_code err;
  llvm::raw_fd_ostream outstream(mCtx->outputFile, err, llvm::sys::fs::F_None);
  mModule.print(outstream, NULL);
  outstream.flush();


  //mModule.dump();

}

} // namespace staple
