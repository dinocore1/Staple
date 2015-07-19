
#include <fstream>

#include "../compilercontext.h"
#include "typevisitor.h"
#include "../typehelper.h"
#include "../stringutils.h"

#include <llvm/Support/FileSystem.h>

#define path_sep '/'

using namespace std;
using namespace llvm;

namespace staple {

#define CONTAINS(x, container) (container.find(x) != container.end())


    void Pass1TypeVisitor::visit(NCompileUnit* compileUnit) {

        for(string import : compileUnit->includes) {
            string importPath = import;
            replace(importPath.begin(), importPath.end(), '.', path_sep);

            for (string searchPath : mContext->searchPaths) {
                if (sys::fs::is_directory(searchPath)) {

                    string srcFilePath = searchPath + path_sep + importPath + ".stp";
                    if (sys::fs::is_regular_file(srcFilePath)) {
                        ifstream inputFileStream(srcFilePath);
                        if (!inputFileStream) {
                            fprintf(stderr, "cannot open file: %s", srcFilePath.c_str());
                        } else if (!CONTAINS(srcFilePath, mContext->mFilesParsed)) {

                            ParserContext parserContext(&inputFileStream);
                            yyparse(&parserContext);

                            mContext->mFilesParsed.insert(srcFilePath);

                            size_t pos = import.find_last_of('.');
                            compileUnit->usingNamespaces.insert(import.substr(0, pos));

                            Pass1TypeVisitor pass1(mContext);
                            pass1.visit(parserContext.compileUnit);

                            Pass2TypeVisitor pass2(mContext);
                            pass2.visit(parserContext.compileUnit);

                        }
                        break;
                    }
                }
            }
        }

        for(NFunctionPrototype* functionPrototype : compileUnit->externFunctions) {
            const string fqFunctionName = functionPrototype->name;

            if(CONTAINS(fqFunctionName, mContext->mRootScope.table)) {
                mContext->logError(functionPrototype->location, "redefination of function '%s'", functionPrototype->name.c_str());
            } else {
                StapleFunction* function = new StapleFunction(fqFunctionName);
                mContext->mRootScope.table[fqFunctionName] = function;
                mFQFunctions.insert(fqFunctionName);
            }

        }

        for(NFunction* functionDecl : compileUnit->functions) {
            string fqFunctionName = !compileUnit->package.empty() ? (compileUnit->package + "." + functionDecl->name)
                                                           : functionDecl->name;

            if(CONTAINS(fqFunctionName, mContext->mRootScope.table)) {
                mContext->logError(functionDecl->location, "redefination of function '%s'", functionDecl->name.c_str());
            } else {
                StapleFunction* function = new StapleFunction(fqFunctionName);
                mContext->mRootScope.table[fqFunctionName] = function;
                mFQFunctions.insert(fqFunctionName);
            }
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;

            if(CONTAINS(fqClassName, mContext->mRootScope.table)) {
                mContext->logError(classDeclaration->location, "redefination of class '%s'", classDeclaration->name.c_str());
            } else {
                StapleClass *stpClass = new StapleClass(fqClassName);
                mContext->mRootScope.table[fqClassName] = stpClass;
                mFQClasses.insert(fqClassName);
            }
        }


    }

    ///// Pass2TypeVisitor ////

#define CheckType(type, location, name, positive) \
if(type == NULL) { \
    mContext->logError(location, "undefined type: '%s'", name.c_str()); \
} else { \
    positive \
}

    void Pass2TypeVisitor::visit(NField* field) {
        StapleType* stpType = getType(&field->type);
        CheckType(stpType, field->location, field->name,
                  StapleField* stpField = mCurrentClass->addField(field->name, stpType);
                  mType[field] = stpField;
                  if(mContext->mCompileUnit == mCompileUnit){
                      mContext->typeTable[field] = stpField;
                  }
        )
    }

    void Pass2TypeVisitor::visit(NArgument* argument) {
        StapleType* stpType = getType(&argument->type);
        if(stpType != nullptr) {
            mType[argument] = stpType;
            if (mContext->mCompileUnit == mCompileUnit) {
                mContext->typeTable[argument] = stpType;
            }
        }
    }

