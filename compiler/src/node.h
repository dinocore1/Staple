#ifndef SNODE_H_
#define SNODE_H_


#include <iostream>
#include <vector>

#include "parsercontext.h"
#include "parser.hpp"

namespace staple {

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
    VISIT(NFunctionPrototype)
    VISIT(NForLoop)
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

class NExpression : public ASTNode {
};


class NStatement : public ASTNode {
};

class NClassDeclaration : public NStatement {

public:
    ACCEPT
    const std::string name;
    const std::string mExtends;
    std::vector<NField*> fields;
    std::vector<NMethodFunction*> functions;

    NClassDeclaration(const std::string& name,
            const std::vector<NField*>& fields,
            const std::vector<NMethodFunction*>& functions)
    : name(name), fields(fields), functions(functions)
    {}

    NClassDeclaration(const std::string& name, const std::string& extends, ASTNode* members)
    : name(name), mExtends(extends)
    {
        ClassMemberVisitor collector;
        collector.fields = &fields;
        collector.functions = &functions;

        collector.visitChildren(members);
    }

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

class NArgument : public NExpression {
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
    NBlock(const StatementList& list) : statements(list) { }

    static bool classof(const ASTNode *T) {
        return dynamic_cast<const NBlock*>(T);
    }

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

class NForLoop : public NStatement {
public:
    ACCEPT
    ASTNode* init;
    ASTNode* condition;
    ASTNode* increment;
    NStatement* loop;

    NForLoop(ASTNode* init, ASTNode* condition, ASTNode* increment, NStatement* loop)
            : init(init), condition(condition), increment(increment), loop(loop) {}
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

class NFunctionPrototype : public NStatement {
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
    StatementList statements;


    NFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const StatementList& statements)
            : NFunctionPrototype(type, name, arguments, isVarg), statements(statements) {

    }


};

class NMethodFunction : public NFunctionPrototype {
public:
    ACCEPT
    StatementList statements;


    NMethodFunction(const NType& type, const std::string& name,
            const std::vector<NArgument*>& arguments, bool isVarg,
            const StatementList& statements)
            : NFunctionPrototype(type, name, arguments, isVarg), statements(statements) { }


};

class NCompileUnit : public ASTNode {
public:
    ACCEPT
    std::string package;
    std::vector<std::string> includes;
    std::vector<NClassDeclaration*> classes;
    std::vector<NFunction*> functions;
    std::vector<NFunctionPrototype*> externFunctions;

    NCompileUnit() {}

};

} // namespace staple

#endif /* SNODE_H_ */