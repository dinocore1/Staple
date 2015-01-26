#include "node.h"
#include "codegen.h"
#include "type.h"

using namespace std;

class ILClassType {
private:
    StructType* objStruct;
    StructType* runtimeStructType;
    Constant* runtimeDef;

public:
    const SClassType* sClassType;


    ILClassType(SClassType* classType)
    : objStruct(NULL),
      runtimeDef(NULL),
      sClassType(classType) {

    }

    StructType* getRuntimeStructType() {
        if(runtimeStructType == NULL) {
            std::vector<Type*> types;

            //classname
            types.push_back(PointerType::getInt8PtrTy(getGlobalContext()));

            //parent runtime struct ptr
            types.push_back(PointerType::getInt8PtrTy(getGlobalContext()));

            //vtable
            for(auto it=sClassType->methods.begin();it!=sClassType->methods.end();it++) {
                types.push_back(PointerType::getUnqual((*it).second->type));
            }

            char funName[512];
            snprintf(funName, 512, "%s_class", sClassType->name.c_str());

            runtimeStructType = StructType::create(types, funName);

        }
        return runtimeStructType;
    }

    StructType* getObjStructType() {
        if (objStruct == NULL) {
            std::vector<Type *> typeFields;

            typeFields.push_back(PointerType::getUnqual(getRuntimeStructType()));

            for (auto it=sClassType->fields.begin();it!=sClassType->fields.end();it++) {
                typeFields.push_back((*it).second->type);
            }
            objStruct = StructType::create(typeFields, sClassType->name.c_str());
        }
        return objStruct;
    }

};

static Type* getBaseType(const std::string& name, const CodeGenContext& ctx);



void Error(const char* str)
{
	fprintf(stderr, "Error: %s\n", str);
}

class ForwardDeclareFunctionVisitor : public ASTVisitor {
public:
	const CodeGenContext* context;
	ForwardDeclareFunctionVisitor(const CodeGenContext* context)
	: context(context) {}

	virtual void visit(NFunction* fun) {

		vector<Type*> argTypes;
		for (std::vector<NArgument*>::const_iterator it = fun->arguments.begin(); it != fun->arguments.end(); it++) {
			argTypes.push_back((**it).type.getLLVMType(*context));
		}
		FunctionType *ftype = FunctionType::get(fun->returnType.getLLVMType(*context), argTypes, fun->isVarg);
		fun->llvmFunction = Function::Create(ftype, fun->linkage, fun->name.c_str(), context->module);

	}

    virtual void visit(NMethodFunction* fun) {
        std::vector<Type*> argTypes;
        std::vector<NArgument*>::iterator it;
        //add 'this' as first argument

        ILClassType classType(fun->classType);

        argTypes.push_back(PointerType::getUnqual(classType.getObjStructType()));
        //argTypes.push_back(PointerType::getUnqual(getBaseType(fun->classType->name, *context)));
        //argTypes.push_back(PointerType::getUnqual(fun->classType->type));
        for (it = fun->arguments.begin(); it != fun->arguments.end(); it++) {
            argTypes.push_back((**it).type.getLLVMType(*context));
        }
        FunctionType *ftype = FunctionType::get(fun->returnType.getLLVMType(*context), argTypes, fun->isVarg);

        char funName[512];
        snprintf(funName, 512, "%s_%s", fun->classType->name.c_str(), fun->name.c_str());
        fun->llvmFunction = Function::Create(ftype, GlobalValue::ExternalLinkage, funName, context->module);

    }
};

NClassDeclaration* CodeGenContext::getClass(const std::string &name) const {
	NClassDeclaration* retval = NULL;
	std::vector<NClassDeclaration*>::iterator it;
	for(it = compileUnitRoot->classes.begin(); it != compileUnitRoot->classes.end(); it++){
		if((*it)->name.compare(name) == 0){
			retval = *it;
			break;
		}
	}
	return retval;
}

