
#include "node.h"
#include "types/stapletype.h"

#include "sempass.h"
#include "typehelper.h"
#include "importpass.h"

namespace staple {

using namespace std;
using namespace llvm;

#define CheckType(type, location, name, positive) \
if(type == NULL) { \
    sempass->ctx->logError(location, "undefined type: '%s'", name.c_str()); \
} else { \
    positive \
}


class TypeVisitor : public ASTVisitor {
public:
    CompilerContext* mContext;
    StapleFunction* mCurrentFunctionType;
    StapleClass *mCurrentClass;
    Scope* scope;
    SemPass* sempass;

    TypeVisitor(SemPass* sempass)
    : mContext(sempass->ctx), sempass(sempass)
    {
        scope = &sempass->ctx->mRootScope;
    }

    using ASTVisitor::visit;

    void push() {
        scope = new Scope(scope);
    }

    void pop() {
        Scope* oldScope = scope;
        scope = scope->parent;
        delete oldScope;
    }

    void define(const string& name, StapleType* type) {
        scope->table[name] = type;
    }

    StapleType* resolve(const string& name) {
        return scope->get(name);
    }

    StapleType* getType(ASTNode* node) {
        node->accept(this);
        return sempass->ctx->typeTable[node];
    }



    virtual void visit(NCompileUnit* compileUnit) {

        Pass1ClassVisitor p1ClassVisitor(mContext);
        p1ClassVisitor.visit(compileUnit);

        Pass2ClassVisitor pass2ClassVisitor(mContext);
        pass2ClassVisitor.visit(compileUnit);

        push();

        for(string fqFunctionName : p1ClassVisitor.mFQFunctions) {
            StapleFunction* stapleFunction = cast<StapleFunction>(mContext->mRootScope.table[fqFunctionName]);

            string simpleName;
            size_t pos = fqFunctionName.find_last_of('.');
            if(pos == string::npos) {
                simpleName = fqFunctionName;
            } else {
                simpleName = fqFunctionName.substr(pos+1);
            }

            define(simpleName, stapleFunction);
        }

        for(string fqClassName : p1ClassVisitor.mFQFunctions) {
            StapleClass* stapleClass = cast<StapleClass>(mContext->mRootScope.table[fqClassName]);
            define(stapleClass->getSimpleName(), stapleClass);
        }

        for(NFunction* function : compileUnit->functions) {
            function->accept(this);
        }

        for(NClassDeclaration* classDeclaration : compileUnit->classes) {
            string fqClassName = !compileUnit->package.empty() ? (compileUnit->package + "." + classDeclaration->name) : classDeclaration->name;
            mCurrentClass = cast<StapleClass>(sempass->ctx->mRootScope.table[fqClassName]);

            for(NMethodFunction* methodFunction : classDeclaration->functions) {
                methodFunction->accept(this);
            }
        }

        pop();
    }

    virtual void visit(NFunction* functionDecl) {

        string fqFunctionName;
        if(functionDecl->name.compare("main") == 0){
            fqFunctionName = "main";
        } else {
            fqFunctionName = !mContext->mCompileUnit->package.empty() ? (mContext->mCompileUnit->package + "." + functionDecl->name)
                                                                      : functionDecl->name;
        }
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

    virtual void visit(NArgument* argument) override {
        StapleType* stpType = getType(&argument->type);
        if(stpType != nullptr) {
            mContext->typeTable[argument] = stpType;
        }
    }

    virtual void visit(NType* type) {
        StapleType* rettype = getStapleType(type, sempass->ctx, sempass->ctx->mCompileUnit, *scope);
        CheckType(rettype, type->location, type->name,
                  sempass->ctx->typeTable[type] = rettype;
        )
    }

    virtual void visit(NField* field) {
        StapleType* fieldType = getType(&field->type);
        CheckType(fieldType, field->location, field->type.name,
                sempass->ctx->typeTable[field] = mCurrentClass->addField(field->name, fieldType);
        )
    }

    virtual void visit(NExpressionStatement* expressionStatement) {
        sempass->ctx->typeTable[expressionStatement] = getType(expressionStatement->expression);
    }

    virtual void visit(NMethodFunction* methodFunction) {
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
                    sempass->ctx->typeTable[arg] = type;
            )
        }

        StapleType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        mCurrentFunctionType = cast<StapleFunction>(sempass->ctx->typeTable[methodFunction]);

        for(NStatement* statement : methodFunction->statements){
            statement->accept(this);
        }
        pop();
    }

    virtual void visit(NReturn* returnexp) {
        StapleType* returnType = getType(returnexp->ret);
        if(!returnType->isAssignable(mCurrentFunctionType->getReturnType())) {
            sempass->ctx->logError(returnexp->location, "return type mismatch");
        }

        sempass->ctx->typeTable[returnexp] = StapleType::getVoidType();
    }



