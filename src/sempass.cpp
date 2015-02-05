#include <cstdarg>

#include "node.h"
#include "codegen.h"
#include "type.h"

#include "sempass.h"

using namespace std;

static SType* getBaseType(const std::string& name)
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
    }

    if(retval != NULL) {
        return SType::get(retval);
    } else {
        return NULL;
    }
}

class Scope {
public:
    Scope* parent;
    map<string, SType*> table;

    SType* get(const string& name) {
        SType* retval = NULL;

        map<string, SType*>::iterator it;
        if((it = table.find(name)) != table.end()) {
            retval = it->second;
        } else if(parent != NULL) {
            retval = parent->get(name);
        }

        return retval;
    }
};


class TypeVisitor : public ASTVisitor {
public:
    SClassType *currentClass;
    Scope* scope;
    SemPass* sempass;

    TypeVisitor(SemPass* sempass)
    : scope(NULL)
    , sempass(sempass) {

    }

    void push() {
        Scope* newScope = new Scope();
        newScope->parent = scope;
        scope = newScope;
    }

    void pop() {
        Scope* oldScope = scope;
        scope = scope->parent;
        delete oldScope;
    }

    void define(const string& name, SType* type) {
        scope->table[name] = type;
    }

    SType* getType(ASTNode* node) {
        node->accept(this);
        return sempass->ctx.typeTable[node];
    }

#define CheckType(type, location, name, positive) \
if(type == NULL) { \
    sempass->logError(location, "undefined type: '%s'", name.c_str()); \
} else { \
    positive \
}

    SMethodType* getMethodType(SClassType* classType, NMethodFunction *methodFunction) {

        std::vector<SType*> args;

        for(vector<NArgument*>::iterator it = methodFunction->arguments.begin();it != methodFunction->arguments.end();it++){
            NArgument* arg = *it;
            SType* type = getType(&arg->type);

            CheckType(type, arg->location, arg->type.name, args.push_back(type); )
        }

        SType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        SMethodType* retval = new SMethodType(classType, returnType, args, methodFunction->isVarg);

        return retval;
    }

    virtual void visit(NCompileUnit* compileUnit) {

        currentClass = NULL;
        push();

        //first pass class declaration
        for(vector<NClassDeclaration*>::iterator it = compileUnit->classes.begin();it != compileUnit->classes.end();it++){
            NClassDeclaration* classDeclaration = *it;
            SClassType* classType = new SClassType(classDeclaration->name);
            define(classDeclaration->name, classType);
            sempass->ctx.defineClass(classType);
        }

        //first pass extern functions
        for(vector<NFunctionPrototype*>::iterator it = compileUnit->externFunctions.begin();it != compileUnit->externFunctions.end();it++) {
            NFunctionPrototype* functionPrototype = *it;

            std::vector<SType*> argsType;
            for(vector<NArgument*>::iterator it = functionPrototype->arguments.begin();it != functionPrototype->arguments.end();it++){
                NArgument* arg = *it;
                SType* type = getType(&arg->type);

                CheckType(type, arg->location, arg->name,
                        argsType.push_back(type);
                        sempass->ctx.typeTable[arg] = type;
                )
            }

            SType* returnType = getType(&functionPrototype->returnType);
            CheckType(returnType, functionPrototype->location, functionPrototype->returnType.name,
                    SFunctionType* functionType = new SFunctionType(returnType, argsType, functionPrototype->isVarg);
                    define(functionPrototype->name, functionType);
            )

        }

        //first pass global functions
        for(vector<NFunction *>::iterator it = compileUnit->functions.begin();it != compileUnit->functions.end();it++){
            NFunction* function = *it;

            std::vector<SType*> argsType;
            for(vector<NArgument*>::iterator it = function->arguments.begin();it != function->arguments.end();it++){
                NArgument* arg = *it;
                SType* type = getType(&arg->type);

                CheckType(type, arg->location, arg->name,
                        argsType.push_back(type);
                        sempass->ctx.typeTable[arg] = type;
                )
            }

            SType* returnType = getType(&function->returnType);
            CheckType(returnType, function->location, function->returnType.name,
                    SFunctionType* functionType = new SFunctionType(returnType, argsType, function->isVarg);
                    define(function->name, functionType);
            )
        }

        //second pass class fields and methods
        for(vector<NClassDeclaration*>::iterator it = compileUnit->classes.begin();it != compileUnit->classes.end();it++){
            NClassDeclaration* classDeclaration = *it;
            currentClass = (SClassType *) scope->get(classDeclaration->name);

            for(auto field=classDeclaration->fields.begin();field != classDeclaration->fields.end();field++) {
                (*field)->accept(this);
            }

            for(auto method=classDeclaration->functions.begin();method != classDeclaration->functions.end();method++) {
                SMethodType* functionType = getMethodType(currentClass, *method);
                currentClass->methods.push_back(make_pair((*method)->name, functionType));
                (*method)->classType = currentClass;
            }

            currentClass->createLLVMClass();

            for(auto method=classDeclaration->functions.begin();method != classDeclaration->functions.end();method++) {
                (*method)->accept(this);
            }

        }

        //second pass global functions
        for(vector<NFunction *>::iterator it = compileUnit->functions.begin();it != compileUnit->functions.end();it++){
            (*it)->accept(this);
        }



        currentClass = NULL;

        pop();
    }

