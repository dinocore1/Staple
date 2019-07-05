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

  case Type::Float: {
    FloatType* floatType = cast<FloatType>(t);
    if(floatType->mWidth == 32) {
      return llvm::Type::getFloatTy(mLLVMCtx);
    } else {
      return llvm::Type::getDoubleTy(mLLVMCtx);
    }
  }


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

llvm::DIType* ILGenerator::getLLVMDebugType(Node* n) {
  return getLLVMDebugType(mCtx->mTypeTable[n]);
}

llvm::DIType* ILGenerator::getLLVMDebugType(Type* t) {
  llvm::DIType* retval = nullptr;

  switch(t->mTypeId) {
  case Type::Integer: {
    //static llvm::DIType* debugIntType = nullptr;
    //if(debugIntType == nullptr) {
      IntegerType* intType = cast<IntegerType>(t);
      retval = mDIBuider->createBasicType("int", intType->mWidth, dwarf::DW_ATE_signed);
    //}
    //retval = debugIntType;
    
  } break;

  case Type::Pointer:
    PointerType* ptrType = cast<PointerType>(t);
    retval = mDIBuider->createPointerType(getLLVMDebugType(ptrType->mBase), 32);
    break;
  }

  return retval;
}

llvm::Function* ILGenerator::getClassDestructorFunction(ClassType* classType) {
  auto it = mDestructorFunctionCache.find(classType);
  if(it != mDestructorFunctionCache.end()) {
    return it->second;
  }

  llvm::StructType* structType = cast<StructType>(getLLVMType(classType));
  llvm::Type* arg = llvm::PointerType::get(structType, 0);
  llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::Type::getVoidTy(mLLVMCtx), arg, false);
  llvm::Function* retval = llvm::Function::Create(ftype,
                           Function::LinkageTypes::ExternalLinkage,
                           classType->mFQName.getFullString() + "_kill", &mModule);

  mDestructorFunctionCache[classType] = retval;
  return retval;
}

class Cleanup {
public:
  Cleanup(llvm::Value* l, llvm::Function* f)
    : location(l), destructor(f) {}

  llvm::Function* destructor;
  llvm::Value* location;
};

class Scope {
public:
  Scope* mParent;

  Scope(Scope* scope)
    : mParent(scope) { }

  ~Scope() {}


  void defineSymbol(const std::string& symbolName, llvm::Value* l,
                    llvm::Function* destructor = nullptr) {
    symbolTable[symbolName] = l;
    managedLocations.insert(l);

    if(destructor != nullptr) {
      localCleanup.push_back(Cleanup(l, destructor));
    }
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

  void destroyLocals(ILGenerator* ilgen) {
    for(const Cleanup& var : localCleanup) {
      ilgen->mIRBuilder.CreateCall(var.destructor, var.location);
    }
  }

  map<Node*, llvm::Value*> locationTable;
  map<const std::string, llvm::Value*> symbolTable;
  set<llvm::Value*> managedLocations;
  vector<Cleanup> localCleanup;
  llvm::DIScope* debugCtx;


};

struct DebugInfo {
  llvm::DICompileUnit* compileUnit;
  llvm::DIFile* file;
};

class ILGenVisitor : public Visitor {
public:
  using Visitor::visit;
  Scope* mScope;
  ILGenerator* mILGen;
  llvm::Function* mCurrentFunction;
  llvm::Value* mCurrentFunctionReturnValueAddress;

  DebugInfo mDebugInfo;

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

  void emitDebugLocation(Node* n) {
    mILGen->mIRBuilder.SetCurrentDebugLocation(llvm::DebugLoc::get(n->location.first_line,
        n->location.first_column, mScope->debugCtx));
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
    mScope->destroyLocals(mILGen);
    pop();

    return basicBlock;
  }

  void visit(NCompileUnit* compileUnit) {

    if(mILGen->mDIBuider) {
      mDebugInfo.file = mILGen->mDIBuider->createFile(mILGen->mCtx->inputFile.getPath().c_str(), ".");
      mDebugInfo.compileUnit = mILGen->mDIBuider->createCompileUnit(dwarf::DW_LANG_C, mDebugInfo.file,
                               "Staple Compiler", 0, "", 0);
      mScope->debugCtx = mDebugInfo.file;
    }


    visitChildren(compileUnit);
  }

