

#ifndef STAPLE_LLVMCODEGENERATOR_H
#define STAPLE_LLVMCODEGENERATOR_H

#include "../compilercontext.h"

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/PassManager.h>

namespace staple {

    using namespace llvm;

    class LLVMCodeGenerator {
    private:
        const CompilerContext* mCompilerContext;
        IRBuilder<> mIRBuilder;
        Module mModule;
        FunctionPassManager mFunctionPassManager;

    public:
        LLVMCodeGenerator(const CompilerContext* compilerContext);

        void generateCode(NCompileUnit* compileUnit);

        Module* getModule() {
            return &mModule;
        }
    };


} // namespace staple

#endif //STAPLE_LLVMCODEGENERATOR_H
