#include "node.h"
#include "codegen.h"
#include "codegen/objecthelper.hpp"

#include <llvm/IR/Verifier.h>
#include <llvm/IR/DerivedTypes.h>
#include <llvm/IR/IRBuilder.h>
#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/Module.h>
#include <llvm/IR/Function.h>


namespace staple {

using namespace std;

StructType* ObjectHelper::sStapleRuntimeClassStruct = NULL;
StructType* ObjectHelper::sGenericObjectType = NULL;

Function* getStaple_release(CodeGenContext& context) {
	Function* retval = context.module->getFunction("stp_release");
	if(retval == NULL) {
		FunctionType *ftype = FunctionType::get(Type::getVoidTy(getGlobalContext()),
				std::vector<Type*>{
						PointerType::getUnqual(ObjectHelper::getGenericObjType())
				},
				false);
		retval = Function::Create(ftype, GlobalValue::ExternalLinkage, "stp_release", context.module);
	}

	return retval;
}



class ReleasePtr : public ScopeCleanUp {
public:
	Value* ptrToFree;
	bool shouldLoad;

	ReleasePtr(Value* ptr, bool shouldLoad = false)
	: ptrToFree(ptr), shouldLoad(shouldLoad)
	{}

	~ReleasePtr() {};

	User* getLastUsage(Value* value) {
		User* lastUser = value->user_back();
		if(BitCastInst* inst = dyn_cast<BitCastInst>(lastUser)) {
			return getLastUsage(inst);
		}
		return lastUser;
	}

	void scopeOut(CodeGenContext& context) {

		Function* releaseFunction = getStaple_release(context);

		Value* ptr = ptrToFree;
		if(shouldLoad) {
			ptr = context.Builder.CreateLoad(ptrToFree);
			ptr = context.Builder.CreatePointerCast(ptr, PointerType::getUnqual(ObjectHelper::getGenericObjType()));
			context.Builder.CreateCall(releaseFunction,
					std::vector<Value*>{ptr}
			);
		} else {
			User *lastUser = getLastUsage(ptr);
			if (Instruction *inst = dyn_cast<Instruction>(lastUser)) {
				if (!isa<ReturnInst>(lastUser)) {
					IRBuilder<> Builder(inst->getNextNode());
					ptr = Builder.CreatePointerCast(ptr, PointerType::getUnqual(ObjectHelper::getGenericObjType()));
					Builder.CreateCall(releaseFunction,
							std::vector<Value *>{ptr}
					);
				}
			}
		}

		/*
		Value* ptr = ptrToFree;
		if(shouldLoad) {
			ptr = context.Builder.CreateLoad(ptrToFree);
		}
		ptr = context.Builder.CreatePointerCast(ptr, PointerType::getUnqual(ObjectHelper::getGenericObjType()));

		Function* releaseFunction = getStaple_release(context);
		context.Builder.CreateCall(releaseFunction,
				std::vector<Value*>{ptr}
		);
		*/
	}
};

void loadFields(SClassType* classObj, std::vector<llvm::Type*>& elements)
{
    if(classObj->parent != NULL){
        loadFields(classObj->parent, elements);
    }
    for(auto field : classObj->fields){
        elements.push_back(field.second->type);
    }
}

#define getLLVMType(val) context.ctx.typeTable[val]->type

void Error(const char* str)
{
	fprintf(stderr, "Error: %s\n", str);
}

Function* getStaple_StrongStore(CodeGenContext& context) {
    Function* retval = context.module->getFunction("stp_storeStrong");
    if(retval == NULL) {
        FunctionType *ftype = FunctionType::get(Type::getVoidTy(getGlobalContext()),
                std::vector<Type*>{
                        PointerType::getUnqual(PointerType::getUnqual(ObjectHelper::getGenericObjType())),
                        PointerType::getUnqual(ObjectHelper::getGenericObjType())
                },
                false);
        retval = Function::Create(ftype, GlobalValue::ExternalLinkage, "stp_storeStrong", context.module);
    }

    return retval;
}


static void doObjCleanup(CodeGenContext& context)
{
	for(auto it=context.top->ptrsToFree.rbegin();it!=context.top->ptrsToFree.rend();it++){
		(*it)->scopeOut(context);
	}
}

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

class FieldLookup : public SymbolLookup {
private:
    Value* thisPtr;
    int fieldIndex;

public:

    static FieldLookup* get(Value* thisPtr, int fieldIndex) {
        FieldLookup* retval = new FieldLookup();
        retval->thisPtr = thisPtr;
        retval->fieldIndex = fieldIndex;
        return retval;
    }

