#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node {
public:
	YYLTYPE location;
};

class Stmt : public Node {

};

class Expr : public Node {

};

class IfStmt : public Stmt {
public:
	IfStmt(Expr* condition, Stmt* thenStmt, Stmt* elseStmt = NULL)
	 : mCondition(condition), mThenStmt(thenStmt), mElseStmt(elseStmt) {}

	Expr* mCondition;
	Stmt* mThenStmt;
	Stmt* mElseStmt;
};

class Assign : public Stmt {
public:
	Assign(Expr* l, Expr* r)
	: mLeft(l), mRight(r) {}

  Expr* mLeft;
	Expr* mRight;
};

class StmtExpr : public Stmt {
public:
	StmtExpr(Expr* expr)
	 : mExpr(expr) {}

	Expr* mExpr;
};

class Return : public Stmt {
public:
	Return(Expr* expr)
	 : mExpr(expr) {}

	Expr* mExpr;
};

class Block : public Stmt {
public:
	Block(StmtList* stmts)
	 : mStmts(stmts) {}

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

	}

	const Type mOp;
	const Expr* mLeft;
	const Expr* mRight;
};

class Not : public Expr {
public:
	Not(Expr* expr)
	 : mExpr(expr) {}

	const Expr* mExpr;
};

class Neg : public Expr {
public:
	Neg(Expr* expr)
	 : mExpr(expr) {}

	const Expr* mExpr;

};

class Call : public Expr {
public:
	Call(const std::string& name, ExprList* args)
	 : mName(name), mArgList(*args) {}

	const std::string mName;
	ExprList mArgList;
};

class Id : public Expr {
public:
	Id(const std::string& name)
	 : mName(name) {}

 const std::string mName;

};

class IntLiteral : public Expr {
public:
	IntLiteral(int value)
	 : mValue(value) {}

 const int mValue;
};

} // namespace staple

#endif // NODE_H_