  void genifelse(NIfStmt* ifStmt) {
    BasicBlock* thenBB = BasicBlock::Create(mILGen->mLLVMCtx, "then", mCurrentFunction);
    BasicBlock* elseBB = BasicBlock::Create(mILGen->mLLVMCtx, "else");
    BasicBlock* endBB = BasicBlock::Create(mILGen->mLLVMCtx, "ifcont");

    llvm::Value* lcondition = gen(ifStmt->mCondition);
    mILGen->mIRBuilder.CreateCondBr(lcondition, thenBB, elseBB);

    //Codegen Then block
    mILGen->mIRBuilder.SetInsertPoint(thenBB);
    ifStmt->mThenStmt->accept(this);
    thenBB = mILGen->mIRBuilder.GetInsertBlock();
    if(thenBB->getTerminator() == nullptr) {
      mILGen->mIRBuilder.CreateBr(endBB);
    }

    //Codegen Else block
    mCurrentFunction->getBasicBlockList().push_back(elseBB);
    mILGen->mIRBuilder.SetInsertPoint(elseBB);
    if(ifStmt->mElseStmt != nullptr) {
      ifStmt->mElseStmt->accept(this);
    }
    elseBB = mILGen->mIRBuilder.GetInsertBlock();
    if(elseBB->getTerminator() == nullptr) {
      mILGen->mIRBuilder.CreateBr(endBB);
    }


    mCurrentFunction->getBasicBlockList().push_back(endBB);
    mILGen->mIRBuilder.SetInsertPoint(endBB);
  }

  void genif(NIfStmt* ifStmt) {
    BasicBlock* thenBB = BasicBlock::Create(mILGen->mLLVMCtx, "then", mCurrentFunction);
    BasicBlock* endBB = BasicBlock::Create(mILGen->mLLVMCtx, "ifcont");

    llvm::Value* lcondition = gen(ifStmt->mCondition);
    mILGen->mIRBuilder.CreateCondBr(lcondition, thenBB, endBB);

    //Codegen Then block
    mILGen->mIRBuilder.SetInsertPoint(thenBB);
    ifStmt->mThenStmt->accept(this);
    thenBB = mILGen->mIRBuilder.GetInsertBlock();
    if(thenBB->getTerminator() == nullptr) {
      mILGen->mIRBuilder.CreateBr(endBB);
    }

    mCurrentFunction->getBasicBlockList().push_back(endBB);
    mILGen->mIRBuilder.SetInsertPoint(endBB);
  }

  void visit(NIfStmt* ifStmt) {
    if(ifStmt->mElseStmt != nullptr) {
      genifelse(ifStmt);
    } else {
      genif(ifStmt);
    }

  }

  void visit(NReturn* returnStmt) override {

    llvm::Value* expr = gen(returnStmt->mExpr);

    mScope->destroyLocals(mILGen);

    //mILGen->mIRBuilder.CreateStore(expr, mCurrentFunctionReturnValueAddress);
    mILGen->mIRBuilder.CreateRet(expr);

    //mILGen->mIRBuilder.CreateBr(mCurrentFunctionReturnBB);
    //mILGen->mIRBuilder.CreateRet(getValue(expr));
  }

  void visit(NBlock* n) override {

    llvm::DILexicalBlock* newDebugScope;
    if(mILGen->mCtx->generateDebugSymobols) {
      newDebugScope = mILGen->mDIBuider->createLexicalBlock(mScope->debugCtx, mDebugInfo.file,
                      n->location.first_line, n->location.first_column);
    }


    push();

    if(mILGen->mCtx->generateDebugSymobols) {
      mScope->debugCtx = newDebugScope;
    }

    //BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx);
    //mILGen->mIRBuilder.SetInsertPoint(basicBlock);

    visitChildren(n);

    if(mILGen->mIRBuilder.GetInsertBlock()->getTerminator() == nullptr) {
      mScope->destroyLocals(mILGen);
    }
    pop();

    //set(n, basicBlock);
  }

