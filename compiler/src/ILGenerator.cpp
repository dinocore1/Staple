#include "stdafx.h"

#include <map>
#include <set>
#include <memory>

using namespace std;

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/BasicBlock.h>
#include <llvm/Support/raw_ostream.h>
#include <llvm/Support/FileSystem.h>

using namespace llvm;

namespace staple {

static LLVMContext TheContext;

    
llvm::Type* getLLVMType(const NNamedType* n) {
    if(n->mTypeName.getNumParts() == 1) {
        std::string simpleName = n->mTypeName.getSimpleName();
        if(simpleName.compare("void") == 0){
            return llvm::Type::getVoidTy(TheContext);
        } else if(simpleName.compare("bool") == 0) {
            return llvm::Type::getInt1Ty(TheContext);
        } else if(simpleName.compare("i8") == 0) {
            return llvm::Type::getInt8Ty(TheContext);
        } else if(simpleName.compare("i16") == 0) {
            return llvm::Type::getInt16Ty(TheContext);
        } else if(simpleName.compare("i32") == 0) {
            return llvm::Type::getInt32Ty(TheContext);
        } else if(simpleName.compare("int") == 0) {
            return llvm::Type::getInt32Ty(TheContext);
        }
    } else {
        //todo: resolve struct types
    }
    
    return nullptr;
}

llvm::Type* getLLVMType(const NType* n) {
    if(isa<NNamedType>(n)) {
        const NNamedType* namedType = cast<NNamedType>(n);
        return getLLVMType(namedType);
        
    } else if(isa<NPointerType>(n)) {
        const NPointerType* npointerType = cast<NPointerType>(n);
        llvm::Type* baseType = getLLVMType(npointerType->mBase);
        return llvm::PointerType::get(baseType, 0);
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
    BasicBlock* basicBlock = BasicBlock::Create(TheContext);
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);
    visitChildren(block);
    pop();

    return basicBlock;
  }

  void visit(NCompileUnit* compileUnit) {
    visitChildren(compileUnit);
  }

  void visit(NIfStmt* ifStmt) {
    BasicBlock* thenBB = BasicBlock::Create(TheContext, "", mCurrentFunction);
    BasicBlock* elseBB = BasicBlock::Create(TheContext);
    BasicBlock* endBB = BasicBlock::Create(TheContext);
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

    BasicBlock* basicBlock = BasicBlock::Create(TheContext);
    mILGen->mIRBuilder.SetInsertPoint(basicBlock);

    visitChildren(block);

    pop();

    set(block, basicBlock);
  }

  void visit(NLocalVar* localVar) {
    llvm::Type* type = llvm::IntegerType::getInt32Ty(TheContext);

    AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type, 0);
    mScope->defineSymbol(localVar->mName, alloc);
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

  virtual void visit(NIntLiteral* lit) {
    llvm::Value* value = mILGen->mIRBuilder.getInt(APInt(32, lit->mValue, true));
    set(lit, value);
  }
  
  void visit(NStringLiteral* strLit) {
      StringRef value(strLit->mStr);
      
      llvm::ArrayType* arrayType = llvm::ArrayType::get(llvm::IntegerType::get(mILGen->mModule.getContext(), 8), value.size()+1);
      
      GlobalVariable* gvar_array_str = new GlobalVariable(mILGen->mModule, arrayType, true, GlobalValue::PrivateLinkage, 0, ".str");
      Constant* const_array = ConstantDataArray::getString(mILGen->mModule.getContext(), value, true);
      gvar_array_str->setInitializer(const_array);
      
      std::vector<Constant*> const_ptr_indices;
      const_ptr_indices.push_back(llvm::ConstantInt::get(mILGen->mModule.getContext(), APInt(32, 0, false)));
      const_ptr_indices.push_back(llvm::ConstantInt::get(mILGen->mModule.getContext(), APInt(32, 0, false)));
      
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


  void visit(NFunction* function) {

    std::vector<llvm::Type*> argTypes;

    for(NParam* param : function->mParams) {
      argTypes.push_back(getLLVMType(param->mType));
    }
    
    llvm::Type* returnType = getLLVMType(function->mReturnType);

    llvm::FunctionType* ftype = llvm::FunctionType::get(returnType, argTypes, false);

    mCurrentFunction = Function::Create(ftype,
                                        Function::LinkageTypes::ExternalLinkage,
                                        function->mName, &mILGen->mModule);

    if(function->mStmts != NULL) {
      push();

      BasicBlock* basicBlock = BasicBlock::Create(TheContext, "", mCurrentFunction);
      mILGen->mIRBuilder.SetInsertPoint(basicBlock);

      //preamble
      if(function->mReturnType) {
          AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(returnType, 0);
          mCurrentFunctionReturnValueAddress = alloc;
      }
      
      Function::arg_iterator AI = mCurrentFunction->arg_begin();
      const size_t numArgs = function->mParams.size();
      for(size_t i=0; i<numArgs; i++,++AI) {
        llvm::Type* type = argTypes[i];
        AllocaInst* alloc = mILGen->mIRBuilder.CreateAlloca(type, 0);
        mILGen->mIRBuilder.CreateStore(&*AI, alloc);
        mScope->defineSymbol(function->mParams.at(i)->mName, alloc);
      }

      mCurrentFunctionReturnBB = BasicBlock::Create(TheContext);

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
    mIRBuilder(TheContext),
    mModule(ctx->inputFile.getAbsolutePath().c_str(), TheContext) {
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

  void visit(NCompileUnit* compileUnit) {
      visitChildren(compileUnit);
  }
  
  void visit(NFunctionDecl* funDecl) {
    std::vector<llvm::Type*> argTypes;

    for(NParam* param : funDecl->mParams) {
      argTypes.push_back(getLLVMType(param->mType));
    }

    llvm::FunctionType* ftype = llvm::FunctionType::get(llvm::IntegerType::getInt32Ty(
                                  TheContext), argTypes,
                                funDecl->mIsVarg);

    llvm::Function::Create(ftype,
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
