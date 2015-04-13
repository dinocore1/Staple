

#ifndef STAPLE_LLVMCODEGENERATOR_H
#define STAPLE_LLVMCODEGENERATOR_H


#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/PassManager.h>
#include "../node.h"

namespace staple {

    using namespace llvm;

    class CompilerContext;
    class NCompileUnit;

    class LLVMCodeGenerator {
    friend class LLVMCodeGenVisitor;
    private:
        CompilerContext* mCompilerContext;
        IRBuilder<> mIRBuilder;
        Module mModule;
        FunctionPassManager mFunctionPassManager;

    public:
        LLVMCodeGenerator(CompilerContext* compilerContext);

        void generateCode(NCompileUnit* compileUnit);

        Module* getModule() {
            return &mModule;
        }
    };


} // namespace staple

#endif //STAPLE_LLVMCODEGENERATOR_H