  void visit(NLocalVar* localVar) {
    emitDebugLocation(localVar);
    llvm::Function* destructor = nullptr;

    Type* type = mILGen->mCtx->mTypeTable[localVar->mType];
    llvm::Type* llvmType = mILGen->getLLVMType(type);
    AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(llvmType);


    if(mILGen->mCtx->generateDebugSymobols) {
      llvm::DILocalVariable* debugLocalVar = mILGen->mDIBuider->createAutoVariable(mScope->debugCtx,
                                             localVar->mName, mDebugInfo.file,
                                             localVar->location.first_line, mILGen->getLLVMDebugType(type), true);

      mILGen->mDIBuider->insertDeclare(alloc, debugLocalVar, mILGen->mDIBuider->createExpression(),
                                       llvm::DebugLoc::get(localVar->location.first_line,
                                           localVar->location.first_column,
                                           mScope->debugCtx),
                                       mILGen->mIRBuilder.GetInsertBlock());
    }

    if(type->mTypeId == Type::Object) {
      ClassType* classType = cast<ClassType>(type);
      destructor = mILGen->getClassDestructorFunction(classType);
    }

    mScope->defineSymbol(localVar->mName, alloc, destructor);

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

  virtual void visit(NAssign* assign) {
    llvm::Value* lright = gen(assign->mRight);
    llvm::Value* lleft = gen(assign->mLeft);

    mILGen->mIRBuilder.CreateStore(lright, lleft);
  }

  void visit(NCall* call) {

    llvm::Function* func = mILGen->mModule.getFunction(call->mName);
    std::vector<llvm::Value*> args;
    for(NExpr* argExp : call->mArgList) {
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

    llvm::DISubprogram* subprogram;
    std::vector<llvm::Type*> argTypes;
    SmallVector<llvm::Metadata*, 8> debugArgTypes;
    //std::vector<llvm::DIType*> debugArgTypes;

    for(NParam* param : function->mParams) {
      argTypes.push_back(mILGen->getLLVMType(param->mType));

      if(mILGen->mCtx->generateDebugSymobols) {
        debugArgTypes.push_back(mILGen->getLLVMDebugType(param->mType));
      }

    }

    llvm::Type* returnType = mILGen->getLLVMType(function->mReturnType);

    llvm::FunctionType* ftype = llvm::FunctionType::get(returnType, argTypes, false);

    mCurrentFunction = Function::Create(ftype,
                                        Function::LinkageTypes::ExternalLinkage,
                                        function->mName, &mILGen->mModule);

    if(mILGen->mCtx->generateDebugSymobols) {
      llvm::DISubroutineType* subType = mILGen->mDIBuider->createSubroutineType(
                                          mILGen->mDIBuider->getOrCreateTypeArray(debugArgTypes));
      subprogram = mILGen->mDIBuider->createFunction(mScope->debugCtx, function->mName, llvm::StringRef(),
                   mDebugInfo.file, function->location.first_line, subType, function->location.first_line,
                   DINode::FlagPrototyped, DISubprogram::SPFlagDefinition);
      mCurrentFunction->setSubprogram(subprogram);
    }

    if(function->mStmts != NULL) {
      push();

      if(mILGen->mCtx->generateDebugSymobols) {
        mScope->debugCtx = subprogram;
      }

      BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx, "", mCurrentFunction);
      mILGen->mIRBuilder.SetInsertPoint(basicBlock);

      //preamble
      if(function->mReturnType) {
        //AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(returnType);
        //mCurrentFunctionReturnValueAddress = alloc;
      }

      Function::arg_iterator AI = mCurrentFunction->arg_begin();
      const size_t numArgs = function->mParams.size();
      for(size_t i=0; i<numArgs; i++,++AI) {
        llvm::Type* type = argTypes[i];
        AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type);
        mILGen->mIRBuilder.CreateStore(&*AI, alloc);
        mScope->defineSymbol(function->mParams.at(i)->mName, alloc);

        if(mILGen->mCtx->generateDebugSymobols) {
          llvm::DILocalVariable* localVar = mILGen->mDIBuider->createParameterVariable(mScope->debugCtx,
                                            function->mParams.at(i)->mName, i, mDebugInfo.file,
                                            function->mParams.at(i)->location.first_line, (llvm::DIType*)debugArgTypes[i], true);

          mILGen->mDIBuider->insertDeclare(alloc, localVar, mILGen->mDIBuider->createExpression(),
                                           llvm::DebugLoc::get(function->mParams.at(i)->location.first_line,
                                               function->mParams.at(i)->location.first_column,
                                               subprogram),
                                           mILGen->mIRBuilder.GetInsertBlock());
        }
      }

      //mCurrentFunctionReturnBB = BasicBlock::Create(mILGen->mLLVMCtx);

      for(NStmt* stmt : *function->mStmts) {
        stmt->accept(this);
      }



      basicBlock = mILGen->mIRBuilder.GetInsertBlock();
      if(basicBlock->getTerminator() == nullptr) {
        mScope->destroyLocals(mILGen);
        //mILGen->mIRBuilder.CreateBr(mCurrentFunctionReturnBB);
        mILGen->mIRBuilder.CreateRetVoid();
      }

      //postamble
      //mCurrentFunction->getBasicBlockList().push_back(mCurrentFunctionReturnBB);
      //mILGen->mIRBuilder.SetInsertPoint(mCurrentFunctionReturnBB);
      //LoadInst* returnValue = mILGen->mIRBuilder.CreateLoad(mCurrentFunctionReturnValueAddress);
      //mILGen->mIRBuilder.CreateRet(returnValue);


      pop();
    }
  }
};

ILGenerator::ILGenerator(CompilerContext* ctx)
  : mCtx(ctx),
    mLLVMCtx(),
    mIRBuilder(mLLVMCtx),
    mModule(ctx->inputFile.getAbsolutePath().c_str(), mLLVMCtx),
    mDIBuider(nullptr) {
  if(ctx->generateDebugSymobols) {
    mModule.addModuleFlag(llvm::Module::Warning, "Dwarf Version", 4);
    mModule.addModuleFlag(llvm::Module::Error, "Debug Info Version", llvm::DEBUG_METADATA_VERSION);
    mDIBuider = new DIBuilder(mModule);
  }
}

class StructVisitor : public Visitor {
public:
  StructVisitor(ILGenerator* ig)
    : mILGen(ig), mCompileUnit(nullptr), mCurrentPackage(nullptr)
  {}