    virtual Value* getValue(CodeGenContext& context) {

        Value* thisValue = context.Builder.CreateLoad(thisPtr);

        std::vector<Value*> indecies;
        indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
        indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), fieldIndex+1, false));

        return context.Builder.CreateGEP(thisValue, indecies);
    }
};



class ForwardDeclareFunctionVisitor : public ASTVisitor {
public:
	CodeGenContext& context;
	ForwardDeclareFunctionVisitor(CodeGenContext* context)
	: context(*context) {}

	virtual void visit(NFunction* fun) {

		vector<Type*> argTypes;
		for (std::vector<NArgument*>::const_iterator it = fun->arguments.begin(); it != fun->arguments.end(); it++) {
			argTypes.push_back(getLLVMType(*it));
		}
		FunctionType *ftype = FunctionType::get(getLLVMType(&fun->returnType), argTypes, fun->isVarg);
		fun->llvmFunction = Function::Create(ftype, fun->linkage, fun->name.c_str(), context.module);
	}

};

class ClassVisitor : public ASTVisitor {
private:
    ObjectHelper* mObjHelper;
public:
    CodeGenContext& context;

    ClassVisitor(CodeGenContext* context)
    : context(*context)
    , mObjHelper(NULL) {}


    virtual void visit(NClassDeclaration* classDecl) {

        SClassType* classType = (SClassType*) context.ctx.typeTable[classDecl];
        mObjHelper = new ObjectHelper(classType);
        context.mClassObjMap[classType] = mObjHelper;


        for(auto method : classDecl->functions){
            method->accept(this);
        }

        //init the full object type
        mObjHelper->getObjectType();

        //output the init function
        mObjHelper->emitInitFunction(context);

        //output the destructor function
        mObjHelper->emitDestroyFunction(context);

        //output class class object definition
        mObjHelper->getClassDef(context);


    }

    virtual void visit(NMethodFunction* fun) {
        std::vector<Type*> argTypes;
        std::vector<NArgument*>::iterator it;
        //add 'this' as first argument
        argTypes.push_back(PointerType::getUnqual(fun->classType->type));
        for (it = fun->arguments.begin(); it != fun->arguments.end(); it++) {
            argTypes.push_back(getLLVMType(*it));
        }
        FunctionType *ftype = FunctionType::get(getLLVMType(&fun->returnType), argTypes, fun->isVarg);

        char funName[512];
        snprintf(funName, 512, "%s_%s", fun->classType->name.c_str(), fun->name.c_str());

        Function* functionType = Function::Create(ftype, GlobalValue::ExternalLinkage, funName, context.module);
        fun->llvmFunction = functionType;

        mObjHelper->mMethods.push_back( std::make_pair(fun, functionType) );
    }


};

/* Compile the AST into a module */
void CodeGenContext::generateCode(NCompileUnit& root)
{
	compileUnitRoot = &root;

	for(std::vector<NFunctionPrototype*>::iterator it = root.externFunctions.begin(); it != root.externFunctions.end(); it++) {
		(*it)->codeGen(*this);
	}

	for (std::vector<NFunction*>::iterator it = root.functions.begin(); it != root.functions.end(); it++) {
        ForwardDeclareFunctionVisitor globalfuncdec(this);
		globalfuncdec.visit(*it);
	}

    for(auto classObj : root.classes) {
        ClassVisitor classVisitor(this);
        classVisitor.visit(classObj);
    }

	for (std::vector<NFunction*>::iterator it = root.functions.begin(); it != root.functions.end(); it++) {
		(*it)->codeGen(*this);
	}


    for(auto classObj : root.classes) {
        for (auto function : classObj->functions) {
            function->codeGen(*this);
        }
    }


	/* Print the bytecode in a human-readable format 
	   to see if our program compiled properly
	 */
	module->dump();
}

NType* NType::GetPointerType(const std::string& name, int numPtrs)
{
	NType* retval = new NType();
	retval->name = name;
	retval->isArray = false;
	retval->numPointers = numPtrs;
	return retval;
}

NType* NType::GetArrayType(const std::string& name, int size)
{
	NType* retval = new NType();
	retval->name = name;
	retval->isArray = true;
	retval->size = size;
	return retval;
}


/* -- Code Generation -- */

Value* NFunctionPrototype::codeGen(CodeGenContext &context)
{

	std::vector<Type*> argTypes;
	std::vector<NArgument*>::iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
        Type* argType = getLLVMType(*it);
		argTypes.push_back(argType);
	}
	FunctionType *ftype = FunctionType::get(context.ctx.typeTable[&returnType]->type, argTypes, isVarg);

	Function *function = Function::Create(ftype, GlobalValue::ExternalLinkage, name.c_str(), context.module);

}

