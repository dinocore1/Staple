

#ifndef STAPLE_LLVMCODEGENERATOR_H
#define STAPLE_LLVMCODEGENERATOR_H

#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/Module.h>
#include <llvm/PassManager.h>
#include <llvm/IR/DIBuilder.h>
#include "../node.h"
#include "../types/stapletype.h"

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
    friend class LLVMFunctionForwardDeclVisitor;
    friend class LLVMDebugInfo;
    private:
        CompilerContext* mCompilerContext;
        IRBuilder<> mIRBuilder;
        DIBuilder* mDIBuider;
        Module mModule;
        FunctionPassManager mFunctionPassManager;

    public:
        LLVMCodeGenerator(CompilerContext* compilerContext);

        void generateCode(NCompileUnit* compileUnit);

        Module* getModule() {
            return &mModule;
        }

        Type* getLLVMType(StapleType* stapleType);
        Function* getMallocFunction();
        Function* getFreeFunction();

        string createNamespaceSymbolName(const string &name);
        static string createClassSymbolName(const StapleClass* stapleClass);

    };


} // namespace staple

#endif //STAPLE_LLVMCODEGENERATOR_H
