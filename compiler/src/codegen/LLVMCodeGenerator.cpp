

#include "LLVMCodeGenerator.h"
#include "../compilercontext.h"
#include "../types/stapletype.h"
#include "LLVMStapleObject.h"

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

    class LLVMDebugInfo {
    private:
        map<StapleType*, DIType> mDebugTypeCache;
        const LLVMCodeGenerator* mCodeGen;
    public:
        LLVMDebugInfo(const LLVMCodeGenerator* codeGen) : mCodeGen(codeGen) {}

        DICompileUnit mCompileUnit;
        DIFile mFile;

        DIType getLLVMDebugType(StapleType* stapleType);

    };

    DIType LLVMDebugInfo::getLLVMDebugType(StapleType* stapleType) {
        DIType retval;
        auto it = mDebugTypeCache.find(stapleType);
        if(it != mDebugTypeCache.end()) {
            retval = it->second;
        } else {
            if(StapleInt* intType = dyn_cast<StapleInt>(stapleType)) {
                retval = mCodeGen->mDIBuider->createBasicType("int", intType->getWidth(), 0, dwarf::DW_ATE_signed);
            } else if(StaplePointer* ptrType = dyn_cast<StaplePointer>(stapleType)){
                retval = mCodeGen->mDIBuider->createPointerType(getLLVMDebugType(ptrType->getElementType()), 64);
            } else {
                retval = mCodeGen->mDIBuider->createUnspecifiedType("unknown");
            }
            mDebugTypeCache[stapleType] = retval;
        }
        return retval;

    }

    class CodeGenBlock {
    private:
        CodeGenBlock* mParent;
        map<const string, Value*> mSymbolTable;

    public:
        CodeGenBlock(CodeGenBlock* parent)
        : mParent(parent), mDebugInfo(parent == nullptr ? nullptr : parent->mDebugInfo){}

        CodeGenBlock* getParent() const { return mParent; }

        void defineSymbol(const string& name, Value* llvmValue) {
            mSymbolTable[name] = llvmValue;
        }

        Value* lookupSymbol(const string& name) {
            auto it = mSymbolTable.find(name);
            if(it != mSymbolTable.end()) {
                return it->second;
            } else if(mParent != nullptr){
                return mParent->lookupSymbol(name);
            } else {
                return nullptr;
            }
        }

        LLVMDebugInfo* mDebugInfo;
        BasicBlock* mBasicBlock;
        DIScope mDIScope;

    };

    class LLVMFunctionForwardDeclVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        LLVMCodeGenerator* mCodeGen;
        CodeGenBlock* mScope;
        map<ASTNode*, Value*>& mValues;

    public:
        LLVMFunctionForwardDeclVisitor(LLVMCodeGenerator* codeGen, CodeGenBlock* scope, map<ASTNode*, Value*>& values)
        : mCodeGen(codeGen), mScope(scope), mValues(values) {}

        void visit(NFunctionPrototype* functionPrototype) {

            StapleFunction* stapleFunction = cast<StapleFunction>(mCodeGen->mCompilerContext->typeTable[functionPrototype]);

            Type* returnType = mCodeGen->getLLVMType(stapleFunction->getReturnType());

            vector<Type*> argTypes;
            for(StapleType* arg : stapleFunction->getArguments()) {
                argTypes.push_back(mCodeGen->getLLVMType(arg));
            }

            FunctionType* functionType = FunctionType::get(returnType, argTypes, stapleFunction->getIsVarg());
            Function* function = Function::Create(
                    functionType,
                    GlobalValue::LinkageTypes::ExternalLinkage,
                    functionPrototype->name.c_str(),
                    &mCodeGen->mModule);

            mScope->defineSymbol(functionPrototype->name, function);
            mValues[functionPrototype] = function;

        }

        void visit(NFunction* function) {

            StapleFunction* stpFunctionType = cast<StapleFunction>(mCodeGen->mCompilerContext->typeTable[function]);

            Type* returnType = mCodeGen->getLLVMType(stpFunctionType->getReturnType());

            vector<Type*> argTypes;
            for(StapleType* arg : stpFunctionType->getArguments()) {
                argTypes.push_back(mCodeGen->getLLVMType(arg));
            }

            FunctionType* functionType = FunctionType::get(returnType, argTypes, stpFunctionType->getIsVarg());

            string functionName = mCodeGen->createNamespaceSymbolName(function->name);

            Function* llvmFunction = Function::Create(
                    functionType,
                    GlobalValue::LinkageTypes::ExternalLinkage,
                    function->name.compare("main") == 0 ? function->name.c_str() : functionName.c_str(),
                    &mCodeGen->mModule);

            mScope->defineSymbol(function->name, llvmFunction);
            mScope->defineSymbol(functionName, llvmFunction);
            mValues[function] = llvmFunction;
        }
    };

    class LLVMCodeGenVisitor : public ASTVisitor {
    using ASTVisitor::visit;
    private:
        LLVMCodeGenerator* mCodeGen;
        map<ASTNode*, Value*> mValues;
        CodeGenBlock* mScope;
        StapleClass* mCurrentClass;

        Function* getMallocFunction() {

            Function* retval = mCodeGen->mModule.getFunction("malloc");
            if(retval == NULL) {
                std::vector<Type*> argTypes;
                argTypes.push_back(IntegerType::getInt32Ty(getGlobalContext()));

                Type* returnType = Type::getInt8PtrTy(getGlobalContext());
                FunctionType *ftype = FunctionType::get(returnType, argTypes, false);
                retval = Function::Create(ftype, Function::LinkageTypes::ExternalLinkage, "malloc", &mCodeGen->mModule);
            }

            return retval;
        }


        void push() {
            mScope = new CodeGenBlock(mScope);
        }

        void pop() {
            CodeGenBlock* parent = mScope->getParent();
            delete mScope;
            mScope = parent;
        }

        void emitDebugLocation(ASTNode* node) {
            mCodeGen->mIRBuilder.SetCurrentDebugLocation(DebugLoc::get(node->location.first_line, node->location.first_column, mScope->mDIScope));
        }


        inline Type* getNodeType(ASTNode* node) {
            return mCodeGen->getLLVMType(mCodeGen->mCompilerContext->typeTable[node]);
        }


    public:
        LLVMCodeGenVisitor(LLVMCodeGenerator*codeGen)
        : mCodeGen(codeGen), mScope(new CodeGenBlock(nullptr)) {}

        Value* getValue(ASTNode* node) {
            node->accept(this);
            return mValues[node];
        }

        void visit(NCompileUnit* compileUnit) {

            if(mCodeGen->mCompilerContext->debugSymobols) {
                mScope->mDebugInfo = new LLVMDebugInfo(mCodeGen);
                mScope->mDebugInfo->mCompileUnit = mCodeGen->mDIBuider->createCompileUnit(
                        dwarf::DW_LANG_C, mCodeGen->mCompilerContext->inputFilename.c_str(), ".",
                        "Staple Compiler", false, "", 0);
                mScope->mDebugInfo->mFile = mCodeGen->mDIBuider->createFile(
                        mCodeGen->mCompilerContext->inputFilename.c_str(), ".");

                mScope->mDIScope = mScope->mDebugInfo->mFile;
            }

            {
                LLVMFunctionForwardDeclVisitor visitor(mCodeGen, mScope, mValues);

                for (NFunctionPrototype *functionPrototype : compileUnit->externFunctions) {
                    functionPrototype->accept(&visitor);
                }

                for (NFunction *function : compileUnit->functions) {
                    function->accept(&visitor);
                }
            }

            for(NFunction* function : compileUnit->functions) {
                function->accept(this);
            }

            for(NClassDeclaration* classDeclaration : compileUnit->classes) {
                classDeclaration->accept(this);
            }
        }

        void visit(NStringLiteral* strLiteral) {
            Value* retval = mCodeGen->mIRBuilder.CreateGlobalStringPtr(strLiteral->str.c_str());
            mValues[strLiteral] = retval;
        }

        DICompositeType createDebugFunctionType(StapleFunction* stpFunctionType) {
            vector<Value*> elements;

            elements.push_back(mScope->mDebugInfo->getLLVMDebugType(stpFunctionType->getReturnType()));
            for(StapleType* arg : stpFunctionType->getArguments()) {
                elements.push_back(mScope->mDebugInfo->getLLVMDebugType(arg));
            }

            return mCodeGen->mDIBuider->createSubroutineType(mScope->mDebugInfo->mFile, mCodeGen->mDIBuider->getOrCreateArray(elements));
        }

        void visit(NFunction* function) {

            Function* llvmFunction = cast<Function>(mValues[function]);

            push();
            mScope->mBasicBlock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction);
            mCodeGen->mIRBuilder.SetInsertPoint(mScope->mBasicBlock);

            StapleFunction* stpFunctionType = cast<StapleFunction>(mCodeGen->mCompilerContext->typeTable[function]);

            if(mCodeGen->mCompilerContext->debugSymobols) {
                mScope->mDIScope = mCodeGen->mDIBuider->createFunction(mScope->getParent()->mDIScope,
                                                                       function->name.c_str(), StringRef(),
                                                                       mScope->mDebugInfo->mFile,
                                                                       function->location.first_line,
                                                                       createDebugFunctionType(stpFunctionType), false,
                                                                       true, 0);

                emitDebugLocation(function);

            }

            const size_t numArgs = function->arguments.size();
            Function::arg_iterator AI = llvmFunction->arg_begin();
            for(int i=0;i<numArgs;i++,++AI) {
                Type* llvmArg = AI->getType();
                NArgument* nodeArg = function->arguments[i];

                AllocaInst* alloc = mCodeGen->mIRBuilder.CreateAlloca(llvmArg, 0, nodeArg->name.c_str());
                mScope->defineSymbol(nodeArg->name, alloc);
                mCodeGen->mIRBuilder.CreateStore(AI, alloc);
            }

            for(NStatement* statement : function->block.statements) {
                statement->accept(this);
            }

            pop();
        }

        void visit(NMethodFunction* methodFunction) {

            LLVMStapleObject* stapleObject = LLVMStapleObject::get(mCurrentClass);

            StapleMethodFunction* stpFunctionType = cast<StapleMethodFunction>(mCodeGen->mCompilerContext->typeTable[methodFunction]);

            FunctionType* functionType = cast<FunctionType>(mCodeGen->getLLVMType(stpFunctionType));

            /*
            Type* returnType = mCodeGen->getLLVMType(stpFunctionType->getReturnType());

            vector<Type*> argTypes;
            argTypes.push_back(PointerType::getUnqual(stapleObject->getObjectType(mCodeGen)));
            for(StapleType* arg : stpFunctionType->getArguments()) {
                argTypes.push_back(mCodeGen->getLLVMType(arg));
            }

            FunctionType* functionType = FunctionType::get(returnType, argTypes, stpFunctionType->getIsVarg());
             */

            string functionName = mCodeGen->createClassSymbolName(mCurrentClass) + "_" + methodFunction->name;

            Function* llvmFunction = cast<Function>(mCodeGen->mModule.getOrInsertFunction(functionName.c_str(), functionType));

            /*
            Function* llvmFunction = Function::Create(
                    functionType,
                    GlobalValue::LinkageTypes::PrivateLinkage,
                    functionName.c_str(),
                    &mCodeGen->mModule);
            */


            mScope->defineSymbol(functionName, llvmFunction);
            mValues[methodFunction] = llvmFunction;

            push();
            mScope->defineSymbol(methodFunction->name, llvmFunction);
            mScope->mBasicBlock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction);
            mCodeGen->mIRBuilder.SetInsertPoint(mScope->mBasicBlock);

            if(mCodeGen->mCompilerContext->debugSymobols) {
                mScope->mDIScope = mCodeGen->mDIBuider->createFunction(mScope->getParent()->mDIScope,
                                                                       methodFunction->name.c_str(), StringRef(),
                                                                       mScope->mDebugInfo->mFile,
                                                                       methodFunction->location.first_line,
                                                                       createDebugFunctionType(stpFunctionType), false,
                                                                       true, 0);

                emitDebugLocation(methodFunction);

            }


            Function::arg_iterator AI = llvmFunction->arg_begin();
            Value* thisPtr = AI;
            mScope->defineSymbol("this", thisPtr);

            //define all the fields in scope
            for(StapleField* field : mCurrentClass->getFields()) {
                uint fieldIndex = 1; // start at index 1 to account for the ref counter
                mCurrentClass->getField(field->getName(), fieldIndex);
                Value *fieldPtr = mCodeGen->mIRBuilder.CreateConstGEP2_32(thisPtr, 0, fieldIndex);
                mScope->defineSymbol(field->getName(), fieldPtr);
            }

            AI++;

            int i = 0;
            while(AI != llvmFunction->arg_end()) {
                string argName = methodFunction->arguments[i++]->name;
                AllocaInst* alloc = mCodeGen->mIRBuilder.CreateAlloca(AI->getType(), 0, argName.c_str());
                mScope->defineSymbol(argName.c_str(), alloc);
                mCodeGen->mIRBuilder.CreateStore(AI, alloc);
                AI++;
            }

            for(NStatement* statement : methodFunction->block.statements) {
                statement->accept(this);
            }


            pop();

        }

        void visit(NClassDeclaration* classDeclaration) {

            mCurrentClass = cast<StapleClass>(mCodeGen->mCompilerContext->typeTable[classDeclaration]);

            for(NMethodFunction* method : classDeclaration->functions) {
                method->accept(this);
            }

        }

        void visit(NVariableDeclaration* declaration) {

            if(mCodeGen->mCompilerContext->debugSymobols) {
                emitDebugLocation(declaration);
            }

            StapleType* type = mCodeGen->mCompilerContext->typeTable[declaration];

            AllocaInst* alloc = mCodeGen->mIRBuilder.CreateAlloca(mCodeGen->getLLVMType(type), 0, declaration->name.c_str());
            mScope->defineSymbol(declaration->name, alloc);

            if(mCodeGen->mCompilerContext->debugSymobols) {

                DITypeRef diType = mScope->mDebugInfo->getLLVMDebugType(type);
                DIVariable debugSymbol = mCodeGen->mDIBuider->createLocalVariable(dwarf::DW_TAG_auto_variable,
                                                                                  mScope->mDIScope,
                                                                                  declaration->name.c_str(),
                                                                                  mScope->mDebugInfo->mFile,
                                                                                  declaration->location.first_line,
                                                                                  diType);

                Instruction *call = mCodeGen->mDIBuider->insertDeclare(alloc, debugSymbol, mCodeGen->mIRBuilder.GetInsertBlock());
                call->setDebugLoc(DebugLoc::get(declaration->location.first_line, declaration->location.first_column, mScope->mDIScope));


            }

            if(StaplePointer* ptrType = dyn_cast<StaplePointer>(type)) {
                mCodeGen->mIRBuilder.CreateStore(ConstantPointerNull::get(cast<PointerType>(mCodeGen->getLLVMType(ptrType))), alloc);
            }

            if(declaration->assignmentExpr != nullptr) {
                NAssignment assign(new NIdentifier(declaration->name), declaration->assignmentExpr);
                assign.location = declaration->location;
                assign.accept(this);
            }

            mValues[declaration] = alloc;
        }

        void visit(NIdentifier* identifier) {
            Value* value = mScope->lookupSymbol(identifier->name);
            mValues[identifier] = value;
        }

        void visit(NIntLiteral* intLiteral) {
            int value = atoi(intLiteral->str.c_str());
            mValues[intLiteral] = mCodeGen->mIRBuilder.getInt(APInt(intLiteral->width, value));
        }

        void visit(NNew* newnode) {
            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(newnode);
            }
            StaplePointer* ptrType = cast<StaplePointer>(mCodeGen->mCompilerContext->typeTable[newnode]);

            PointerType* llvmPtrType = cast<PointerType>(mCodeGen->getLLVMType(ptrType));

            Value* nullptrValue = ConstantPointerNull::get(llvmPtrType);
            Value* size = mCodeGen->mIRBuilder.CreateGEP(nullptrValue, ConstantInt::get(mCodeGen->mIRBuilder.getInt32Ty(), 1));
            size = mCodeGen->mIRBuilder.CreatePointerCast(size, mCodeGen->mIRBuilder.getInt32Ty());

            Function* malloc = getMallocFunction();

            Value* retval = mCodeGen->mIRBuilder.CreateCall(malloc, size);
            retval = mCodeGen->mIRBuilder.CreatePointerCast(retval, llvmPtrType);

            mValues[newnode] = retval;

            //call init function
            StapleClass* stapleClass = cast<StapleClass>(ptrType->getElementType());
            LLVMStapleObject* llvmStapleObject = LLVMStapleObject::get(stapleClass);

            Function* initFunction = llvmStapleObject->getInitFunction(mCodeGen);
            mCodeGen->mIRBuilder.CreateCall(initFunction, retval);

        }

        void visit(NAssignment* assignment) {
            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(assignment);
            }
            Value* lhsValue = getValue(assignment->lhs);
            Value* rhsValue = getValue(assignment->rhs);

            StapleType* rhsType = mCodeGen->mCompilerContext->typeTable[assignment->rhs];
            StaplePointer* ptrType;
            if((ptrType = dyn_cast<StaplePointer>(rhsType)) && isa<StapleClass>(ptrType->getElementType())) {

                Function* strongStore = LLVMStapleObject::getStoreStrongFunction(&mCodeGen->mModule);
                mCodeGen->mIRBuilder.CreateCall(strongStore, std::vector<Value*>{
                        mCodeGen->mIRBuilder.CreatePointerCast(lhsValue, PointerType::getUnqual(PointerType::getUnqual(LLVMStapleObject::getStpObjInstanceType()))),
                        mCodeGen->mIRBuilder.CreatePointerCast(rhsValue, PointerType::getUnqual(LLVMStapleObject::getStpObjInstanceType()))
                });

            } else {
                mCodeGen->mIRBuilder.CreateStore(rhsValue, lhsValue);
            }
        }

        void visit(NLoad* load) {
            Value* ptr = getValue(load->expr);
            mValues[load] = mCodeGen->mIRBuilder.CreateLoad(ptr);
        }

        void visit(NBinaryOperator* binaryOperator) {

            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(binaryOperator);
            }

            Value* l = getValue(binaryOperator->lhs);
            Value* r = getValue(binaryOperator->rhs);

            Value* retval = nullptr;

            switch (binaryOperator->op) {
                case TPLUS: 	retval = mCodeGen->mIRBuilder.CreateAdd(l, r);
                    break;
                case TMINUS: 	retval = mCodeGen->mIRBuilder.CreateSub(l, r);
                    break;
                case TMUL: 		retval = mCodeGen->mIRBuilder.CreateMul(l, r);
                    break;
                case TDIV: 		retval = mCodeGen->mIRBuilder.CreateSDiv(l, r);
                    break;
                case TCEQ:		retval = mCodeGen->mIRBuilder.CreateICmpEQ(l, r);
                    break;
                case TCNE:		retval = mCodeGen->mIRBuilder.CreateICmpNE(l, r);
                    break;
                case TCGT:		retval = mCodeGen->mIRBuilder.CreateICmpSGT(l, r);
                    break;
                case TCLT:		retval = mCodeGen->mIRBuilder.CreateICmpSLT(l, r);
                    break;
                case TCGE:		retval = mCodeGen->mIRBuilder.CreateICmpSGE(l, r);
                    break;
                case TCLE:		retval = mCodeGen->mIRBuilder.CreateICmpSLE(l, r);
                    break;

            }

            mValues[binaryOperator] = retval;
        }

        void visit(NIfStatement* ifStatement) {

            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(ifStatement);
            }

            Function* parent = mCodeGen->mIRBuilder.GetInsertBlock()->getParent();


            BasicBlock* thenBB = dyn_cast<BasicBlock>(getValue(ifStatement->thenBlock));
            BasicBlock* elseBB = ifStatement->elseBlock != nullptr
                                 ? dyn_cast<BasicBlock>(getValue(ifStatement->elseBlock))
                                 : nullptr;

            BasicBlock* mergeBlock = BasicBlock::Create(getGlobalContext(), "", parent);

            IRBuilder<> builder(thenBB);
            builder.CreateBr(mergeBlock);

            if(elseBB != nullptr) {
                IRBuilder<> builder(elseBB);
                builder.CreateBr(mergeBlock);
            } else {
                elseBB = mergeBlock;
            }

            Value* conditionValue = getValue(ifStatement->condition);
            mCodeGen->mIRBuilder.CreateCondBr(conditionValue, thenBB, elseBB);


            mScope->mBasicBlock = mergeBlock;
            mCodeGen->mIRBuilder.SetInsertPoint(mergeBlock);

        }

        void visit(NMemberAccess* memberAccess) {

            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(memberAccess);
            }

            StapleType* baseType = mCodeGen->mCompilerContext->typeTable[memberAccess->base];

            StaplePointer* ptr = nullptr;
            StapleClass* classPtr = nullptr;
            if((ptr = dyn_cast<StaplePointer>(baseType)) && (classPtr = dyn_cast<StapleClass>(ptr->getElementType()))) {
                Value* basePtr = getValue(memberAccess->base);
                LLVMStapleObject* stapleObject = LLVMStapleObject::get(classPtr);
                Value* fieldPtr = stapleObject->getFieldPtr(memberAccess->field, mCodeGen->mIRBuilder, basePtr);

                mValues[memberAccess] = fieldPtr;
            }


        }

        void visit(NFunctionCall* functionCall) {

            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(functionCall);
            }

            Function* function = cast<Function>(mScope->lookupSymbol(functionCall->name));

            vector<Value*> argValues;
            for(NExpression* argExp : functionCall->arguments) {
                argValues.push_back(getValue(argExp));
            }

            mValues[functionCall] = mCodeGen->mIRBuilder.CreateCall(function, argValues);

        }

        void visit(NExpressionStatement* expressionStatement) {
            expressionStatement->expression->accept(this);
        }

        void visit(NBlock* block) {

            Function* parent = mCodeGen->mIRBuilder.GetInsertBlock()->getParent();
            BasicBlock* basicBlock = BasicBlock::Create(getGlobalContext(), "", parent);
            mValues[block] = basicBlock;

            push();

            if(mCodeGen->mCompilerContext->debugSymobols) {
                mScope->mDIScope = mCodeGen->mDIBuider->createLexicalBlock(mScope->getParent()->mDIScope, mScope->mDebugInfo->mFile, block->location.first_line, block->location.first_column, 0);
            }

            mScope->mBasicBlock = basicBlock;
            mCodeGen->mIRBuilder.SetInsertPoint(mScope->mBasicBlock);

            for(NStatement* statement : block->statements) {
                statement->accept(this);
            }

            pop();

            mCodeGen->mIRBuilder.SetInsertPoint(mScope->mBasicBlock);

        }

        void visit(NReturn* ret) {

            if(mCodeGen->mCompilerContext->debugSymobols){
                emitDebugLocation(ret);
            }

            Value* retval = getValue(ret->ret);
            mCodeGen->mIRBuilder.CreateRet(retval);
        }

    };

    LLVMCodeGenerator::LLVMCodeGenerator(CompilerContext *compilerContext)
    : mCompilerContext(compilerContext),
      mIRBuilder(getGlobalContext()),
      mModule(mCompilerContext->inputFilename.c_str(), getGlobalContext()),
      mFunctionPassManager(&mModule)

    {
        if(mCompilerContext->debugSymobols) {
            mModule.addModuleFlag(llvm::Module::Warning, "Dwarf Version", 4);
            mModule.addModuleFlag(llvm::Module::Error, "Debug Info Version", llvm::DEBUG_METADATA_VERSION);

            mDIBuider = new DIBuilder(mModule);
        }
    }

    string LLVMCodeGenerator::createNamespaceSymbolName(const string &name) {
        string retval = mCompilerContext->package;
        replace(retval.begin(), retval.end(), '.', '_');
        retval += "_" + name;
        return retval;
    }

    string LLVMCodeGenerator::createClassSymbolName(const StapleClass *stapleClass) {
        string retval = stapleClass->getClassName();
        replace(retval.begin(), retval.end(), '.', '_');
        return retval;
    }

    /*
    std::map<StapleClass*, StructType*> mClassStructCache;

    StructType* createClassType(StapleClass* stapleClass, StructType* llvmStructType) {
        vector<Type*> elements;
        for(StapleType* stapleType : stapleClass->getFields()) {
            elements.push_back(LLVMCodeGenerator::getLLVMType(stapleType));
        }
        llvmStructType->setBody(elements);
    }
     */

    Type* LLVMCodeGenerator::getLLVMType(StapleType* stapleType) {
        Type* retval = nullptr;
        if(stapleType == StapleType::getVoidType()) {
            retval = Type::getVoidTy(getGlobalContext());
        } else if(stapleType == StapleType::getBoolType()) {
            retval = Type::getInt1Ty(getGlobalContext());
        } else if(StapleInt* intType = dyn_cast<StapleInt>(stapleType)) {
            retval = Type::getIntNTy(getGlobalContext(), intType->getWidth());
        } else if(StaplePointer* ptrType = dyn_cast<StaplePointer>(stapleType)) {
            retval = PointerType::getUnqual(getLLVMType(ptrType->getElementType()));
        } else if(StapleClass* classType = dyn_cast<StapleClass>(stapleType)) {
            LLVMStapleObject* objHelper = LLVMStapleObject::get(classType);
            retval = objHelper->getObjectType(this);
        } else if(StapleField* field = dyn_cast<StapleField>(stapleType)) {
            retval = getLLVMType(field->getElementType());
        } else if(StapleClassDef* classDef = dyn_cast<StapleClassDef>(stapleType)) {
            LLVMStapleObject* llvmStapleObject = LLVMStapleObject::get(classDef->getClass());
            retval = llvmStapleObject->getClassDefType(this);
        } else if(StapleMethodFunction* method = dyn_cast<StapleMethodFunction>(stapleType)){
            vector<Type*> argTypes;
            argTypes.push_back(PointerType::getUnqual(getLLVMType(method->getClass())));
            for(StapleType* argType : method->getArguments()) {
                argTypes.push_back(getLLVMType(argType));
            }
            retval = FunctionType::get(getLLVMType(method->getReturnType()), argTypes, method->getIsVarg());
        } else if(StapleFunction* function = dyn_cast<StapleFunction>(stapleType)) {
            vector<Type*> argTypes;
            for(StapleType* argType : function->getArguments()) {
                argTypes.push_back(getLLVMType(argType));
            }
            retval = FunctionType::get(getLLVMType(function->getReturnType()), argTypes, function->getIsVarg());
        }

        return retval;
    }

    void LLVMCodeGenerator::generateCode(NCompileUnit *compileUnit) {

        LLVMCodeGenVisitor visitor(this);
        compileUnit->accept(&visitor);

        if(mCompilerContext->debugSymobols) {
            mDIBuider->finalize();
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