    virtual void visit(NType* type) {
        SType* retval = getBaseType(type->name);
        if(retval == NULL){
            //might be class type
            retval = scope->get(type->name);
            if(retval == NULL || !retval->isClassTy()){
                sempass->logError(type->location, "unknown type: '%s'", type->name.c_str());
                sempass->ctx.typeTable[type] = NULL;
                return;
            }
        }
        if(type->isArray) {
            retval = SArrayType::get(retval, type->size);
        } else {
            for(int i=0;i<type->numPointers;i++) {
                retval = SPointerType::get(retval);
            }
        }
        sempass->ctx.typeTable[type] = retval;
    }

    virtual void visit(NField* field) {
        SType* fieldType = getType(&field->type);
        CheckType(fieldType, field->location, field->type.name,
                currentClass->fields.push_back(make_pair(field->name, fieldType));
                sempass->ctx.typeTable[field] = fieldType;
        )
    }

    virtual void visit(NExpressionStatement* expressionStatement) {
        expressionStatement->expression->accept(this);
    }

    virtual void visit(NMethodFunction* methodFunction) {
        push();

        SType* thisType = SPointerType::get(currentClass);
        define("this", thisType);

        for(auto field = currentClass->fields.begin();field != currentClass->fields.end();field++){
            define((*field).first, (*field).second);
        }

        for(vector<NArgument*>::iterator it = methodFunction->arguments.begin();it != methodFunction->arguments.end();it++){
            NArgument* arg = *it;
            SType* type = getType(&arg->type);

            CheckType(type, arg->location, arg->type.name,
                    define(arg->name, type);
                    sempass->ctx.typeTable[arg] = type;
            )
        }

        SType* returnType = getType(&methodFunction->returnType);
        CheckType(returnType, methodFunction->returnType.location, methodFunction->returnType.name, )

        for(vector<NStatement*>::iterator it = methodFunction->block.statements.begin();it != methodFunction->block.statements.end();it++){
            NStatement* statement = *it;
            statement->accept(this);
        }
        pop();
    }

    virtual void visit(NFunction* function) {
        push();
        for(vector<NArgument*>::iterator it = function->arguments.begin();it != function->arguments.end();it++){
            NArgument* arg = *it;
            SType* type = getType(&arg->type);

            CheckType(type, arg->location, arg->type.name,
                    define(arg->name, type);
                    sempass->ctx.typeTable[arg] = type;
            )
        }

        SType* returnType = getType(&function->returnType);
        CheckType(returnType, function->returnType.location, function->returnType.name, )

        for(vector<NStatement*>::iterator it = function->block.statements.begin();it != function->block.statements.end();it++){
            NStatement* statement = *it;
            statement->accept(this);
        }
        pop();
    }

