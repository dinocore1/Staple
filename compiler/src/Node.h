#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node {
public:
	YYLTYPE location;
};

class Expr : public Node {

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

private:
	Type mOp;
	Expr* mLeft;
	Expr* mRight;
};

class Not : public Expr {
public:
	Not(Expr* expr)
	 : mExpr(expr) {}

	Expr* mExpr;
};

class Neg : public Expr {
public:
	Neg(Expr* expr)
	 : mExpr(expr) {}

	Expr* mExpr;

};

class Call : public Expr {
public:
	Call(const std::string& name)
	 : mName(name) {}

	const std::string mName;
};

class Id : public Expr {
public:
	Id(const std::string& name)
	 : mName(name) {}

 std::string mName;

};

} // namespace staple

#endif // NODE_H_