Value* NIntLiteral::codeGen(CodeGenContext& context)
{
	int value = atoi(str.c_str());
	IntegerType* type = IntegerType::getIntNTy(getGlobalContext(), width);
	return ConstantInt::getSigned(type, value);
}

Value* NFloatLiteral::codeGen(CodeGenContext& context)
{
	double value = atof(str.c_str());
	return ConstantFP::get(getGlobalContext(), APFloat(value));
}

Value* NStringLiteral::codeGen(CodeGenContext& context)
{
	return context.Builder.CreateGlobalStringPtr(str.c_str());
}

Value* NIdentifier::codeGen(CodeGenContext& context)
{
	Value* v = context.getSymbol(name)->getValue(context);
	if (v == NULL) {
		std::cerr << "undeclared variable " << name << std::endl;
		return NULL;
	}
	return v;
}

Value* NFunctionCall::codeGen(CodeGenContext& context)
{
	Function *function = context.module->getFunction(name.c_str());
	if (function == NULL) {
		std::cerr << "no such function " << name << std::endl;
	}
	std::vector<Value*> args;
	ExpressionList::const_iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
		args.push_back((**it).codeGen(context));
	}

	return context.Builder.CreateCall(function, args);
}

Value* NMethodCall::codeGen(CodeGenContext &context)
{
    ObjectHelper* objHelper = context.mClassObjMap[classType];

    Value* baseVal = base->codeGen(context);

    Value* functionPtr = objHelper->getVirtualFunction(context, baseVal, methodIndex+1);

    std::vector<Value*> args;
    ExpressionList::const_iterator it;

    //add 'this' as first argument
    args.push_back(baseVal);
    for (it = arguments.begin(); it != arguments.end(); it++) {
        args.push_back((**it).codeGen(context));
    }

    return context.Builder.CreateCall(functionPtr, args);
}

Value*NArrayElementPtr::codeGen(CodeGenContext &context)
{
	Value* idSymbol = base->codeGen(context);
	Value* exprVal = expr->codeGen(context);

	std::vector<Value*> indecies;

	Type* baseType = idSymbol->getType()->getPointerElementType();
	if(baseType->isArrayTy()) {
		indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
	} else {
		idSymbol = NLoad(base).codeGen(context);
	}

	indecies.push_back(exprVal);

	return context.Builder.CreateGEP(idSymbol, indecies);
}

Value* NMemberAccess::codeGen(CodeGenContext &context)
{
	Value* baseVal = base->codeGen(context);

	//baseVal = context.Builder.CreateLoad(baseVal);

	std::vector<Value*> indecies;
	indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
	indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), fieldIndex, false));

	return context.Builder.CreateGEP(baseVal, indecies);
}

Value* NLoad::codeGen(CodeGenContext &context)
{
	Value* exprVal = expr->codeGen(context);
	return context.Builder.CreateLoad(exprVal);
}

Value* NBinaryOperator::codeGen(CodeGenContext& context)
{
	Value *l = lhs->codeGen(context);
	Value *r = rhs->codeGen(context);


	switch (op) {
		case TPLUS: 	return context.Builder.CreateAdd(l, r);
		case TMINUS: 	return context.Builder.CreateSub(l, r);
		case TMUL: 		return context.Builder.CreateMul(l, r);
		case TDIV: 		return context.Builder.CreateSDiv(l, r);
		case TCEQ:		return context.Builder.CreateICmpEQ(l, r);
		case TCNE:		return context.Builder.CreateICmpNE(l, r);
		case TCGT:		return context.Builder.CreateICmpSGT(l, r);
		case TCLT:		return context.Builder.CreateICmpSLT(l, r);
		case TCGE:		return context.Builder.CreateICmpSGE(l, r);
		case TCLE:		return context.Builder.CreateICmpSLE(l, r);
				
		default:
			Error("invalid binary operator");
			return 0;
	}
}

Value* NNot::codeGen(CodeGenContext &context)
{
	Value* baseVal = base->codeGen(context);
	return context.Builder.CreateNot(baseVal);
}

Value* NNegitive::codeGen(CodeGenContext &context)
{
	Value* baseVal = base->codeGen(context);
	return context.Builder.CreateNeg(baseVal);
}