    void Pass2TypeVisitor::visit(NType* type) {
        StapleType* retType = getStapleType(type, mContext, mCompileUnit, mContext->mRootScope);
        mType[type] = retType;
        if(mContext->mCompileUnit == mCompileUnit){
            mContext->typeTable[type] = retType;
        }
    }

    void Pass2TypeVisitor::visit(NCompileUnit* compileUnit) {

        mCompileUnit = compileUnit;

        for(NFunctionPrototype* functionPrototype : compileUnit->externFunctions) {
            functionPrototype->accept(this);
        }

        for(NFunction* function : compileUnit->functions) {
            function->accept(this);
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);

            for(NField* field : classDeclaration->fields){
                field->accept(this);
            }

            for(NMethodFunction* methodFunction : classDeclaration->functions) {
                methodFunction->accept(this);
            }
        }

    }

    void Pass2TypeVisitor::visit(NFunctionPrototype* functionPrototype) {
        string fqFunctionName = functionPrototype->name;
        StapleFunction* function = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

        std::vector<StapleType*> args;
        for(NArgument* arg : functionPrototype->arguments) {
            StapleType* type = getType(arg);
            args.push_back(type);
        }
        function->setArgumentTypes(args, functionPrototype->isVarg);

        StapleType* returnType = getType(&functionPrototype->returnType);
        CheckType(returnType, functionPrototype->returnType.location, functionPrototype->returnType.name,
                  function->setReturnType(returnType);)

        mContext->typeTable[functionPrototype] = function;
    }

    void Pass2TypeVisitor::visit(NFunction* functionDecl) {
        string fqFunctionName = !mCompileUnit->package.empty() ? (mCompileUnit->package + "." + functionDecl->name)
                                                                             : functionDecl->name;
        StapleFunction* function = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

        std::vector<StapleType*> args;
        for(NArgument* arg : functionDecl->arguments) {
            StapleType* type = getType(arg);
            args.push_back(type);
        }

        function->setArgumentTypes(args, functionDecl->isVarg);

        StapleType* returnType = getType(&functionDecl->returnType);
        CheckType(returnType, functionDecl->returnType.location, functionDecl->returnType.name,
                  function->setReturnType(returnType);)

        mContext->typeTable[functionDecl] = function;
    }

    void Pass2TypeVisitor::visit(NMethodFunction* methodFunction) {

        std::vector<StapleType*> args;
        for(NArgument* arg : methodFunction->arguments){
            StapleType* type = getType(&arg->type);
            args.push_back(type);
        }

        StapleType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        StapleMethodFunction* stpMethod = mCurrentClass->addMethod(methodFunction->name, returnType, args, methodFunction->isVarg);

        if(mContext->mCompileUnit == mCompileUnit) {
            mContext->typeTable[methodFunction] = stpMethod;
        }
    }


    /////////// Pass3TypeVisitor ////////////

    Pass3TypeVisitor::Pass3TypeVisitor(CompilerContext *ctx)
    : mContext(ctx), scope(&ctx->mRootScope) { }

    void Pass3TypeVisitor::push() {
        scope = new Scope(scope);
    }

    void Pass3TypeVisitor::pop() {
        Scope* oldScope = scope;
        scope = scope->parent;
        delete oldScope;
    }

    void Pass3TypeVisitor::define(const string& name, StapleType* type) {
        scope->table[name] = type;
    }

    StapleType* Pass3TypeVisitor::resolve(const string& name) {
        return scope->get(name);
    }

    StapleType* Pass3TypeVisitor::getType(ASTNode* node) {
        node->accept(this);
        return mContext->typeTable[node];
    }

    void Pass3TypeVisitor::visit(NCompileUnit* compileUnit) {
        mCompileUnit = compileUnit;



        push();



        //collect symbols in used namespaces
        for(string namespaceName : mCompileUnit->usingNamespaces) {
            for(auto& entry : mContext->mRootScope.table) {
                if(strStartWith(entry.first, namespaceName)) {
                    size_t len = entry.first.length() - namespaceName.length()-1;
                    string shortName = entry.first.substr(namespaceName.length()+1, len);
                    define(shortName, entry.second);
                }
            }
        }

        //collect symbols in scope
        for(auto& entry : mContext->mRootScope.table) {
            if(strStartWith(entry.first, mCompileUnit->package)) {
                size_t len = entry.first.length() - mCompileUnit->package.length()-1;
                string shortName = entry.first.substr(mCompileUnit->package.length()+1, len);
                define(shortName, entry.second);
            }
        }

        for(NFunction* function : compileUnit->functions) {
            function->accept(this);
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);
            mContext->typeTable[classDeclaration] = mCurrentClass;

            for(NMethodFunction* methodFunction : classDeclaration->functions) {
                methodFunction->accept(this);
            }
        }

        pop();
    }

    void Pass3TypeVisitor::visit(NFunction* functionDecl) {

        string fqFunctionName = !mContext->mCompileUnit->package.empty() ? (mContext->mCompileUnit->package + "." + functionDecl->name)
                                                                         : functionDecl->name;
        mCurrentFunctionType = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

        push();

        for(NArgument* arg : functionDecl->arguments){
            StapleType* type = getType(arg);
            if(type != nullptr){
                define(arg->name, type);
            }
        }

        for(NStatement* statement : functionDecl->statements){
            statement->accept(this);
        }

        pop();
    }

    void Pass3TypeVisitor::visit(NArgument* argument) {
        StapleType* stpType = getType(&argument->type);
        if(stpType != nullptr) {
            mContext->typeTable[argument] = stpType;
        }
    }

    void Pass3TypeVisitor::visit(NType* type) {
        StapleType* rettype = getStapleType(type, mContext, mContext->mCompileUnit, *scope);
        CheckType(rettype, type->location, type->name,
                  mContext->typeTable[type] = rettype;
        )
    }

    void Pass3TypeVisitor::visit(NField* field) {
        StapleType* fieldType = getType(&field->type);
        CheckType(fieldType, field->location, field->type.name,
                  mContext->typeTable[field] = mCurrentClass->addField(field->name, fieldType);
        )
    }

    void Pass3TypeVisitor::visit(NExpressionStatement* expressionStatement) {
        mContext->typeTable[expressionStatement] = getType(expressionStatement->expression);
    }

    void Pass3TypeVisitor::visit(NMethodFunction* methodFunction) {
        push();

        StapleType* thisType = new StaplePointer(mCurrentClass);
        define("this", thisType);

        for(StapleField* field : mCurrentClass->getFields()){
            define(field->getName(), field);
        }

        for(NArgument* arg : methodFunction->arguments){
            StapleType* type = getType(&arg->type);

            CheckType(type, arg->location, arg->type.name,
                      define(arg->name, type);
                              mContext->typeTable[arg] = type;
            )
        }

        StapleType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        mCurrentFunctionType = cast<StapleFunction>(mContext->typeTable[methodFunction]);

        for(NStatement* statement : methodFunction->statements){
            statement->accept(this);
        }
        pop();
    }

    void Pass3TypeVisitor::visit(NReturn* returnexp) {
        StapleType* returnType = getType(returnexp->ret);
        if(!returnType->isAssignable(mCurrentFunctionType->getReturnType())) {
            mContext->logError(returnexp->location, "return type mismatch");
        }

        mContext->typeTable[returnexp] = StapleType::getVoidType();
    }

    void Pass3TypeVisitor::visit(NVariableDeclaration* variableDeclaration) {
        StapleType* type = getType(variableDeclaration->type);
        CheckType(type, variableDeclaration->location, variableDeclaration->type->name,
                  define(variableDeclaration->name, type);
                          mContext->typeTable[variableDeclaration] = type;
        )

        if(variableDeclaration->assignmentExpr != NULL) {
            variableDeclaration->assignmentExpr->accept(this);
            StapleType* rhs = mContext->typeTable[variableDeclaration->assignmentExpr];

            if(!rhs->isAssignable(type)) {
                mContext->logError(variableDeclaration->location, "cannot convert rhs to lhs");
            }
        }
    }

    void Pass3TypeVisitor::visit(NIntLiteral* intLiteral) {
        mContext->typeTable[intLiteral] = StapleType::getInt32Type();
    }

    void Pass3TypeVisitor::visit(NStringLiteral* literal) {
        mContext->typeTable[literal] = StapleType::getInt8PtrType();
    }

    void Pass3TypeVisitor::visit(NAssignment* assignment) {
        assignment->lhs->accept(this);
        assignment->rhs->accept(this);

        StapleType* lhsType = mContext->typeTable[assignment->lhs];
        StapleType* rhsType = mContext->typeTable[assignment->rhs];

        if(!rhsType->isAssignable(lhsType)){
            mContext->logError(assignment->location, "cannot convert rhs to lhs");
        }
    }

    void Pass3TypeVisitor::visit(NSizeOf* nsizeOf) {
        StapleType* type = getType(nsizeOf->type);

        if(type != NULL) {
            mContext->typeTable[nsizeOf] = StapleType::getInt32Type();
        }
    }

    void Pass3TypeVisitor::visit(NNew* newNode) {
        StapleType* type = getStapleType(newNode->id, mCompileUnit, mContext);

        if(StapleClass* classType = dyn_cast<StapleClass>(type)) {
            mContext->typeTable[newNode] = new StaplePointer(classType);
        } else {
            mContext->logError(newNode->location, "undefined class: '%s'", newNode->id.c_str());
        }
    }

    void Pass3TypeVisitor::visit(NIdentifier* identifier) {
        StapleType* type = scope->get(identifier->name);
        if(type == nullptr) {
            mContext->logError(identifier->location, "undeclaired identifier: '%s'", identifier->name.c_str());
        }
        mContext->typeTable[identifier] = type;
    }

    void Pass3TypeVisitor::visit(NArrayElementPtr* arrayElementPtr) {

        StapleType* baseType = getType(arrayElementPtr->base);
        if(StapleField* fieldType = dyn_cast<StapleField>(baseType)) {
            baseType = fieldType->getElementType();
        }

        if(StaplePointer* ptrType = dyn_cast<StaplePointer>(baseType)) {
            StapleType* exprType = getType(arrayElementPtr->expr);

            if(isa<StapleInt>(exprType)) {
                mContext->typeTable[arrayElementPtr] = ptrType->getElementType();
            } else {
                mContext->logError(arrayElementPtr->expr->location, "array index is not an integer");
            }
        } else if(StapleArray* arrayType = dyn_cast<StapleArray>(baseType)) {
            StapleType* exprType = getType(arrayElementPtr->expr);

            if(isa<StapleInt>(exprType)) {
                mContext->typeTable[arrayElementPtr] = baseType;
            } else {
                mContext->logError(arrayElementPtr->expr->location, "array index is not an integer");
            }
        } else {
            mContext->logError(arrayElementPtr->base->location, "not an array or pointer type");
        }

    }

    void Pass3TypeVisitor::visit(NMemberAccess* memberAccess) {

        StapleType* baseType = getType(memberAccess->base);
        if(StapleField* fieldType = dyn_cast<StapleField>(baseType)) {
            baseType = fieldType->getElementType();
        }

        StaplePointer* ptr = nullptr;
        StapleClass* classPtr = nullptr;

        if((ptr = dyn_cast<StaplePointer>(baseType)) && (classPtr = dyn_cast<StapleClass>(ptr->getElementType()))) {
            //memberAccess->base = new NLoad(memberAccess->base);
            //memberAccess->base->accept(this);
        } else if(!(classPtr = dyn_cast<StapleClass>(baseType))) {
            mContext->logError(memberAccess->base->location, "not a class type");
            return;
        }

        uint index = 0;
        StapleField* field = classPtr->getField(memberAccess->field, index);
        if(field != nullptr){
            mContext->typeTable[memberAccess] = field->getElementType();
            memberAccess->fieldIndex = index;
        } else {
            mContext->logError(memberAccess->location, "class '%s' does not have field named: '%s'",
                                   classPtr->getClassName().c_str(),
                                   memberAccess->field.c_str());
        }

    }

    void Pass3TypeVisitor::visit(NIfStatement* ifStatement) {
        StapleType* conditionType = getType(ifStatement->condition);

        if(!isa<StapleInt>(conditionType)){
            mContext->logError(ifStatement->condition->location, "cannot evaluate condition");
        }

        ifStatement->thenBlock->accept(this);
        if(ifStatement->elseBlock != NULL) {
            ifStatement->elseBlock->accept(this);
        }
    }

    void Pass3TypeVisitor::visit(NForLoop* forLoop) {
        forLoop->init->accept(this);
        StapleType* conditionType = getType(forLoop->condition);
        if(!isa<StapleInt>(conditionType)){
            mContext->logError(forLoop->condition->location, "cannot evaluate condition");
        }
        forLoop->increment->accept(this);
        forLoop->loop->accept(this);
    }

    void Pass3TypeVisitor::visit(NBinaryOperator* binaryOperator) {
        binaryOperator->lhs->accept(this);
        binaryOperator->rhs->accept(this);

        StapleType* returnType = StapleType::getVoidType();

        switch(binaryOperator->op) {
            case NBinaryOperator::Operator::Equal:
            case NBinaryOperator::Operator::NotEqual:
            case NBinaryOperator::Operator::LessThan:
            case NBinaryOperator::Operator::LessThanEqual:
            case NBinaryOperator::Operator::GreaterThan:
            case NBinaryOperator::Operator::GreaterThanEqual:
                returnType = StapleType::getBoolType();
                break;

            case NBinaryOperator::Operator::Add:
            case NBinaryOperator::Operator::Sub:
            case NBinaryOperator::Operator::Mul:
            case NBinaryOperator::Operator::Div:
                returnType = mContext->typeTable[binaryOperator->lhs];
                break;
        }

        mContext->typeTable[binaryOperator] = returnType;
    }

    void Pass3TypeVisitor::visit(NMethodCall* methodCall) {

        StapleType* baseType = getType(methodCall->base);

        StaplePointer* ptr = nullptr;
        StapleClass* classPtr = nullptr;

        if((ptr = dyn_cast<StaplePointer>(baseType)) && (classPtr = dyn_cast<StapleClass>(ptr->getElementType()))) {
            //methodCall->base = new NLoad(methodCall->base);
            //methodCall->base->accept(this);
        } else if(!(classPtr = dyn_cast<StapleClass>(baseType))) {
            mContext->logError(methodCall->base->location, "not a class type");
            return;
        }

        int index = 0;
        StapleMethodFunction* method = classPtr->getMethod(methodCall->name, index);
        if(method != nullptr) {
            methodCall->methodIndex = index;

            int i = 0;
            for(auto arg : methodCall->arguments) {
                StapleType* argType = getType(arg);

                if(i < method->getArguments().size()) {
                    StapleType* definedArgType = method->getArguments()[i];
                    if(!argType->isAssignable(definedArgType)) {
                        mContext->logError(arg->location, "argument mismatch");
                    }
                }
                i++;
            }

            mContext->typeTable[methodCall] = method->getReturnType();
        } else {
            mContext->logError(methodCall->base->location, "class '%s' does not have method: '%s'",
                                   classPtr->getClassName().c_str(), methodCall->name.c_str());
        }
    }

    void Pass3TypeVisitor::visit(NFunctionCall* functionCall) {
        StapleType* type = resolve(functionCall->name);

        if(StapleFunction* function = dyn_cast<StapleFunction>(type)) {
            int i = 0;
            for(auto arg : functionCall->arguments) {
                StapleType* argType = getType(arg);

                if(i < function->getArguments().size()) {
                    StapleType* definedArgType = function->getArguments()[i];
                    if(!argType->isAssignable(definedArgType)) {
                        mContext->logError(arg->location, "argument mismatch");
                    }
                }
                i++;
            }

            mContext->typeTable[functionCall] = function->getReturnType();
        } else {
            mContext->logError(functionCall->location, "undefined function: '%s'", functionCall->name.c_str());
        }
    }

    void Pass3TypeVisitor::visit(NLoad* load) {
        StapleType* type = getType(load->expr);
        mContext->typeTable[load] = type;
    }

    void Pass3TypeVisitor::visit(NBlock* block) {
        push();
        for(NStatement* statement : block->statements){
            statement->accept(this);
        }
        pop();
    }

}