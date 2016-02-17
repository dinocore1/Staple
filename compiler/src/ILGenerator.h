
#ifndef STAPLE_ILGENERATOR_H
#define STAPLE_ILGENERATOR_H

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/PassManager.h>
#include <llvm/IR/DIBuilder.h>

namespace staple {

class ILGenerator {
public:

  ILGenerator(CompilerContext*);
  void generate();


  CompilerContext* mCtx;
  llvm::IRBuilder<> mIRBuilder;
  llvm::DIBuilder* mDIBuider;
  llvm::Module mModule;
};

}

#endif //STAPLE_ILGENERATOR_H
