#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/PassManager.h"
#include "llvm/Transforms/Scalar.h"
#include <stack>

using namespace llvm;

class NCompileUnit;
class NClassDeclaration;

class CodeGenBlock {
public:
    CodeGenBlock* parent;
    BasicBlock *block;
    std::map<std::string, Value*> locals;

    CodeGenBlock(CodeGenBlock* parent, BasicBlock* block)
    : parent(parent), block(block) {}

    Value* getSymbol(const std::string& name) {
        Value* retval = NULL;

        std::map<std::string, Value*>::iterator it;
        if((it = locals.find(name)) != locals.end()) {
            retval = it->second;
        } else if(parent != NULL) {
            retval = parent->getSymbol(name);
        }

        return retval;
    }
};

class CodeGenContext {
    CodeGenBlock* top;
    NCompileUnit* compileUnitRoot;

public:
    Module *module;
    FunctionPassManager *fpm;
    IRBuilder<> Builder;
    CodeGenContext(const char* moduleName)
    : top(NULL), Builder(getGlobalContext())
    {
        module = new Module(moduleName, getGlobalContext());
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
    Value* getSymbol(const std::string& name) {
        return top->getSymbol(name);
    }
    void defineSymbol(const std::string& name, Value* value) {
        top->locals[name] = value;
    }
    NClassDeclaration* getClass(const std::string& name) const;
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