#include "node.h"
#include "codegen.h"
#include "parser.hpp"

#include "sempass.h"

using namespace std;

class Scope {
public:
    Scope* parent;
    map<string, NType*> table;

    NType* get(const string& name) {
        NType* retval = NULL;

        map<string, NType*>::iterator it;
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
    map<ASTNode*, NType*> typeTable;
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

    void define(const string& name, NType* type) {
        scope->table[name] = type;
    }

    virtual void visit(NCompileUnit* compileUnit) {
        for(vector<NFunction *>::iterator it = compileUnit->functions.begin();it != compileUnit->functions.end();it++){
            (*it)->accept(this);
        }
    }

    virtual void visit(NFunction* function) {
        push();
        for(vector<NArgument*>::iterator it = function->arguments.begin();it != function->arguments.end();it++){
            NArgument* arg = *it;
            define(arg->name, &arg->type);
            typeTable[arg] = &arg->type;
        }

        for(vector<NStatement*>::iterator it = function->block.statements.begin();it != function->block.statements.end();it++){
            NStatement* statement = *it;
            statement->accept(this);
        }
        pop();
    }

    virtual void visit(NVariableDeclaration* variableDeclaration) {
        define(variableDeclaration->name, variableDeclaration->type);
        typeTable[variableDeclaration] = variableDeclaration->type;
    }

    virtual void visit(NAssignment* assignment) {
        assignment->lhs->accept(this);
        assignment->rhs->accept(this);

        NType* lhsType = typeTable[assignment->lhs];
        NType* rhsType = typeTable[assignment->rhs];

        //TODO ensure that rhs type is assignable to lhs
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