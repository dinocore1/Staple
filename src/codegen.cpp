#include "node.h"
#include "codegen.h"
#include "parser.hpp"

using namespace std;

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

	for (std::vector<NFunction*>::iterator it = root.functions.begin(); it != root.functions.end(); it++) {
		(*it)->codeGen(*this);
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
	//return context.Builder.CreateLoad(v, name.c_str());
}

Value* NMethodCall::codeGen(CodeGenContext& context)
{
	Function *function = context.module->getFunction(id.name.c_str());
	if (function == NULL) {
		std::cerr << "no such function " << id.name << std::endl;
	}
	std::vector<Value*> args;
	ExpressionList::const_iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
		args.push_back((**it).codeGen(context));
	}

	return context.Builder.CreateCall(function, args);
}

Value*NArrayElementPtr::codeGen(CodeGenContext &context)
{
	Value* idSymbol = id->codeGen(context);
	//Value* idSymbol = NLoad(id).codeGen(context);
	Value* exprVal = expr->codeGen(context);

	std::vector<Value*> indecies;

	Type* baseType = idSymbol->getType()->getPointerElementType();
	if(baseType->isArrayTy()) {
		indecies.push_back(ConstantInt::get(IntegerType::getInt32Ty(getGlobalContext()), 0, false));
	} else {
		idSymbol = NLoad(id).codeGen(context);
	}

	indecies.push_back(exprVal);

	return context.Builder.CreateGEP(idSymbol, indecies);
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

Value* NAssignment::codeGen(CodeGenContext& context)
{
	Value* lhsValue = lhs->codeGen(context);
	Value *rhsValue = rhs->codeGen(context);
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