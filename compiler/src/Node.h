#ifndef NODE_H_
#define NODE_H_

namespace staple {

class IfStmt;
class Assign;
class Return;
class Block;

#define VISIT(x) virtual void visit(x*){};
class Visitor {
public:
	virtual ~Visitor() {}
	virtual void visit(Node* node);
	VISIT(IfStmt)
	VISIT(Assign)
	VISIT(Return)
	VISIT(Block)

};

#define ACCEPT void accept(Visitor* visitor) { visitor->visit(this); }

class Node {
public:
	YYLTYPE location;
	std::vector<Node*> children;

	virtual void accept(Visitor*) = 0;
};

class Stmt : public Node {
public:

};

class Expr : public Node {

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
	 : mStmts(stmts) {
		children.insert(children.end(), mStmts->begin(), mStmts->end());
	}

	ACCEPT
	StmtList* mStmts;
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
	const Type mOp;
	const Expr* mLeft;
	const Expr* mRight;
};

class Not : public Expr {
public:
	Not(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT
	const Expr* mExpr;
};

class Neg : public Expr {
public:
	Neg(Expr* expr)
	 : mExpr(expr) {
		children.push_back(expr);
	}

	ACCEPT
	const Expr* mExpr;

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
	IntLiteral(int value)
	 : mValue(value) {}

	ACCEPT
	const int mValue;
};

} // namespace staple

#endif // NODE_H_
