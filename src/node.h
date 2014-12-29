#include "llvm/IR/Verifier.h"
#include "llvm/IR/DerivedTypes.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/LLVMContext.h"
#include "llvm/IR/Module.h"
#include <iostream>
#include <vector>

class CodeGenContext;
class NStatement;
class NExpression;
class NVariableDeclaration;
class NFunctionDeclaration;
class NType;

typedef std::vector<NStatement*> StatementList;
typedef std::vector<NExpression*> ExpressionList;
typedef std::vector<NVariableDeclaration*> VariableList;

using namespace llvm;

class ASTNode {
public:
    virtual ~ASTNode() {}
    virtual llvm::Value* codeGen(CodeGenContext& context) { }
};

template<class T>
class ASTNodeList : public ASTNode {
public:
    std::vector<T> list;
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

};


class NClassDeclaration : public ASTNode {
public:
    const std::string name;
    std::vector<NField*> fields;

    NClassDeclaration(const std::string& name,
            const std::vector<NField*>& fields)
    : name(name), fields(fields)
    {}
};



class NExpression : public ASTNode {
};


class NStatement : public ASTNode {
};

class NInteger : public NExpression {
public:
    long long value;
    NInteger(long long value) : value(value) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NDouble : public NExpression {
public:
    double value;
    NDouble(double value) : value(value) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NIdentifier : public NExpression {
public:
    std::string name;
    NIdentifier(const std::string& name) : name(name) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class PrototypeNode : public ASTNode {
public:
    const NIdentifier type;
    const NIdentifier id;
    VariableList arguments;

    PrototypeNode(const NIdentifier& type, const NIdentifier& id,
            const VariableList& arguments) :
            type(type), id(id), arguments(arguments){}

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

class NReturn : public NExpression {
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

class NFunctionDeclaration : public NStatement {
public:
    const NType type;
    const std::string name;
    VariableList arguments;
    NBlock& block;
    NFunctionDeclaration(const NType& type, const std::string& name,
            const VariableList& arguments, NBlock& block) :
        type(type), name(name), arguments(arguments), block(block) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NCompileUnit : public ASTNode {
public:
    std::vector<NClassDeclaration*> classes;
    std::vector<NFunctionDeclaration*> functions;

    NCompileUnit() {}

};