Value* NAssignment::codeGen(CodeGenContext& context)
{
	Value* lhsValue = lhs->codeGen(context);
	Value* rhsValue = rhs->codeGen(context);

    SType* rhsType = context.ctx.typeTable[rhs];
    if(rhsType->isPointerTy() && ((SPointerType*)rhsType)->elementType->isClassTy()) {

        Function* strongStore = getStaple_StrongStore(context);
        context.Builder.CreateCall(strongStore, std::vector<Value*>{
                context.Builder.CreatePointerCast(lhsValue, PointerType::getUnqual(PointerType::getUnqual(ObjectHelper::getGenericObjType()))),
                context.Builder.CreatePointerCast(rhsValue, PointerType::getUnqual(ObjectHelper::getGenericObjType()))
        });
    } else {
        return context.Builder.CreateStore(rhsValue, lhsValue);
    }
}

Value* NBlock::codeGen(CodeGenContext& context)
{
	StatementList::const_iterator it;
	Value *last = NULL;
	for (it = statements.begin(); it != statements.end(); it++) {
		last = (**it).codeGen(context);
	}
	return last;
}

Value* NExpressionStatement::codeGen(CodeGenContext& context)
{
	return expression->codeGen(context);
}

Value* NVariableDeclaration::codeGen(CodeGenContext& context)
{
	SType* thisType = context.ctx.typeTable[this];
	AllocaInst *alloc = context.Builder.CreateAlloca(thisType->type, 0, name.c_str());

	if(thisType->isPointerTy() && ((SPointerType*)thisType)->elementType->isClassTy()) {
		context.Builder.CreateStore(ConstantPointerNull::get((PointerType*)thisType->type), alloc);
	}

	context.defineSymbol(name, LocalVarLookup::get(alloc));
	if (assignmentExpr != NULL) {
		NAssignment assn(new NIdentifier(name), assignmentExpr);
		assn.codeGen(context);
	}

    SType* varType = context.ctx.typeTable[this];
    if(varType->isPointerTy() && ((SPointerType*)varType)->elementType->isClassTy()) {
        context.top->ptrsToFree.push_back(std::unique_ptr<ScopeCleanUp>(new ReleasePtr(alloc, true)));
    }

	return alloc;
}

Value* NReturn::codeGen(CodeGenContext &context)
{
    doObjCleanup(context);
	Value* r = ret->codeGen(context);
	return context.Builder.CreateRet(r);
}

static AllocaInst* CreateEntryBlockAlloca(Function *TheFunction, Type* type, const std::string &VarName) {
	IRBuilder<> TmpB(&TheFunction->getEntryBlock(),
			TheFunction->getEntryBlock().begin());
	return TmpB.CreateAlloca(type, 0, VarName.c_str());
}

Value* NFunction::codeGen(CodeGenContext& context)
{

	BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction);
	context.pushBlock(bblock);
	context.Builder.SetInsertPoint(bblock);

	Function::arg_iterator AI = llvmFunction->arg_begin();
	for(size_t i=0,e=llvmFunction->arg_size();i!=e;++i,++AI){
		NArgument* arg = arguments[i];
		AllocaInst* alloc = CreateEntryBlockAlloca(llvmFunction, getLLVMType(arg), arg->name);
		context.defineSymbol(arg->name, LocalVarLookup::get(alloc));
		context.Builder.CreateStore(AI, alloc);
	}
	
	block.codeGen(context);

	Instruction &last = *bblock->getInstList().rbegin();
	if(llvmFunction->getReturnType()->isVoidTy() && !last.isTerminator()){
        doObjCleanup(context);
		ReturnInst::Create(getGlobalContext(), bblock);
	}

	context.popBlock();

	context.fpm->run(*llvmFunction);

	return llvmFunction;
}

Value* NMethodFunction::codeGen(CodeGenContext &context) {
    BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction);
    context.pushBlock(bblock);
    context.Builder.SetInsertPoint(bblock);


    Value* thisValue;
    {
        AllocaInst *alloc = CreateEntryBlockAlloca(llvmFunction, PointerType::getUnqual(this->classType->type), "this");
        context.defineSymbol("this", LocalVarLookup::get(alloc));
        context.Builder.CreateStore(llvmFunction->arg_begin(), alloc);
        thisValue = alloc;
    }

    for(auto fieldEntry : classType->fields) {
        int fieldIndex = classType->getFieldIndex(fieldEntry.first);
        context.defineSymbol(fieldEntry.first, FieldLookup::get(thisValue, fieldIndex));
    }

    Function::arg_iterator AI = llvmFunction->arg_begin();
    for(size_t i=1,e=llvmFunction->arg_size();i!=e;++i,++AI){
        NArgument* arg = arguments[i];
        AllocaInst* alloc = CreateEntryBlockAlloca(llvmFunction, getLLVMType(arg), arg->name);
        context.defineSymbol(arg->name, LocalVarLookup::get(alloc));
        context.Builder.CreateStore(AI, alloc);
    }

    block.codeGen(context);

    Instruction &last = *bblock->getInstList().rbegin();
    if(llvmFunction->getReturnType()->isVoidTy() && !last.isTerminator()){
        doObjCleanup(context);
        ReturnInst::Create(getGlobalContext(), bblock);
    }

    context.popBlock();

    context.fpm->run(*llvmFunction);

    return llvmFunction;
}