/* Compile the AST into a module */
void CodeGenContext::generateCode(NCompileUnit& root)
{
	compileUnitRoot = &root;

	for(std::vector<NFunctionPrototype*>::iterator it = root.externFunctions.begin(); it != root.externFunctions.end(); it++) {
		(*it)->codeGen(*this);
	}

	ForwardDeclareFunctionVisitor globalfuncdec(this);
	for (std::vector<NFunction*>::iterator it = root.functions.begin(); it != root.functions.end(); it++) {
		globalfuncdec.visit(*it);
	}

    for(auto it = root.classes.begin(); it != root.classes.end(); it++) {

        
        for(auto function : (*it)->functions){
            globalfuncdec.visit(function);
        }
    }

	for (std::vector<NFunction*>::iterator it = root.functions.begin(); it != root.functions.end(); it++) {
		(*it)->codeGen(*this);
	}


    for(auto it = root.classes.begin(); it != root.classes.end(); it++) {
        for(auto function : (*it)->functions){
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


static Type* getBaseType(const std::string& name, const CodeGenContext& ctx)
{
	Type* retval = NULL;
	if(name.compare("void") == 0) {
		retval = Type::getVoidTy(getGlobalContext());
	} else if(name.compare("int") == 0){
		retval = Type::getInt32Ty(getGlobalContext());
	} else if(name.compare(0, 3, "int") == 0) {
		int width = atoi(name.substr(3).c_str());
		retval = Type::getIntNTy(getGlobalContext(), width);
	} else if(name.compare(0, 4, "uint") == 0) {
		int width = atoi(name.substr(4).c_str());
		retval = Type::getIntNTy(getGlobalContext(), width);
	} else if(name.compare("float") == 0) {
		retval = Type::getFloatTy(getGlobalContext());
	} else if(name.compare("bool") == 0) {
		retval = Type::getInt1Ty(getGlobalContext());
	} else {
		//class type
		NClassDeclaration* classDecl = ctx.getClass(name);
		if(classDecl != NULL) {
			retval = classDecl->getLLVMType(ctx);
		}
	}

	return retval;
}

Type* NType::getLLVMType(const CodeGenContext &context) const
{
	Type* retval = getBaseType(name, context);
	if(isArray) {
		retval = ArrayType::get(retval, size);
	} else {
		for(int i=0;i<numPointers;i++) {
			retval = PointerType::getUnqual(retval);
		}
	}

	return retval;
}



/* -- Code Generation -- */

Value* NFunctionPrototype::codeGen(CodeGenContext &context)
{

	std::vector<Type*> argTypes;
	std::vector<NArgument*>::iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
		argTypes.push_back((**it).type.getLLVMType(context));
	}
	FunctionType *ftype = FunctionType::get(returnType.getLLVMType(context), argTypes, isVarg);

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
	Value* v = context.getSymbol(name);
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
    Value* baseVal = base->codeGen(context);

    std::vector<Value*> indecies;
    indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
    indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
    indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), methodIndex, false));

    //Value* functionPtr = context.Builder.CreateGEP(baseVal, indecies);

    std::vector<Value*> args;
    ExpressionList::const_iterator it;

    //add 'this' as first argument
    args.push_back(baseVal);
    for (it = arguments.begin(); it != arguments.end(); it++) {
        args.push_back((**it).codeGen(context));
    }


    char funName[512];
    snprintf(funName, 512, "%s_%s", classType->name.c_str(), name.c_str());
    Function *function = context.module->getFunction(funName);


    return context.Builder.CreateCall(function, args);
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

	//rhsValue = context.Builder.CreateLoad(rhsValue);
	return context.Builder.CreateStore(rhsValue, lhsValue);
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
	AllocaInst *alloc = context.Builder.CreateAlloca(type->getLLVMType(context), 0, name.c_str());
	context.defineSymbol(name, alloc);
	if (assignmentExpr != NULL) {
		NAssignment assn(new NIdentifier(name), assignmentExpr);
		assn.codeGen(context);
	}
	return alloc;
}

Value* NReturn::codeGen(CodeGenContext &context)
{
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
		AllocaInst* alloc = CreateEntryBlockAlloca(llvmFunction, arg->type.getLLVMType(context), arg->name);
		context.defineSymbol(arg->name, alloc);
		context.Builder.CreateStore(AI, alloc);
	}
	
	block.codeGen(context);

	Instruction &last = *bblock->getInstList().rbegin();
	if(llvmFunction->getReturnType()->isVoidTy() && !last.isTerminator()){
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

    {
        AllocaInst *alloc = CreateEntryBlockAlloca(llvmFunction, PointerType::getUnqual(this->classType->type), "this");
        context.defineSymbol("this", alloc);
        context.Builder.CreateStore(llvmFunction->arg_begin(), alloc);
    }

    Function::arg_iterator AI = llvmFunction->arg_begin();
    for(size_t i=1,e=llvmFunction->arg_size();i!=e;++i,++AI){
        NArgument* arg = arguments[i];
        AllocaInst* alloc = CreateEntryBlockAlloca(llvmFunction, arg->type.getLLVMType(context), arg->name);
        context.defineSymbol(arg->name, alloc);
        context.Builder.CreateStore(AI, alloc);
    }

    block.codeGen(context);

    Instruction &last = *bblock->getInstList().rbegin();
    if(llvmFunction->getReturnType()->isVoidTy() && !last.isTerminator()){
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
	context.popBlock();


	context.pushBlock(elseBB);
	parent->getBasicBlockList().push_back(elseBB);
	context.Builder.SetInsertPoint(elseBB);
	if(elseBlock != NULL) {
		elseBlock->codeGen(context);
	}
	context.Builder.CreateBr(mergeBlock);
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

Value* NSizeOf::codeGen(CodeGenContext &context)
{
	NClassDeclaration* classDeclaration = context.getClass(id);
	Type* type = classDeclaration->getLLVMType(context);

	PointerType* pointerType = PointerType::getUnqual(type);
	Value* ptr = ConstantPointerNull::get(pointerType);
	Value* one = ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 1);

	Value* size = context.Builder.CreateGEP(ptr, one, "size");
	size = context.Builder.CreatePointerCast(size, IntegerType::getInt32Ty(getGlobalContext()));
	return size;
}

Value* NNew::codeGen(CodeGenContext& context)
{
	NClassDeclaration* classDeclaration = context.getClass(id);
	Type* type = classDeclaration->getLLVMType(context);

	PointerType* pointerType = PointerType::getUnqual(type);

	Value* ptr = ConstantPointerNull::get(pointerType);
	Value* one = ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 1);

	Value* size = context.Builder.CreateGEP(ptr, one, "size");
	size = context.Builder.CreatePointerCast(size, IntegerType::getInt32Ty(getGlobalContext()));

	Function* malloc = getMalloc(context);

	std::vector<Value*> args;
	args.push_back(size);

	Value* retval = context.Builder.CreateCall(malloc, args);

	retval = context.Builder.CreatePointerCast(retval, pointerType);
	return retval;
}