  void visit(NCompileUnit* n) {
    if(mCompileUnit == nullptr) {
      mCompileUnit = n;
    }
    FQPath* oldPackage = mCurrentPackage;
    mCurrentPackage = &n->mPackage;
    visitChildren(n);
    mCurrentPackage = oldPackage;
  }

  void visit(NImport* n) {

  }

  void visit(NClassDecl* n) {
    bool isLocalClass = mCompileUnit->mPackage == *mCurrentPackage;

    ClassType* classType = cast<ClassType>(mILGen->mCtx->mTypeTable[n]);
    llvm::StructType* structType = dyn_cast<llvm::StructType>(mILGen->getLLVMType(classType));
    structType->setName(classType->mFQName.getFullString());

    llvm::Function* destructor = mILGen->getClassDestructorFunction(classType);
    if(isLocalClass) {
      BasicBlock* basicBlock = BasicBlock::Create(mILGen->mLLVMCtx, "", destructor);
      mILGen->mIRBuilder.SetInsertPoint(basicBlock);
      mILGen->mIRBuilder.CreateRetVoid();
    }
  }

private:
  ILGenerator* mILGen;
  NCompileUnit* mCompileUnit;
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

    llvm::FunctionType* ftype = llvm::FunctionType::get(mILGen->getLLVMType(funDecl->mReturnType),
                                argTypes,
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

  std::error_code err;
  llvm::raw_fd_ostream outstream(mCtx->outputFile, err, llvm::sys::fs::F_None);
  mModule.print(outstream, NULL);
  outstream.flush();


  //mModule.dump();

}

} // namespace staple
