

#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"
#include "../types/stapletype.h"

#include <llvm/IR/GlobalValue.h>


namespace staple {

    using namespace std;

    class SymbolLookup {
    public:
        virtual Value* getValue(CodeGenContext& context) = 0;
    };

    class LocalVarLookup : public SymbolLookup {
    private:
        Value* value;

    public:

        static LocalVarLookup* get(Value* value) {
            LocalVarLookup* retval = new LocalVarLookup();
            retval->value = value;
            return retval;
        }

        virtual Value* getValue(CodeGenContext& context) {
            return value;
        }
    };

    class CodeGenBlock {
    private:
        CodeGenBlock* mParent;
        map<const string, SymbolLookup*> mSymbolTable;

    public:
        CodeGenBlock(CodeGenBlock* parent)
        : mParent(parent){}

        CodeGenBlock* getParent() const { return mParent; }

        void defineSymbol(const string& name, SymbolLookup* symbolLookup) {
            mSymbolTable[name] = symbolLookup;
        }


        BasicBlock* mBasicBlock;

    };

    class LLVMCodeGenVisitor : public ASTVisitor {
    private:
        LLVMCodeGenerator* mCodeGen;
        map<ASTNode*, Value*> mValues;
        CodeGenBlock* mScope;


        void push() {
            mScope = new CodeGenBlock(mScope);
        }

        void pop() {
            CodeGenBlock* parent = mScope->getParent();
            delete mScope;
            mScope = parent;
        }

        Type* getLLVMType(StapleType* stapleType) {
            Type* retval = nullptr;
            if(StapleInt* intType = dyn_cast<StapleInt>(stapleType)) {
                retval = Type::getIntNTy(getGlobalContext(), intType->getWidth());
            } else if(StaplePointer* ptrType = dyn_cast<StaplePointer>(stapleType)) {
                retval = getLLVMType(ptrType->getElementType());
            }

            return retval;
        }

        inline Type* getNodeType(ASTNode* node) {
            return getLLVMType(mCodeGen->mCompilerContext->typeTable[node]);
        }

        inline string createFunctionName(string name) {
            string retval = mCodeGen->mCompilerContext->package;
            replace(retval.begin(), retval.end(), '.', '_');
            retval += "_" + name;
            return retval;
        }

    public:
        LLVMCodeGenVisitor(LLVMCodeGenerator*codeGen)
        : mCodeGen(codeGen) {}

        void visit(NFunctionPrototype* functionPrototype) {

            StapleFunction* stapleFunction = cast<StapleFunction>(mCodeGen->mCompilerContext->typeTable[functionPrototype]);

            Type* returnType = getLLVMType(stapleFunction->getReturnType());

            vector<Type*> argTypes;
            for(NArgument* arg : functionPrototype->arguments) {
                argTypes.push_back(getNodeType(arg));
            }

            FunctionType* functionType = FunctionType::get(returnType, argTypes, stapleFunction->getIsVarg());
            Function* function = Function::Create(
                    functionType,
                    GlobalValue::LinkageTypes::ExternalLinkage,
                    functionPrototype->name.c_str(),
                    &mCodeGen->mModule);

            mValues[functionPrototype] = function;

        }

        void visit(NFunction* function) {
            StapleFunction* stapleFunction = cast<StapleFunction>(mCodeGen->mCompilerContext->typeTable[function]);

            Type* returnType = getLLVMType(stapleFunction->getReturnType());

            vector<Type*> argTypes;
            for(NArgument* arg : function->arguments) {
                argTypes.push_back(getNodeType(arg));
            }

            string functionName = createFunctionName(function->name);

            FunctionType* functionType = FunctionType::get(returnType, argTypes, stapleFunction->getIsVarg());
            Function* llvmFunction = Function::Create(
                    functionType,
                    GlobalValue::LinkageTypes::ExternalLinkage,
                    functionName.c_str(),
                    &mCodeGen->mModule);

            mValues[function] = llvmFunction;

            push();
            mScope->mBasicBlock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction);
            mCodeGen->mIRBuilder.SetInsertPoint(mScope->mBasicBlock);

            Function::arg_iterator AI = llvmFunction->arg_begin();
            for(int i=0;i<argTypes.size();i++,++AI) {
                Type* llvmArg = argTypes[i];
                NArgument* nodeArg = function->arguments[i];

                AllocaInst* alloc = mCodeGen->mIRBuilder.CreateAlloca(llvmArg, 0, nodeArg->name.c_str());
                mScope->defineSymbol(nodeArg->name, LocalVarLookup::get(alloc));
                mCodeGen->mIRBuilder.CreateStore(AI, alloc);
            }

            for(NStatement* statement : function->block.statements) {
                statement->accept(this);
            }

            pop();
        }

        void visit(NBlock* block) {

        }

    };

    LLVMCodeGenerator::LLVMCodeGenerator(CompilerContext *compilerContext)
    : mCompilerContext(compilerContext),
      mIRBuilder(getGlobalContext()),
      mModule(mCompilerContext->inputFilename.c_str(), getGlobalContext()),
      mFunctionPassManager(&mModule) {}

    void LLVMCodeGenerator::generateCode(NCompileUnit *compileUnit) {

        LLVMCodeGenVisitor visitor(this);
        for(NFunctionPrototype* functionPrototype : compileUnit->externFunctions) {
            visitor.visit(functionPrototype);
        }

        for(NFunction* function : compileUnit->functions) {
            visitor.visit(function);
        }

    }


    /*

    llvm::Type* StapleField::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = mType->getLLVMType();
        }
        return mCachedType;
    }


    llvm::Type* StapleArray::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = ArrayType::get(mElementType->getLLVMType(), mSize);
        }
        return mCachedType;
    }

    llvm::Type *StaplePointer::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = PointerType::getUnqual(mElementType->getLLVMType());
        }
        return mCachedType;
    }

    llvm::Type* StapleInt::getLLVMType() {
        if(mCachedType == nullptr) {
            mCachedType = Type::getIntNTy(getGlobalContext(), mWidth);
        }
        return mCachedType;
    }

    llvm::Type* StapleFloat::getLLVMType() {
        if(mCachedType == nullptr) {
            switch(mType) {
                case Type::f16:
                    mCachedType = llvm::Type::getHalfTy(getGlobalContext());
                    break;

                case Type::f32:
                    mCachedType = llvm::Type::getFloatTy(getGlobalContext());
                    break;

                case Type::f64:
                    mCachedType = llvm::Type::getDoubleTy(getGlobalContext());
                    break;
            }
        }
        return mCachedType;
    }

     */
}