    virtual void visit(NVariableDeclaration* variableDeclaration) {
        SType* type = getType(variableDeclaration->type);
        CheckType(type, variableDeclaration->location, variableDeclaration->type->name,
                define(variableDeclaration->name, type);
                sempass->ctx.typeTable[variableDeclaration] = type;
        )

        if(variableDeclaration->assignmentExpr != NULL) {
            variableDeclaration->assignmentExpr->accept(this);
            SType* rhs = sempass->ctx.typeTable[variableDeclaration->assignmentExpr];

            if(!rhs->isAssignable(type)) {
                sempass->logError(variableDeclaration->location, "cannot convert rhs to lhs");
            }
        }
    }

    virtual void visit(NIntLiteral* intLiteral) {
        sempass->ctx.typeTable[intLiteral] = SType::get(llvm::Type::getInt32Ty(getGlobalContext()));
    }

    virtual void visit(NStringLiteral* literal) {
        sempass->ctx.typeTable[literal] = SType::get(llvm::Type::getInt8PtrTy(getGlobalContext()));
    }

    virtual void visit(NAssignment* assignment) {
        assignment->lhs->accept(this);
        assignment->rhs->accept(this);

        SType* lhsType = sempass->ctx.typeTable[assignment->lhs];
        SType* rhsType = sempass->ctx.typeTable[assignment->rhs];

        if(!rhsType->isAssignable(lhsType)){
            sempass->logError(assignment->location, "cannot convert rhs to lhs");
        }
    }

    virtual void visit(NSizeOf* nsizeOf) {
        SType* type = scope->get(nsizeOf->id);
        if(type == NULL) {
            //its not a local var name, maybe its a primitive type
            type = getBaseType(nsizeOf->id);
        }

        if(type == NULL) {
            sempass->logError(nsizeOf->location, "undefined class: '%s'", nsizeOf->id.c_str());
        } else {
            sempass->ctx.typeTable[nsizeOf] = type;
        }
    }

    virtual void visit(NNew* newNode) {
        SType* type = scope->get(newNode->id);
        if(type == NULL || !type->isClassTy()) {
            sempass->logError(newNode->location, "undefined class: '%s'", newNode->id.c_str());
        } else {
            SClassType* classType = (SClassType*)type;
            sempass->ctx.typeTable[newNode] = SPointerType::get(classType);
        }
    }

    virtual void visit(NIdentifier* identifier) {
        SType* type = scope->get(identifier->name);
        sempass->ctx.typeTable[identifier] = type;
    }

    virtual void visit(NArrayElementPtr* arrayElementPtr) {

        arrayElementPtr->base->accept(this);
        SType* baseType = sempass->ctx.typeTable[arrayElementPtr->base];

        if(!baseType->isArrayTy()){
            sempass->logError(arrayElementPtr->location, "not an array type");
        } else {

            arrayElementPtr->expr->accept(this);
            SType* exprType = sempass->ctx.typeTable[arrayElementPtr->expr];

            if(!baseType->isIntTy()) {
                sempass->logError(arrayElementPtr->expr->location, "array index is not an integer");
            } else {
                sempass->ctx.typeTable[arrayElementPtr] = ((SArrayType *) baseType)->elementType;
            }
        }
    }

    virtual void visit(NMemberAccess* memberAccess) {

        memberAccess->base->accept(this);
        SType* baseType = sempass->ctx.typeTable[memberAccess->base];

        if(baseType == NULL || (!baseType->isClassTy() && !(baseType->isPointerTy() && ((SPointerType*)baseType)->elementType->isClassTy()))) {
            sempass->logError(memberAccess->base->location, "not a class type");
        } else {
            SClassType* classType = NULL;
            if(baseType->isClassTy()){
                classType = (SClassType*) baseType;
            } else {
                classType = (SClassType *) ((SPointerType*)baseType)->elementType;
            }

            int index = classType->getFieldIndex(memberAccess->field);
            if(index < 0) {
                sempass->logError(memberAccess->location, "class '%s' does not have field: '%s'", classType->name.c_str(), memberAccess->field.c_str());
            } else {
                memberAccess->fieldIndex = index + 1;
                sempass->ctx.typeTable[memberAccess] = classType->fields[index].second;
            }
        }
    }

