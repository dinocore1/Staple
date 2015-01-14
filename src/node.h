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
class NClassDeclaration;
class NType;
class NField;
class NFunction;
class NCompileUnit;
class NAssignment;
class NArrayElementPtr;
class NIdentifier;
class NIntLiteral;
class NBlock;
class NArgument;
class NFunctionPrototype;
class NMemberAccess;
class NFunctionCall;
class NExpressionStatement;
class NStringLiteral;
class NNew;
class NSizeOf;
class NLoad;
class NMethodFunction;

#include "parser.hpp"

typedef std::vector<NStatement*> StatementList;
typedef std::vector<NExpression*> ExpressionList;
typedef std::vector<NVariableDeclaration*> VariableList;

using namespace llvm;

class ASTNode {
public:
    YYLTYPE location;
    std::vector<ASTNode*> children;
    virtual ~ASTNode() {}
    virtual void accept(ASTVisitor* visitor) {}

};


#define ACCEPT virtual void accept(ASTVisitor* visitor) { visitor->visit(this); }
#define VISIT(x) virtual void visit(x* field) {}

class ASTVisitor {
public:
    virtual ~ASTVisitor() {}
    void visitChildren(ASTNode* node) {
        for(std::vector<ASTNode*>::iterator it = node->children.begin(); it != node->children.end(); it++) {
            (*it)->accept(this);
        }
    }
    VISIT(ASTNode)
    VISIT(NField)
    VISIT(NClassDeclaration)
    VISIT(NType)
    VISIT(NFunction)
    VISIT(NCompileUnit)
    VISIT(NVariableDeclaration)
    VISIT(NAssignment)
    VISIT(NArrayElementPtr)
    VISIT(NIdentifier)
    VISIT(NIntLiteral)
    VISIT(NStringLiteral)
    VISIT(NMemberAccess)
    VISIT(NFunctionCall)
    VISIT(NExpressionStatement)
    VISIT(NNew)
    VISIT(NSizeOf)
    VISIT(NLoad)
    VISIT(NMethodFunction)
};


class NType : public ASTNode {
public:
    std::string name;
    bool isArray;
    union {
        int numPointers;
        int size;
    };

    ACCEPT

    static NType* GetPointerType(const std::string& name, int numPtrs);
    static NType* GetArrayType(const std::string& name, int size);

    llvm::Type* getLLVMType(const CodeGenContext &context) const;
};

class NField : public ASTNode {
public:
    ACCEPT
    const std::string name;
    const NType type;

    NField(const NType& type, const std::string& name)
    : type(type), name(name)
    {}

};

class ClassMemberVisitor : public ASTVisitor {
public:
    std::vector<NField*>* fields;
    std::vector<NMethodFunction*>* functions;

    virtual void visit(NField* field) {
        fields->push_back(field);
    }

    virtual void visit(NMethodFunction* function) {
        functions->push_back(function);
    }

};

class NClassDeclaration : public ASTNode {
private:
    StructType* structType;
public:
    ACCEPT
    const std::string name;
    std::vector<NField*> fields;
    std::vector<NMethodFunction*> functions;

    NClassDeclaration(const std::string& name,
            const std::vector<NField*>& fields,
            const std::vector<NMethodFunction*>& functions)
    : structType(NULL), name(name), fields(fields), functions(functions)
    {}

    NClassDeclaration(const std::string& name, ASTNode* members)
    : structType(NULL), name(name)
    {
        ClassMemberVisitor collector;
        collector.fields = &fields;
        collector.functions = &functions;

        collector.visitChildren(members);
    }

    Type* getLLVMType(const CodeGenContext &context) {
        if(structType == NULL) {
            std::vector<Type *> typeFields;
            for (int i = 0; i < fields.size(); i++) {
                typeFields.push_back(fields[i]->type.getLLVMType(context));
            }
            structType = StructType::create(typeFields, name);
        }
        return structType;
    }
};



class NExpression : public ASTNode {
public:
    virtual llvm::Value* codeGen(CodeGenContext& context) = 0;
};


class NStatement : public NExpression {
};

class NLiteral : public NExpression {
public:
    const std::string str;

    NLiteral(const std::string& str)
    : str(str) {}
};