Value* NIfStatement::codeGen(CodeGenContext &context)
{
	Function* parent = context.Builder.GetInsertBlock()->getParent();

	BasicBlock* thenBB = BasicBlock::Create(getGlobalContext(), "then", parent);
	BasicBlock* elseBB = BasicBlock::Create(getGlobalContext(), "else");
	BasicBlock* mergeBlock = BasicBlock::Create(getGlobalContext(), "cont");


	Value* conditionValue = condition->codeGen(context);
	context.Builder.CreateCondBr(conditionValue, thenBB, elseBB);

	//parent->getBasicBlockList().push_back(thenBB);
	//parent->getBasicBlockList().push_back(elseBB);
	//parent->getBasicBlockList().push_back(mergeBlock);


	context.pushBlock(thenBB);
	context.Builder.SetInsertPoint(thenBB);
	thenBlock->codeGen(context);
	context.Builder.CreateBr(mergeBlock);
    doObjCleanup(context);
	context.popBlock();


	context.pushBlock(elseBB);
	parent->getBasicBlockList().push_back(elseBB);
	context.Builder.SetInsertPoint(elseBB);
	if(elseBlock != NULL) {
		elseBlock->codeGen(context);
	}
	context.Builder.CreateBr(mergeBlock);
    doObjCleanup(context);
	context.popBlock();

	parent->getBasicBlockList().push_back(mergeBlock);
	context.Builder.SetInsertPoint(mergeBlock);
}

Function* getMalloc(CodeGenContext& context) {

	Function* retval = context.module->getFunction("malloc");
	if(retval == NULL) {
		std::vector<Type*> argTypes;
		argTypes.push_back(IntegerType::getInt32Ty(getGlobalContext()));

		Type* returnType = Type::getInt8PtrTy(getGlobalContext());
		FunctionType *ftype = FunctionType::get(returnType, argTypes, false);
		retval = Function::Create(ftype, GlobalValue::ExternalLinkage, "malloc", context.module);
	}

	return retval;
}

Function* CodeGenContext::getFree() {
	Function* retval = module->getFunction("free");
	if(retval == NULL) {
		std::vector<Type*> argTypes;
		argTypes.push_back(Type::getInt8PtrTy(getGlobalContext()));

		Type* returnType = Type::getVoidTy(getGlobalContext());
		FunctionType *ftype = FunctionType::get(returnType, argTypes, false);
		retval = Function::Create(ftype, GlobalValue::ExternalLinkage, "free", module);
	}

	return retval;
}

Function* CodeGenContext::getRelease() {
	return getStaple_release(*this);
}

Value* NSizeOf::codeGen(CodeGenContext &context)
{
    Type* type = context.ctx.typeTable[this]->type;

	PointerType* pointerType = PointerType::getUnqual(type);
	Value* ptr = ConstantPointerNull::get(pointerType);
	Value* one = ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 1);

	Value* size = context.Builder.CreateGEP(ptr, one, "size");
	size = context.Builder.CreatePointerCast(size, IntegerType::getInt32Ty(getGlobalContext()));
	return size;
}

Value* NNew::codeGen(CodeGenContext& context)
{
    SPointerType* classPtr = (SPointerType *) context.ctx.typeTable[this];
    SClassType* classType = (SClassType*) classPtr->elementType;

	PointerType* pointerType = PointerType::getUnqual(classType->type);

	Value* ptr = ConstantPointerNull::get(pointerType);
	Value* one = ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 1);

	Value* size = context.Builder.CreateGEP(ptr, one, "size");
	size = context.Builder.CreatePointerCast(size, IntegerType::getInt32Ty(getGlobalContext()));

	Function* malloc = getMalloc(context);

	std::vector<Value*> args;
	args.push_back(size);

	Value* retval = context.Builder.CreateCall(malloc, args);

	retval = context.Builder.CreatePointerCast(retval, pointerType);

	context.top->ptrsToFree.push_back(std::unique_ptr<ScopeCleanUp>(new ReleasePtr(retval)));

    ObjectHelper* objHelper = context.mClassObjMap[classType];
    Function* initFunction = objHelper->getInitFunction(context);

    context.Builder.CreateCall(initFunction, std::vector<Value*>{retval});
	return retval;
}

} // namespace staple