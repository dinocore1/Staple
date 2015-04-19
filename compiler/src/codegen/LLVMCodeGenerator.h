

#ifndef STAPLE_LLVMCODEGENERATOR_H
#define STAPLE_LLVMCODEGENERATOR_H

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/PassManager.h>
#include "../node.h"

#include <string>

namespace staple {

    using namespace std;
    using namespace llvm;

    class CompilerContext;
    class NCompileUnit;
    class StapleType;
    class StapleClass;

    class LLVMCodeGenerator {
    friend class LLVMCodeGenVisitor;
    friend class LLVMStapleObject;
    friend class LLVMBaseObject;
    private:
        CompilerContext* mCompilerContext;
        IRBuilder<> mIRBuilder;
        Module mModule;
        FunctionPassManager mFunctionPassManager;


        string createFunctionName(const string& name);

    public:
        LLVMCodeGenerator(CompilerContext* compilerContext);

        void generateCode(NCompileUnit* compileUnit);

        Module* getModule() {
            return &mModule;
        }

        Type* getLLVMType(StapleType* stapleType);
    };


} // namespace staple

#endif //STAPLE_LLVMCODEGENERATOR_H
