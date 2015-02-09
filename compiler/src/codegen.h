#ifndef CODEGEN_H_
#define CODEGEN_H_

#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/PassManager.h"
#include "llvm/Transforms/Scalar.h"
#include <stack>

#include "compilercontext.h"

using namespace llvm;

class NCompileUnit;
class NClassDeclaration;
class SymbolLookup;

class CodeGenBlock {
public:
    CodeGenBlock* parent;
    BasicBlock *block;
    std::map<std::string, SymbolLookup*> locals;

    CodeGenBlock(CodeGenBlock* parent, BasicBlock* block)
    : parent(parent), block(block) {}

    SymbolLookup* getSymbol(const std::string& name) {
        SymbolLookup* retval = NULL;

        std::map<std::string, SymbolLookup*>::iterator it;
        if((it = locals.find(name)) != locals.end()) {
            retval = it->second;
        } else if(parent != NULL) {
            retval = parent->getSymbol(name);
        }

        return retval;
    }
};

class ILClassType;
class ObjectHelper;

class CodeGenContext {
    CodeGenBlock* top;
    NCompileUnit* compileUnitRoot;

public:
    CompilerContext& ctx;
    Module *module;
    FunctionPassManager *fpm;
    IRBuilder<> Builder;
    std::map<SClassType*, ObjectHelper*> mClassObjMap;

    CodeGenContext(CompilerContext& ctx)
    : ctx(ctx), top(NULL), Builder(getGlobalContext())
    {
        module = new Module(ctx.inputFilename.c_str(), getGlobalContext());
        fpm = new FunctionPassManager(module);

        //fpm->add(new DataLayoutPass());
        //fpm->add(createBasicAliasAnalysisPass());
        //fpm->add(createInstructionCombiningPass());
        //fpm->add(createReassociatePass());
        //fpm->add(createGVNPass());
        //fpm->add(createCFGSimplificationPass());
        //fpm->add(createPromoteMemoryToRegisterPass());


        fpm->doInitialization();
    }

    virtual ~CodeGenContext() {
        fpm->doFinalization();
    }
    
    void generateCode(NCompileUnit& root);
    SymbolLookup* getSymbol(const std::string& name) {
        return top->getSymbol(name);
    }
    void defineSymbol(const std::string& name, SymbolLookup* value) {
        top->locals[name] = value;
    }

    void pushBlock(BasicBlock *block) {
        CodeGenBlock* newBlock = new CodeGenBlock(top, block);
        top = newBlock;
        //Builder.SetInsertPoint(block);
    }
    void popBlock() {
        if(top != NULL) {
            CodeGenBlock* lastTop = top;
            top = top->parent;
            delete lastTop;
            if(top != NULL) {
                //Builder.SetInsertPoint(top->block);
            }
        }
    }
};

#endif /* CODEGEN_H_ */