    virtual void visit(NVariableDeclaration* variableDeclaration) {
        StapleType* type = getType(variableDeclaration->type);
        CheckType(type, variableDeclaration->location, variableDeclaration->type->name,
                define(variableDeclaration->name, type);
                sempass->ctx->typeTable[variableDeclaration] = type;
        )

        if(variableDeclaration->assignmentExpr != NULL) {
            variableDeclaration->assignmentExpr->accept(this);
            StapleType* rhs = sempass->ctx->typeTable[variableDeclaration->assignmentExpr];

            if(!rhs->isAssignable(type)) {
                sempass->ctx->logError(variableDeclaration->location, "cannot convert rhs to lhs");
            }
        }
    }

    virtual void visit(NIntLiteral* intLiteral) {
        sempass->ctx->typeTable[intLiteral] = StapleType::getInt32Type();
    }

    virtual void visit(NStringLiteral* literal) {
        sempass->ctx->typeTable[literal] = StapleType::getInt8PtrType();
    }

    virtual void visit(NAssignment* assignment) {
        assignment->lhs->accept(this);
        assignment->rhs->accept(this);

        StapleType* lhsType = sempass->ctx->typeTable[assignment->lhs];
        StapleType* rhsType = sempass->ctx->typeTable[assignment->rhs];

        if(!rhsType->isAssignable(lhsType)){
            sempass->ctx->logError(assignment->location, "cannot convert rhs to lhs");
        }
    }

    virtual void visit(NSizeOf* nsizeOf) {
        StapleType* type = getType(nsizeOf->type);

        if(type != NULL) {
            sempass->ctx->typeTable[nsizeOf] = StapleType::getInt32Type();
        }
    }

    virtual void visit(NNew* newNode) {
        StapleType* type = resolve(newNode->id);

        if(StapleClass* classType = dyn_cast<StapleClass>(type)) {
            sempass->ctx->typeTable[newNode] = new StaplePointer(classType);
        } else {
            sempass->ctx->logError(newNode->location, "undefined class: '%s'", newNode->id.c_str());
        }
    }

    virtual void visit(NIdentifier* identifier) {
        StapleType* type = scope->get(identifier->name);
        if(type == nullptr) {
            sempass->ctx->logError(identifier->location, "undeclaired identifier: '%s'", identifier->name.c_str());
        }
        sempass->ctx->typeTable[identifier] = type;
    }

    virtual void visit(NArrayElementPtr* arrayElementPtr) {

        StapleType* baseType = getType(arrayElementPtr->base);
        if(StapleField* fieldType = dyn_cast<StapleField>(baseType)) {
            baseType = fieldType->getElementType();
        }

        if(StaplePointer* ptrType = dyn_cast<StaplePointer>(baseType)) {
            StapleType* exprType = getType(arrayElementPtr->expr);

            if(isa<StapleInt>(exprType)) {
                sempass->ctx->typeTable[arrayElementPtr] = ptrType->getElementType();
            } else {
                sempass->ctx->logError(arrayElementPtr->expr->location, "array index is not an integer");
            }
        } else if(StapleArray* arrayType = dyn_cast<StapleArray>(baseType)) {
            StapleType* exprType = getType(arrayElementPtr->expr);

            if(isa<StapleInt>(exprType)) {
                sempass->ctx->typeTable[arrayElementPtr] = baseType;
            } else {
                sempass->ctx->logError(arrayElementPtr->expr->location, "array index is not an integer");
            }
        } else {
            sempass->ctx->logError(arrayElementPtr->base->location, "not an array or pointer type");
        }

    }

    virtual void visit(NMemberAccess* memberAccess) {

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
            sempass->ctx->logError(memberAccess->base->location, "not a class type");
            return;
        }

