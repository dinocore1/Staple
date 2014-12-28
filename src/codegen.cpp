#include "node.h"
#include "codegen.h"
#include "parser.hpp"

using namespace std;

void Error(const char* str)
{
	fprintf(stderr, "Error: %s\n", str);
}

/* Compile the AST into a module */
void CodeGenContext::generateCode(NBlock& root)
{
	/* Create the top level interpreter function to call as entry */
	ArrayRef<Type*> argTypes;
	FunctionType *ftype = FunctionType::get(Type::getVoidTy(getGlobalContext()), argTypes, false);
	mainFunction = Function::Create(ftype, GlobalValue::InternalLinkage, "main", module);
	BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", mainFunction);

	
	/* Push a new variable/block context */
	pushBlock(bblock);
	root.codeGen(*this); /* emit bytecode for the toplevel block */
	Builder.CreateRetVoid();
	popBlock();
	
	/* Print the bytecode in a human-readable format 
	   to see if our program compiled properly
	 */
	module->dump();
}

/* Returns an LLVM type based on the identifier */
static Type *typeOf(const NIdentifier& type)
{
	if (type.name.compare("int") == 0) {
		return Type::getInt64Ty(getGlobalContext());
	}
	else if (type.name.compare("double") == 0) {
		return Type::getDoubleTy(getGlobalContext());
	}
	return Type::getVoidTy(getGlobalContext());
}

/* -- Code Generation -- */

Value* NInteger::codeGen(CodeGenContext& context)
{
	return ConstantInt::get(getGlobalContext(), APInt(32, value, true));
}

Value* NDouble::codeGen(CodeGenContext& context)
{
	return ConstantFP::get(getGlobalContext(), APFloat(value));
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
	AllocaInst *alloc = context.Builder.CreateAlloca(typeOf(type), 0, id.name.c_str());
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

Value* NFunctionDeclaration::codeGen(CodeGenContext& context)
{
	vector<Type*> argTypes;
	VariableList::const_iterator it;
	for (it = arguments.begin(); it != arguments.end(); it++) {
		argTypes.push_back(typeOf((**it).type));
	}
	FunctionType *ftype = FunctionType::get(typeOf(type), argTypes, false);
	Function *function = Function::Create(ftype, GlobalValue::InternalLinkage, id.name.c_str(), context.module);
	BasicBlock *bblock = BasicBlock::Create(getGlobalContext(), "entry", function, 0);

	context.pushBlock(bblock);

	Function::arg_iterator AI = function->arg_begin();
	for(unsigned i=0,e=function->arg_size();i!=e;++i,++AI){
		NVariableDeclaration* arg = arguments[i];
		AllocaInst* alloc = CreateEntryBlockAlloca(function, typeOf(arg->type), arg->id.name);
		context.locals()[arg->id.name] = alloc;
		context.Builder.CreateStore(AI, alloc);
	}
	
	block.codeGen(context);

	context.fpm->run(*function);

	context.popBlock();
	return function;
}