    virtual void visit(NMethodCall* methodCall) {
        methodCall->base->accept(this);
        SType* baseType = sempass->ctx.typeTable[methodCall->base];

        if(baseType == NULL || (!baseType->isClassTy() && !(baseType->isPointerTy() && ((SPointerType*)baseType)->elementType->isClassTy()))) {
            sempass->logError(methodCall->base->location, "not a class type");
        } else {

            SClassType* classType = NULL;
            if(baseType->isClassTy()){
                classType = (SClassType*) baseType;
            } else {
                classType = (SClassType *) ((SPointerType*)baseType)->elementType;
            }

            int index = classType->getMethodIndex(methodCall->name);
            if(index < 0) {
                sempass->logError(methodCall->location, "class '%s' does not have method: '%s'", classType->name.c_str(), methodCall->name.c_str());
            } else {
                methodCall->methodIndex = index;
                methodCall->classType = classType;
                SFunctionType* method = classType->getMethod(methodCall->name);

                int i = 0;
                for(auto it=methodCall->arguments.begin();it!=methodCall->arguments.end();it++) {
                    (*it)->accept(this);
                    SType* argType = sempass->ctx.typeTable[*it];

                    if(i < method->arguments.size()) {
                        SType *definedType = method->arguments[i];
                        if (!argType->isAssignable(definedType)) {
                            sempass->logError((*it)->location, "argument mismatch");
                        }
                    }

                    i++;
                }

                sempass->ctx.typeTable[methodCall] = method->returnType;
            }


        }
    }

    virtual void visit(NFunctionCall* methodCall) {
        SType* type = scope->get(methodCall->name);
        if(type != NULL && type->isFunctionTy()){

            SFunctionType* function = (SFunctionType*) type;

            int i = 0;
            for(auto it=methodCall->arguments.begin();it!=methodCall->arguments.end();it++) {
                (*it)->accept(this);
                SType* argType = sempass->ctx.typeTable[*it];

                if(i < function->arguments.size()) {
                    SType *definedType = function->arguments[i];
                    if (!argType->isAssignable(definedType)) {
                        sempass->logError((*it)->location, "argument mismatch");
                    }
                }

                i++;
            }


            sempass->ctx.typeTable[methodCall] = function->returnType;
        } else {
            sempass->logError(methodCall->location, "undefined function: '%s'", methodCall->name.c_str());
        }


    }

    virtual void visit(NLoad* load) {
        load->expr->accept(this);
        sempass->ctx.typeTable[load] = sempass->ctx.typeTable[load->expr];
    }

    virtual void visit(NBlock* block) {
        push();
        for(vector<NStatement*>::iterator it = block->statements.begin();it != block->statements.end();it++){
            NStatement* statement = *it;
            statement->accept(this);
        }
        pop();
    }
};

SemPass::SemPass(CompilerContext& ctx)
: ctx(ctx)
, numErrors(0) {

}

bool SemPass::hasErrors() {
    return numErrors > 0;
}

void SemPass::doSemPass(NCompileUnit& root)
{
    TypeVisitor typeVisitor(this);
    root.accept(&typeVisitor);
}

void SemPass::logError(YYLTYPE location, const char *format, ...)
{
    numErrors++;
    va_list argptr;
    va_start(argptr, format);

    fprintf(stderr, "%s:%d:%d: ", location.filename, location.first_line, location.first_column);
    fprintf(stderr, "error: ");
    vfprintf(stderr, format, argptr);
    va_end(argptr);
}

void SemPass::logWarning(YYLTYPE location, const char *format, ...)
{
    va_list argptr;
    va_start(argptr, format);

    fprintf(stderr, "%s:%d:%d: ", location.filename, location.first_line, location.first_column);
    fprintf(stderr, "warning: ");
    vfprintf(stderr, format, argptr);
    va_end(argptr);
}




