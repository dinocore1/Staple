#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node;
class IfStmt;
class Assign;
class Return;
class Block;
class Op;
class VarDecl;
class ArrayDecl;
class IntLiteral;

#define VISIT(x) virtual void visit(x*){};
class Visitor {
public:
	virtual ~Visitor() {}

	void visitChildren(Node* node);
	virtual void visit(Node* node);
	VISIT(Class)
	VISIT(Field)
	VISIT(Method)
	VISIT(IfStmt)
	VISIT(Assign)
	VISIT(Return)
	VISIT(Block)
	VISIT(Op)
	VISIT(IntLiteral)

};

#define ACCEPT void accept(Visitor* visitor) { visitor->visit(this); }

class Node {
public:

	enum Type {
		Unknown,
		VarDecl
	};

	YYLTYPE location;
	std::vector<Node*> children;

	virtual void accept(Visitor*) = 0;
	virtual Type getType() = 0;
};


class Field : public Node {
public:

	Field(const std::string& name)
	: mName(name) {

	}

	ACCEPT

	Type getType() {
		return Type::Unknown;
	}

	std::string mName;

};

class Method : public Node {
public:

	ACCEPT

	Type getType() {
		return Type::Unknown;
	}
};

class Class : public Node {
public:

	void addField(Field* field) {
		children.push_back(field);
	}

	void addMethod(Method* method) {
		children.push_back(method);
	}

	ACCEPT

	Type getType() {
		return Type::Unknown;
	}
};

class Type : public Node {
public:

};

class Param : public Node {
public:

};

class Stmt : public Node {
public:

	Type getType() {
		return Type::Unknown;
	}
};

class Expr : public Node {
public:
	Type getType() {
		return Type::Unknown;
	}
};

class IfStmt : public Stmt {
public:
	IfStmt(Expr* condition, Stmt* thenStmt, Stmt* elseStmt = NULL)
	 : mCondition(condition), mThenStmt(thenStmt), mElseStmt(elseStmt) {
		children.push_back(condition);
		children.push_back(thenStmt);
		children.push_back(elseStmt);
	}

	ACCEPT

	Expr* mCondition;
	Stmt* mThenStmt;
	Stmt* mElseStmt;
};

class Assign : public Stmt {
public:
	Assign(Expr* l, Expr* r)
	: mLeft(l), mRight(r) {
		children.push_back(l);
		children.push_back(r);
	}

	ACCEPT

	Expr* mLeft;
	Expr* mRight;
};

class StmtExpr : public Stmt {
public:
	StmtExpr(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT

	Expr* mExpr;
};

class Return : public Stmt {
public:
	Return(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT
	Expr* mExpr;
};

class Block : public Stmt {
public:
	Block(StmtList* stmts)
	 : mStmts(*stmts) {
		children.insert(children.end(), mStmts.begin(), mStmts.end());
	}

	ACCEPT
	StmtList mStmts;
};

class VarDecl : public Stmt {
public:
	VarDecl(const std::string& type, const std::string& name)
	 : mType(type), mName(name) { };

	 Type getType() {
	 	return Type::VarDecl;
	 }

	ACCEPT
	std::string mType;
	std::string mName;
};

class ArrayDecl : public VarDecl {
public:
	ArrayDecl(const std::string& type, const std::string& name, uint32_t size)
	 : VarDecl(type, name), mSize(size) { };

	 Type getType() {
	 	return Type::VarDecl;
	 }

	ACCEPT
	uint32_t mSize;
};

class Op : public Expr {
public:
	enum Type {
		ADD,
		SUB,
		MUL,
		DIV
	};

	Op(Type type, Expr* left, Expr* right)
	 : mOp(type), mLeft(left), mRight(right) {
		children.push_back(left);
		children.push_back(right);
	}

	ACCEPT
	Type mOp;
	Expr* mLeft;
	Expr* mRight;
};

class Not : public Expr {
public:
	Not(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT
	Expr* mExpr;
};

class Neg : public Expr {
public:
	Neg(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT
	Expr* mExpr;

};

class Call : public Expr {
public:
	Call(const std::string& name, ExprList* args)
	 : mName(name), mArgList(*args) {
		children.insert(children.end(), mArgList.begin(), mArgList.end());
	}

	ACCEPT
	const std::string mName;
	ExprList mArgList;
};

class Id : public Expr {
public:
	Id(const std::string& name)
	 : mName(name) {}

	ACCEPT
	const std::string mName;

};

class IntLiteral : public Expr {
public:
	IntLiteral(int64_t value)
	 : mValue(value) {}

	ACCEPT
	const int64_t mValue;
};

} // namespace staple

#endif // NODE_H_