        uint index = 0;
        StapleField* field = classPtr->getField(memberAccess->field, index);
        if(field != nullptr){
            sempass->ctx->typeTable[memberAccess] = field->getElementType();
            memberAccess->fieldIndex = index;
        } else {
            sempass->ctx->logError(memberAccess->location, "class '%s' does not have field named: '%s'",
                              classPtr->getClassName().c_str(),
                              memberAccess->field.c_str());
        }

    }

    virtual void visit(NIfStatement* ifStatement) {
        StapleType* conditionType = getType(ifStatement->condition);

        if(!isa<StapleInt>(conditionType)){
            sempass->ctx->logError(ifStatement->condition->location, "cannot evaluate condition");
        }

        ifStatement->thenBlock->accept(this);
        if(ifStatement->elseBlock != NULL) {
            ifStatement->elseBlock->accept(this);
        }
    }

    void visit(NForLoop* forLoop) {
        forLoop->init->accept(this);
        StapleType* conditionType = getType(forLoop->condition);
        if(!isa<StapleInt>(conditionType)){
            sempass->ctx->logError(forLoop->condition->location, "cannot evaluate condition");
        }
        forLoop->increment->accept(this);
        forLoop->loop->accept(this);
    }

    virtual void visit(NBinaryOperator* binaryOperator) {
        binaryOperator->lhs->accept(this);
        binaryOperator->rhs->accept(this);

        StapleType* returnType = StapleType::getVoidType();

        switch(binaryOperator->op) {
            case TCEQ:
            case TCNE:
            case TCGT:
            case TCLT:
            case TCGE:
            case TCLE:
                returnType = StapleType::getBoolType();
                break;

            case TPLUS:
            case TMINUS:
            case TMUL:
            case TDIV:
                returnType = sempass->ctx->typeTable[binaryOperator->lhs];
                break;
        }

        sempass->ctx->typeTable[binaryOperator] = returnType;
    }

    virtual void visit(NMethodCall* methodCall) {

        StapleType* baseType = getType(methodCall->base);

        StaplePointer* ptr = nullptr;
        StapleClass* classPtr = nullptr;

        if((ptr = dyn_cast<StaplePointer>(baseType)) && (classPtr = dyn_cast<StapleClass>(ptr->getElementType()))) {
            //methodCall->base = new NLoad(methodCall->base);
            //methodCall->base->accept(this);
        } else if(!(classPtr = dyn_cast<StapleClass>(baseType))) {
            sempass->ctx->logError(methodCall->base->location, "not a class type");
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
                        sempass->ctx->logError(arg->location, "argument mismatch");
                    }
                }
                i++;
            }

            sempass->ctx->typeTable[methodCall] = method->getReturnType();
        } else {
            sempass->ctx->logError(methodCall->base->location, "class '%s' does not have method: '%s'",
                              classPtr->getClassName().c_str(), methodCall->name.c_str());
        }
    }

    virtual void visit(NFunctionCall* functionCall) {
        StapleType* type = scope->get(functionCall->name);

        if(StapleFunction* function = dyn_cast<StapleFunction>(type)) {
            int i = 0;
            for(auto arg : functionCall->arguments) {
                StapleType* argType = getType(arg);

                if(i < function->getArguments().size()) {
                    StapleType* definedArgType = function->getArguments()[i];
                    if(!argType->isAssignable(definedArgType)) {
                        sempass->ctx->logError(arg->location, "argument mismatch");
                    }
                }
                i++;
            }

            sempass->ctx->typeTable[functionCall] = function->getReturnType();
        } else {
            sempass->ctx->logError(functionCall->location, "undefined function: '%s'", functionCall->name.c_str());
        }
    }

    virtual void visit(NLoad* load) {
        StapleType* type = getType(load->expr);
        sempass->ctx->typeTable[load] = type;
    }

    virtual void visit(NBlock* block) {
        push();
        for(NStatement* statement : block->statements){
            statement->accept(this);
        }
        pop();
    }
};

SemPass::SemPass(CompilerContext* ctx)
: ctx(ctx) { }


void SemPass::doIt()
{

    TypeVisitor typeVisitor(this);
    ctx->mCompileUnit->accept(&typeVisitor);
}


StapleType* getStapleType(NType* type, CompilerContext* ctx, NCompileUnit* compileUnit, const Scope& scope) {
    const string name = type->name;

    StapleType* retval = NULL;
    if(name.compare("void") == 0) {
        retval = StapleType::getVoidType();
    } else if(name.compare("uint") == 0 || name.compare("int") == 0 || name.compare("int32") == 0){
        retval = StapleType::getInt32Type();
    } else if(name.compare("uint8") == 0 || name.compare("int8") == 0) {
        retval = StapleType::getInt8Type();
    } else if(name.compare("uint16") == 0 || name.compare("int16") == 0) {
        retval = StapleType::getInt16Type();
    } else if(name.compare("float") == 0 || name.compare("float32") == 0) {
        retval = StapleType::getFloat32Type();
    } else if(name.compare("bool") == 0) {
        retval = StapleType::getBoolType();
    } else if(name.compare("obj") == 0){
        retval = CompilerContext::getStpObjClass();

    } else {

        retval = scope.get(name);
        if(retval == nullptr || !isa<StapleClass>(retval)) {
            retval = resolveClassType(ctx, compileUnit, type->name);
            if(retval == nullptr) {
                return nullptr;
            }
        }
    }

    if(type->isArray) {
        retval = new StapleArray(retval, type->size);
    } else {
        for(int i=0;i<type->numPointers;i++) {
            retval = new StaplePointer(retval);
        }
    }
    return retval;
}

StapleClass* resolveClassType(CompilerContext* context, NCompileUnit *startingCompileUnit, const string &className) {

    StapleClass* retval = nullptr;
    StapleType* value;
    string fqClassName = startingCompileUnit->package + '.' + className;
    value = context->mRootScope.get(fqClassName);


    if(value != nullptr && (retval = dyn_cast<StapleClass>(value))) {
        return retval;
    }


    for(string include : context->mCompileUnit->includes) {

        size_t pos = include.find_last_of(className);
        if(pos != string::npos) {
            value = context->mRootScope.get(include);
            if(value != nullptr && (retval = dyn_cast<StapleClass>(value))) {
                return retval;
            }
            break;
        }
    }

    return nullptr;
}

}


