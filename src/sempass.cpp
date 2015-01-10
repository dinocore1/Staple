#include "node.h"
#include "codegen.h"
#include "type.h"

#include "sempass.h"

using namespace std;

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
    std::map<ASTNode*, SType*> typeTable;
    Scope* scope;

    TypeVisitor() : scope(NULL) {

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

    SType* getBaseType(const std::string& name)
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

    SType* getType(const NType* type)
    {
        SType* retval = getBaseType(type->name);
        if(retval == NULL){
            //might be class type
            retval = scope->get(type->name);
            if(retval == NULL || !retval->isClassTy()){
                return NULL;
            }
        }
        if(type->isArray) {
            retval = SArrayType::get(retval, type->size);
        } else {
            for(int i=0;i<type->numPointers;i++) {
                retval = SPointerType::get(retval);
            }
        }

        return retval;
    }

    virtual void visit(NCompileUnit* compileUnit) {

        push();

        for(vector<NClassDeclaration*>::iterator it = compileUnit->classes.begin();it != compileUnit->classes.end();it++){
            NClassDeclaration* classDeclaration = *it;
            SClassType* classType = new SClassType(classDeclaration->name);
            define(classDeclaration->name, classType);
        }

        for(vector<NFunctionPrototype*>::iterator it = compileUnit->externFunctions.begin();it != compileUnit->externFunctions.end();it++) {
            NFunctionPrototype* functionPrototype = *it;

            //TODO: validate the function arguments and return type
            std::vector<SType*> argsType;
            for(vector<NArgument*>::iterator it = functionPrototype->arguments.begin();it != functionPrototype->arguments.end();it++){
                NArgument* arg = *it;
                SType* type = getType(&arg->type);

                argsType.push_back(type);
            }

            SType* returnType = getType(&functionPrototype->returnType);

            SFunctionType* functionType = new SFunctionType(returnType, argsType, functionPrototype->isVarg);
            define(functionPrototype->name, functionType);
        }

        for(vector<NFunction *>::iterator it = compileUnit->functions.begin();it != compileUnit->functions.end();it++){
            (*it)->accept(this);
        }

        pop();
    }

    virtual void visit(NFunction* function) {
        push();
        for(vector<NArgument*>::iterator it = function->arguments.begin();it != function->arguments.end();it++){
            NArgument* arg = *it;
            SType* type = getType(&arg->type);

            //TODO: check if valid type
            define(arg->name, type);
        }

        for(vector<NStatement*>::iterator it = function->block.statements.begin();it != function->block.statements.end();it++){
            NStatement* statement = *it;
            statement->accept(this);
        }
        pop();
    }

    virtual void visit(NVariableDeclaration* variableDeclaration) {

        SType* type = getType(variableDeclaration->type);
        //TODO: check if valid type

        define(variableDeclaration->name, type);
    }

    virtual void visit(NIntLiteral* intLiteral) {
        typeTable[intLiteral] = SType::get(llvm::Type::getInt32Ty(getGlobalContext()));
    }

    virtual void visit(NAssignment* assignment) {
        assignment->lhs->accept(this);
        assignment->rhs->accept(this);

        SType* lhsType = typeTable[assignment->lhs];
        SType* rhsType = typeTable[assignment->rhs];

        if(!rhsType->isAssignable(lhsType)){
            //TODO: report error
        }
    }

    virtual void visit(NIdentifier* identifier) {
        SType* type = scope->get(identifier->name);
        typeTable[identifier] = type;
    }

    virtual void visit(NArrayElementPtr* arrayElementPtr) {

        arrayElementPtr->base->accept(this);
        SType* baseType = typeTable[arrayElementPtr->base];

        if(!baseType->isArrayTy()){
            //TODO: report error
        } else {
            typeTable[arrayElementPtr] = ((SArrayType*)baseType)->elementType;
        }

        arrayElementPtr->expr->accept(this);
        SType* exprType = typeTable[arrayElementPtr->expr];

        if(!baseType->isIntTy()) {
            //TODO: report error
        }


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

void SemPass::doSemPass(NCompileUnit& root)
{
    TypeVisitor typeVisitor;
    root.accept(&typeVisitor);
}