class NIntLiteral : public NLiteral {
public:
    ACCEPT
    unsigned width;
    NIntLiteral(std::string const &str, unsigned width = 32)
    : NLiteral(str), width(width) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NFloatLiteral : public NLiteral {
public:
    ACCEPT
    NFloatLiteral(std::string const &str) : NLiteral(str) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NStringLiteral : public NLiteral {
public:
    ACCEPT
    NStringLiteral(std::string const &str) : NLiteral(str) {
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NIdentifier : public NExpression {
public:
    ACCEPT
    std::string name;
    NIdentifier(const std::string& name) : name(name) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NArgument : public ASTNode {
public:
    ACCEPT
    NType type;
    std::string name;

    NArgument(const NType& type)
     : type(type) {}

    NArgument(const NType& type, const std::string& name)
    : type(type), name(name) {}
};

class NFunctionCall : public NExpression {
public:
    ACCEPT
    std::string name;
    ExpressionList arguments;
    NFunctionCall(const std::string& name, ExpressionList& arguments)
    : name(name), arguments(arguments) { }
    NFunctionCall(const std::string& name)
    : name(name) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NMethodCall : public NExpression {
public:
    ACCEPT
    std::string name;
    ExpressionList arguments;
    NExpression* base;
    NMethodCall(NExpression* base, const std::string& name, const ExpressionList& arguments)
    : base(base), name(name), arguments(arguments) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NArrayElementPtr : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NExpression* expr;

    NArrayElementPtr(NExpression* id, NExpression* expr)
    : base(id), expr(expr) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NNew : public NExpression {
public:
    ACCEPT
    std::string id;

    NNew(const std::string& id)
    : id(id) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NSizeOf : public NExpression {
public:
    ACCEPT
    std::string id;

    NSizeOf(const std::string& id)
    : id(id) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NLoad : public NExpression {
public:
    ACCEPT
    NExpression* expr;
    NLoad(NExpression* expr)
    : expr(expr) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NMemberAccess : public NExpression {
public:
    ACCEPT
    NExpression* base;
    std::string field;
    NMemberAccess(NExpression* base, const std::string& field)
    : base(base), field(field) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);

    int fieldIndex;
};

class NNot : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NNot(NExpression* base)
    : base(base) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NNegitive : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NNegitive(NExpression* base)
    : base(base) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NBinaryOperator : public NExpression {
public:
    ACCEPT
    int op;
    NExpression* lhs;
    NExpression* rhs;
    NBinaryOperator(NExpression* lhs, int op, NExpression* rhs) :
        lhs(lhs), rhs(rhs), op(op) {}
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NReturn : public NStatement {
public:
    ACCEPT
    NExpression* ret;
    NReturn(NExpression* ret)
    : ret(ret) {}
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NAssignment : public NStatement {
public:
    ACCEPT
    NExpression* lhs;
    NExpression* rhs;
    NAssignment(NExpression* lhs, NExpression* rhs) :
        lhs(lhs), rhs(rhs) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NBlock : public NStatement {
public:
    ACCEPT
    StatementList statements;
    NBlock() { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NIfStatement : public NStatement {
public:
    ACCEPT
    NExpression* condition;
    NStatement* thenBlock;
    NStatement* elseBlock;

    NIfStatement(NExpression* condition, NStatement* thenBlock, NStatement* elseBlock)
    : condition(condition), thenBlock(thenBlock), elseBlock(elseBlock) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NExpressionStatement : public NStatement {
public:
    ACCEPT
    NExpression* expression;
    NExpressionStatement(NExpression* expression) :
        expression(expression) { }
    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NVariableDeclaration : public NStatement {
public:
    ACCEPT
    NType* type;
    std::string name;
    NExpression* assignmentExpr;
    NVariableDeclaration(NType* type, const std::string& name) :
        type(type), name(name) {}
    NVariableDeclaration(NType* type, const std::string& name, NExpression *assignmentExpr) :
        type(type), name(name), assignmentExpr(assignmentExpr) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NFunctionPrototype : public ASTNode {
public:
    ACCEPT
    const NType returnType;
    const std::string name;
    std::vector<NArgument*> arguments;
    const bool isVarg;

    NFunctionPrototype(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg) :
            returnType(type), name(name), arguments(arguments), isVarg(isVarg) {}

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NFunction : public NFunctionPrototype {
public:
    ACCEPT
    NBlock block;
    llvm::Function::LinkageTypes linkage;
    llvm::Function* llvmFunction;

    NFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const NBlock& block)
            : NFunctionPrototype(type, name, arguments, isVarg), block(block) {
        linkage = GlobalValue::ExternalLinkage;
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NMethodFunction : public NFunctionPrototype {
public:
    ACCEPT
    NBlock block;
    llvm::Function::LinkageTypes linkage;
    llvm::Function* llvmFunction;

    NMethodFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const NBlock& block)
            : NFunctionPrototype(type, name, arguments, isVarg), block(block) {
        linkage = GlobalValue::ExternalLinkage;
    }

    virtual llvm::Value* codeGen(CodeGenContext& context);
};

class NCompileUnit : public ASTNode {
public:
    ACCEPT
    std::vector<NClassDeclaration*> classes;
    std::vector<NFunction*> functions;
    std::vector<NFunctionPrototype*> externFunctions;

    NCompileUnit() {}

};