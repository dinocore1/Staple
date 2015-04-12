#ifndef SNODE_H_
#define SNODE_H_


#include <iostream>
#include <vector>

#include "parser.hpp"

namespace staple {

class CodeGenContext;

class ASTNode;
class ASTVisitor;

class NStatement;
class NExpression;
class NVariableDeclaration;
class NClassDeclaration;
class NType;
class NReturn;
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
class NMethodCall;
class NIfStatement;
class NBinaryOperator;

typedef std::vector<NStatement*> StatementList;
typedef std::vector<NExpression*> ExpressionList;
typedef std::vector<NVariableDeclaration*> VariableList;


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
    VISIT(NReturn)
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
    VISIT(NMethodCall)
    VISIT(NIfStatement)
    VISIT(NBinaryOperator)
    VISIT(NBlock)
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

};

class NField : public ASTNode {
public:
    ACCEPT
    const std::string name;
    NType type;

    NField(const NType& type, const std::string& name)
    : type(type), name(name)
    {}

};

class ClassMemberVisitor : public ASTVisitor {
public:
    std::vector<NField*>* fields;
    std::vector<NMethodFunction*>* functions;

    using ASTVisitor::visit;

    void visit(NField* field) {
        fields->push_back(field);
    }

    void visit(NMethodFunction* function) {
        functions->push_back(function);
    }

};

class NClassDeclaration : public ASTNode {

public:
    ACCEPT
    const std::string name;
    std::vector<NField*> fields;
    std::vector<NMethodFunction*> functions;

    NClassDeclaration(const std::string& name,
            const std::vector<NField*>& fields,
            const std::vector<NMethodFunction*>& functions)
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

};

class NFloatLiteral : public NLiteral {
public:
    ACCEPT
    NFloatLiteral(std::string const &str) : NLiteral(str) {
    }

};

class NStringLiteral : public NLiteral {
public:
    ACCEPT
    NStringLiteral(std::string const &str) : NLiteral(str) {
    }

};

class NIdentifier : public NExpression {
public:
    ACCEPT
    std::string name;
    NIdentifier(const std::string& name) : name(name) { }
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

};

class NMethodCall : public NExpression {
public:
    ACCEPT
    std::string name;
    ExpressionList arguments;
    NExpression* base;

    int methodIndex;


    NMethodCall(NExpression* base, const std::string& name, const ExpressionList& arguments)
    : base(base), name(name), arguments(arguments), methodIndex(-1) {}

};

class NArrayElementPtr : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NExpression* expr;

    NArrayElementPtr(NExpression* id, NExpression* expr)
    : base(id), expr(expr) {}


};

class NNew : public NExpression {
public:
    ACCEPT
    std::string id;

    NNew(const std::string& id)
    : id(id) {}


};

class NSizeOf : public NExpression {
public:
    ACCEPT
    NType* type;

    NSizeOf(NType* type)
    : type(type) {}


};

class NLoad : public NExpression {
public:
    ACCEPT
    NExpression* expr;
    NLoad(NExpression* expr)
    : expr(expr) {}

};

class NMemberAccess : public NExpression {
public:
    ACCEPT
    NExpression* base;
    std::string field;
    NMemberAccess(NExpression* base, const std::string& field)
    : base(base), field(field) {}


    int fieldIndex;
};

class NNot : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NNot(NExpression* base)
    : base(base) {}

};

class NNegitive : public NExpression {
public:
    ACCEPT
    NExpression* base;
    NNegitive(NExpression* base)
    : base(base) {}

};

class NBinaryOperator : public NExpression {
public:
    ACCEPT
    int op;
    NExpression* lhs;
    NExpression* rhs;
    NBinaryOperator(NExpression* lhs, int op, NExpression* rhs) :
        lhs(lhs), rhs(rhs), op(op) {}

};

class NReturn : public NStatement {
public:
    ACCEPT
    NExpression* ret;
    NReturn(NExpression* ret)
    : ret(ret) {}

};

class NAssignment : public NStatement {
public:
    ACCEPT
    NExpression* lhs;
    NExpression* rhs;
    NAssignment(NExpression* lhs, NExpression* rhs) :
        lhs(lhs), rhs(rhs) { }

};

class NBlock : public NStatement {
public:
    ACCEPT
    StatementList statements;
    NBlock() { }

};

class NIfStatement : public NStatement {
public:
    ACCEPT
    NExpression* condition;
    NStatement* thenBlock;
    NStatement* elseBlock;

    NIfStatement(NExpression* condition, NStatement* thenBlock, NStatement* elseBlock)
    : condition(condition), thenBlock(thenBlock), elseBlock(elseBlock) {}


};

class NExpressionStatement : public NStatement {
public:
    ACCEPT
    NExpression* expression;
    NExpressionStatement(NExpression* expression) :
        expression(expression) { }

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


};

class NFunctionPrototype : public ASTNode {
public:
    ACCEPT
    NType returnType;
    const std::string name;
    std::vector<NArgument*> arguments;
    const bool isVarg;

    NFunctionPrototype(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg) :
            returnType(type), name(name), arguments(arguments), isVarg(isVarg) {}


};

class NFunction : public NFunctionPrototype {
public:
    ACCEPT
    NBlock block;


    NFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const NBlock& block)
            : NFunctionPrototype(type, name, arguments, isVarg), block(block) {

    }


};

class NMethodFunction : public NFunctionPrototype {
public:
    ACCEPT
    NBlock block;


    NMethodFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const NBlock& block)
            : NFunctionPrototype(type, name, arguments, isVarg), block(block) { }


};

class NCompileUnit : public ASTNode {
public:
    ACCEPT
    std::vector<std::string> mIncludes;
    std::vector<NClassDeclaration*> classes;
    std::vector<NFunction*> functions;
    std::vector<NFunctionPrototype*> externFunctions;

    NCompileUnit() {}

};

} // namespace staple

#endif /* SNODE_H_ */