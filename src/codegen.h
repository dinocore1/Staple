#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include <stack>

using namespace llvm;

class NBlock;

class CodeGenBlock {
public:
    BasicBlock *block;
    std::map<std::string, Value*> locals;
};

class CodeGenContext {
    std::stack<CodeGenBlock *> blocks;
    Function *mainFunction;

public:
    Module *module;
    IRBuilder<> Builder;
    CodeGenContext()
    : Builder(getGlobalContext())
    {
        module = new Module("main", getGlobalContext());
    }
    
    void generateCode(NBlock& root);
    std::map<std::string, Value*>& locals() { return blocks.top()->locals; }
    BasicBlock *currentBlock() { return blocks.top()->block; }
    void pushBlock(BasicBlock *block)
    {
        blocks.push(new CodeGenBlock());
        blocks.top()->block = block;
        Builder.SetInsertPoint(block);
    }
    void popBlock()
    {
        CodeGenBlock *top = blocks.top();
        blocks.pop();
        delete top;
        if(!blocks.empty()) {
            Builder.SetInsertPoint(blocks.top()->block);
        }
    }
};