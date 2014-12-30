#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include "llvm/IR/Function.h"
#include <iostream>
#include <vector>

class ASTNode;
class ASTVisitor;

class CodeGenContext;
class NStatement;
class NExpression;
class NVariableDeclaration;
class NFunctionDeclaration;
class NType;
class NField;
class NFunction;

typedef std::vector<NStatement*> StatementList;
typedef std::vector<NExpression*> ExpressionList;
typedef std::vector<NVariableDeclaration*> VariableList;

using namespace llvm;

class ASTNode {
public:
    std::vector<ASTNode*> children;
    virtual ~ASTNode() {}
    virtual void accept(ASTVisitor* visitor) {};
    virtual llvm::Value* codeGen(CodeGenContext& context) { }
};

class ASTVisitor {
public:
    virtual ~ASTVisitor() {}
    virtual void visit(ASTNode* node) {}
    void visitChildren(ASTNode* node) {
        for(std::vector<ASTNode*>::iterator it = node->children.begin(); it != node->children.end(); it++) {
            (*it)->accept(this);
        }
    }
    virtual void visit(NField* field) {}
    virtual void visit(NType* type) {}
    virtual void visit(NFunction* func) {}
};


class NType : public ASTNode {
public:
    std::string text;
    bool isPointer;
    NType(const std::string& text, bool isPointer)
            : text(text), isPointer(isPointer) { }

    llvm::Type* getLLVMType() const;
};

class NField : public ASTNode {
public:
    const std::string name;
    const NType type;

    NField(const NType& type, const std::string& name)
    : type(type), name(name)
    {}

    virtual void accept(ASTVisitor* visitor) { visitor->visit(this); };

};

class ClassMemberVisitor : public ASTVisitor {
public:
    std::vector<NField*>* fields;
    std::vector<NFunction*>* functions;

    virtual void visit(NField* field) {
        fields->push_back(field);
    }

    virtual void visit(NFunction* function) {
        functions->push_back(function);
    }

};

class NClassDeclaration : public ASTNode {
public:
    const std::string name;
    std::vector<NField*> fields;
    std::vector<NFunction*> functions;

    NClassDeclaration(const std::string& name,
            const std::vector<NField*>& fields,
            const std::vector<NFunction*>& functions)
    : name(name), fields(fields), functions(functions)
    {}

    NClassDeclaration(const std::string& name, ASTNode* members)
    : name(name)
    {
        ClassMemberVisitor collector;
        collector.fields = &fields;
        collector.functions = &functions;

        collector.visitChildren(members);
    }
};



class NExpression : public ASTNode {
};


class NStatement : public ASTNode {
};

class NLiteral : public NExpression {
public:
    const std::string str;

    NLiteral(const std::string& str)
    : str(str) {}
};

class NIntLiteral : public NLiteral {
public:
    unsigned width;
    NIntLiteral(std::string const &str, unsigned width = 32)
    : NLiteral(str), width(width) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NFloatLiteral : public NLiteral {
public:
    NFloatLiteral(std::string const &str) : NLiteral(str) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NStringLiteral : public NLiteral {
public:
    NStringLiteral(std::string const &str) : NLiteral(str) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NIdentifier : public NExpression {
public:
    std::string name;
    NIdentifier(const std::string& name) : name(name) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NArgument : public ASTNode {
public:
    const NType type;
    std::string name;

    NArgument(const NType& type)
     : type(type) {}

    NArgument(const NType& type, const std::string& name)
    : type(type), name(name) {}
};

class NFunctionPrototype : public ASTNode {
public:
    const NType returnType;
    const std::string name;
    std::vector<NArgument*> arguments;
    const bool isVarg;

    NFunctionPrototype(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg) :
            returnType(type), name(name), arguments(arguments), isVarg(isVarg) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NMethodCall : public NExpression {
public:
    const NIdentifier& id;
    ExpressionList arguments;
    NMethodCall(const NIdentifier& id, ExpressionList& arguments) :
        id(id), arguments(arguments) { }
    NMethodCall(const NIdentifier& id) : id(id) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NBinaryOperator : public NExpression {
public:
    int op;
    NExpression& lhs;
    NExpression& rhs;
    NBinaryOperator(NExpression& lhs, int op, NExpression& rhs) :
        lhs(lhs), rhs(rhs), op(op) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NReturn : public NStatement {
public:
    NExpression& ret;
    NReturn(NExpression& ret)
    : ret(ret) {}
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NAssignment : public NExpression {
public:
    NIdentifier& lhs;
    NExpression& rhs;
    NAssignment(NIdentifier& lhs, NExpression& rhs) : 
        lhs(lhs), rhs(rhs) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NBlock : public NExpression {
public:
    StatementList statements;
    NBlock() { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NExpressionStatement : public NStatement {
public:
    NExpression& expression;
    NExpressionStatement(NExpression& expression) : 
        expression(expression) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NVariableDeclaration : public NStatement {
public:
    const NType type;
    NIdentifier id;
    NExpression *assignmentExpr;
    NVariableDeclaration(const NType& type, const NIdentifier& id) :
        type(type), id(id) { }
    NVariableDeclaration(const NType& type, const NIdentifier& id, NExpression *assignmentExpr) :
        type(type), id(id), assignmentExpr(assignmentExpr) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NFunction : public ASTNode {
public:
    const NType returnType;
    const std::string name;
    std::vector<NArgument*> arguments;
    const bool isVarg;
    NBlock block;
    llvm::Function::LinkageTypes linkage;
    llvm::Function* llvmFunction;

    NFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const NBlock& block)
            : returnType(type), name(name), arguments(arguments),
              isVarg(isVarg), block(block) {
        linkage = GlobalValue::ExternalLinkage;
    }

    virtual void accept(ASTVisitor* visitor) { visitor->visit(this); };

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NCompileUnit : public ASTNode {
public:
    std::vector<NClassDeclaration*> classes;
    std::vector<NFunction*> functions;
    std::vector<NFunctionPrototype*> externFunctions;

    NCompileUnit() {}

};