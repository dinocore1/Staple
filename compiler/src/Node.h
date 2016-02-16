#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node;
class NCompileUnit;
class NFunction;
class NClass;
class NMethod;
class NField;
class NParam;
class NType;
class IfStmt;
class Assign;
class Return;
class Block;
class Op;
class NLocalVar;
class NArrayDecl;
class IntLiteral;

#define VISIT(x) virtual void visit(x*){};
class Visitor {
public:
	virtual ~Visitor() {}

	void visitChildren(Node* node);
	virtual void visit(Node* node);
	VISIT(NFunction)
	VISIT(NClass)
	VISIT(NField)
	VISIT(NMethod)
	VISIT(NParam)
	VISIT(NType)
	VISIT(NLocalVar)
	VISIT(NArrayDecl)
	VISIT(IfStmt)
	VISIT(Assign)
	VISIT(Return)
	VISIT(Block)
	VISIT(Op)
	VISIT(IntLiteral)

};

#define ACCEPT void accept(Visitor* visitor) { visitor->visit(this); }

enum TypeId {
	Function,
	Class,
	Field,
	Method,
	Unknown
};

class Node {
public:

	Node(TypeId type = TypeId::Unknown)
	: mType(type) {};

	YYLTYPE location;
	std::vector<Node*> children;

	void add(Node* child) {
		children.push_back(child);
	}

	virtual void accept(Visitor*) = 0;

	const TypeId mType;

	static inline bool classof(const Node* T) {
		return true;
	}
};

class NCompileUnit : public Node {
public:

	void setPackage(const FQPath& package) {
		mPackage = package;
	}

	FQPath mPackage;

	ACCEPT
};

class NFunction : public Node {
public:
	NFunction(const std::string& name, NType* returnType,
		ParamList* params, StmtList* stmtList = NULL)
	: Node(TypeId::Function), mName(name), mReturnType(returnType),
	mParams(params), mStmts(stmtList) { }

	std::string mName;
	NType* mReturnType;
	ParamList* mParams;
	StmtList* mStmts;

	ACCEPT

	static inline bool classof(const Node* T) {
			return T->mType == TypeId::Function;
	}

};

class NField : public Node {
public:

	NField(const std::string& name)
	: Node(TypeId::Field), mName(name) { }

	std::string mName;

	ACCEPT

	static inline bool classof(const Node* T) {
			return T->mType == TypeId::Field;
	}

};

class NMethod : public Node {
public:
	NMethod(const std::string& name)
	: Node(TypeId::Method), mName(name) {}

	std::string mName;

	ACCEPT

	static inline bool classof(const Node* T) {
			return T->mType == TypeId::Method;
	}
};

class NClass : public Node {
public:
	NClass(const std::string& name)
	: Node(TypeId::Class), mName(name) {}

	std::string mName;

	ACCEPT

	static inline bool classof(const Node* T) {
			return T->mType == TypeId::Class;
	}

};

class NType : public Node {
public:

	ACCEPT
};

class NParam : public Node {
public:
	NParam(const std::string& name, NType* type)
	: mName(name), mType(type) { }

	std::string mName;
	NType* mType;

	ACCEPT
};

class Stmt : public Node {
public:

};

class Expr : public Node {
public:

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

class NLocalVar : public Stmt {
public:
	NLocalVar(const std::string& name, NType* type)
	 : mName(name), mType(type) { };

	ACCEPT
	std::string mName;
	NType* mType;
};

class NArrayDecl : public NLocalVar {
public:
	NArrayDecl(const std::string& name, NType* type, uint32_t size)
	 : NLocalVar(name, type), mSize(size) { };

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
