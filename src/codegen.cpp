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
			argTypes.push_back((**it).type.getLLVMType());
		}
		FunctionType *ftype = FunctionType::get(fun->returnType.getLLVMType(), argTypes, fun->isVarg);
		fun->llvmFunction = Function::Create(ftype, fun->linkage, fun->name.c_str(), context->module);

	}
};

/* Compile the AST into a module */
void CodeGenContext::generateCode(NCompileUnit& root)
{

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

Type* NType::getLLVMType() const
{
	Type* retval = 0;
	if(text.compare("void") == 0) {
		retval = Type::getVoidTy(getGlobalContext());
	} else if(text.compare("int") == 0){
		retval = Type::getInt32Ty(getGlobalContext());
	} else if(text.compare(0, 3, "int") == 0) {
		int width = atoi(text.substr(3).c_str());
		retval = Type::getIntNTy(getGlobalContext(), width);
	} else if(text.compare(0, 4, "uint") == 0) {
		int width = atoi(text.substr(4).c_str());
		retval = Type::getIntNTy(getGlobalContext(), width);
	} else if(text.compare("float") == 0) {
		retval = Type::getFloatTy(getGlobalContext());
	}

	if(isPointer) {
		retval = PointerType::getUnqual(retval);
	}

	return retval;
}



/* -- Code Generation -- */

Value* NFunctionPrototype::codeGen(CodeGenContext &context)
{

	std::vector<Type*> argTypes;
	std::vector<NArgument*>::iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
		argTypes.push_back((**it).type.getLLVMType());
	}
	FunctionType *ftype = FunctionType::get(returnType.getLLVMType(), argTypes, isVarg);

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
	if (context.locals().find(name) == context.locals().end()) {
		std::cerr << "undeclared variable " << name << std::endl;
		return NULL;
	}
	Value* v = context.locals()[name];
	return context.Builder.CreateLoad(v, name.c_str());
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

Value* NBinaryOperator::codeGen(CodeGenContext& context)
{
	Value *l = lhs.codeGen(context);
	Value *r = rhs.codeGen(context);


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
	if (context.locals().find(lhs.name) == context.locals().end()) {
		std::cerr << "undeclared variable " << lhs.name << std::endl;
		return NULL;
	}
	Value *r = rhs.codeGen(context);
	return context.Builder.CreateStore(r, context.locals()[lhs.name]);
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
	return expression.codeGen(context);
}

Value* NVariableDeclaration::codeGen(CodeGenContext& context)
{
	AllocaInst *alloc = context.Builder.CreateAlloca(type.getLLVMType(), 0, id.name.c_str());
	context.locals()[id.name] = alloc;
	if (assignmentExpr != NULL) {
		NAssignment assn(id, *assignmentExpr);
		assn.codeGen(context);
	}
	return alloc;
}

Value* NReturn::codeGen(CodeGenContext &context)
{
	Value* r = ret.codeGen(context);
	return context.Builder.CreateRet(r);
}

static AllocaInst* CreateEntryBlockAlloca(Function *TheFunction, Type* type, const std::string &VarName) {
	IRBuilder<> TmpB(&TheFunction->getEntryBlock(),
			TheFunction->getEntryBlock().begin());
	return TmpB.CreateAlloca(type, 0, VarName.c_str());
}

Value* NFunction::codeGen(CodeGenContext& context)
{
	BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", llvmFunction, 0);

	context.pushBlock(bblock);

	Function::arg_iterator AI = llvmFunction->arg_begin();
	for(size_t i=0,e=llvmFunction->arg_size();i!=e;++i,++AI){
		NArgument* arg = arguments[i];
		AllocaInst* alloc = CreateEntryBlockAlloca(llvmFunction, arg->type.getLLVMType(), arg->name);
		context.locals()[arg->name] = alloc;
		context.Builder.CreateStore(AI, alloc);
	}
	
	block.codeGen(context);

	context.fpm->run(*llvmFunction);

	context.popBlock();
	return llvmFunction;
}