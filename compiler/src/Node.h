#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node;
class NCompileUnit;
class NFunction;
class NFunctionDecl;
class NClass;
class NMethod;
class NField;
class NParam;
class NType;
class NCall;
class NStmt;
class NIfStmt;
class Assign;
class Return;
class Block;
class NOperation;
class NLocalVar;
class NArrayDecl;
class NNot;
class NNeg;
class NSymbolRef;
class NArrayRef;
class NIntLiteral;

#define VISIT(x) virtual void visit(x*){};
class Visitor {
public:
  virtual ~Visitor() {}

  void visitChildren(Node* node);
  virtual void visit(Node* node);
  VISIT(NCompileUnit)
  VISIT(NFunction)
  VISIT(NFunctionDecl)
  VISIT(NClass)
  VISIT(NField)
  VISIT(NMethod)
  VISIT(NParam)
  VISIT(NType)
  VISIT(NCall)
  VISIT(NLocalVar)
  VISIT(NArrayDecl)
  VISIT(NIfStmt)
  VISIT(Assign)
  VISIT(Return)
  VISIT(Block)
  VISIT(NOperation)
  VISIT(NSymbolRef)
  VISIT(NArrayRef)
  VISIT(NIntLiteral)

};

#define ACCEPT void accept(Visitor* visitor) { visitor->visit(this); }

enum TypeId {
  Function,
  FunctionDecl,
  Class,
  Field,
  Method,
  Block,
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

class NFunctionDecl : public Node {
public:
  NFunctionDecl(const std::string& name, NType* returnType,
                ParamList* params)
    : Node(TypeId::FunctionDecl), mName(name), mReturnType(returnType),
      mParams(params) { }

  std::string mName;
  NType* mReturnType;
  ParamList* mParams;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::FunctionDecl;
  }
};

class NFunction : public Node {
public:
  NFunction(const std::string& name, NType* returnType,
            ParamList* params, StmtList* stmtList)
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
  NType(const FQPath& name)
    : mTypeName(name) { }

  FQPath mTypeName;
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

class NStmt : public Node {
public:

  NStmt() {}
  NStmt(TypeId type)
    : Node(type) {}

  static inline bool classof(const Node* T) {
    return true;
  }
};

class Expr : public Node {
public:

};

class NIfStmt : public NStmt {
public:
  NIfStmt(Expr* condition, NStmt* thenStmt, NStmt* elseStmt = nullptr)
    : mCondition(condition), mThenStmt(thenStmt), mElseStmt(elseStmt) {
    add(condition);
    add(thenStmt);
    if(elseStmt != nullptr) {
      add(elseStmt);
    }
  }

  ACCEPT

  Expr* mCondition;
  NStmt* mThenStmt;
  NStmt* mElseStmt;
};

class Assign : public NStmt {
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

class StmtExpr : public NStmt {
public:
  StmtExpr(Expr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT

  Expr* mExpr;
};

class Return : public NStmt {
public:
  Return(Expr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  Expr* mExpr;
};

class NBlock : public NStmt {
public:
  StmtList mStmts;

  NBlock(StmtList* stmts);

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::Block;
  }
};

class NLocalVar : public NStmt {
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

class NOperation : public Expr {
public:
  enum Type {
    ADD,
    SUB,
    MUL,
    DIV,
    CMPEQ,
    CMPNE,
    CMPLT,
    CMPLE,
    CMPGT,
    CMPGE
  };

  NOperation(Type type, Expr* left, Expr* right)
    : mOp(type), mLeft(left), mRight(right) {
    add(left);
    add(right);
  }

  ACCEPT
  Type mOp;
  Expr* mLeft;
  Expr* mRight;
};

class NNot : public Expr {
public:
  NNot(Expr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  Expr* mExpr;
};

class NNeg : public Expr {
public:
  NNeg(Expr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  Expr* mExpr;

};

class NCall : public Expr {
public:
  NCall(const std::string& name, ExprList* args)
    : mName(name), mArgList(*args) {
    children.insert(children.end(), mArgList.begin(), mArgList.end());
  }

  ACCEPT
  const std::string mName;
  ExprList mArgList;
};

class NSymbolRef : public Expr {
public:
  NSymbolRef(const std::string& name)
    : mName(name) {}

  ACCEPT
  const std::string mName;

};

class NArrayRef : public Expr {
public:
  NArrayRef(Expr* base, Expr* index)
  : mBase(base), mIndex(index) {}

  ACCEPT

  Expr* mBase;
  Expr* mIndex;
};

class NIntLiteral : public Expr {
public:
  NIntLiteral(int64_t value)
    : mValue(value) {}

  ACCEPT
  const int64_t mValue;
};

} // namespace staple

#endif // NODE_H_
