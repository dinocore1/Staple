
#ifndef STAPLE_ILGENERATOR_H
#define STAPLE_ILGENERATOR_H

#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
//#include <llvm/PassManager.h>
#include <llvm/IR/DIBuilder.h>

namespace staple {

class ILGenerator {
public:

  ILGenerator(CompilerContext*);
  void generate();

  llvm::Type* getLLVMType(Node*);
  llvm::Type* getLLVMType(Type*);

  CompilerContext* mCtx;
  llvm::LLVMContext mLLVMCtx;
  llvm::IRBuilder<> mIRBuilder;
  llvm::DIBuilder* mDIBuider;
  llvm::Module mModule;

  std::map<Type*, llvm::Type*> mTypeCache;
};

}

#endif //STAPLE_ILGENERATOR_H
