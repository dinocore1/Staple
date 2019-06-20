#ifndef NODE_H_
#define NODE_H_

namespace staple {

class Node;
class NCompileUnit;
class NImport;
class NFunctionDecl;
class NExternFunctionDecl;
class NClassDecl;
class NFieldDecl;
class NMethodDecl;
class NParam;
class NType;
class NArrayType;
class NPointerType;
class NCall;
class NStmt;
class NIfStmt;
class NAssign;
class NReturn;
class NOperation;
class NLocalVar;
class NArrayDecl;
class NNot;
class NNeg;
class NSymbolRef;
class NFieldRef;
class NArrayRef;
class NIntLiteral;
class NStringLiteral;
class NLoad;

#define VISIT(x) virtual void visit(x*){};
class Visitor {
public:
  virtual ~Visitor() {}

  void visitChildren(Node* node);
  virtual void visit(Node*);
  VISIT(NCompileUnit)
  VISIT(NImport)
  VISIT(NFunctionDecl)
  VISIT(NExternFunctionDecl)
  VISIT(NClassDecl)
  VISIT(NFieldDecl)
  VISIT(NMethodDecl)
  VISIT(NParam)
  VISIT(NType)
  VISIT(NCall)
  VISIT(NLocalVar)
  VISIT(NArrayDecl)
  VISIT(NIfStmt)
  VISIT(NAssign)
  VISIT(NReturn)
  VISIT(NOperation)
  VISIT(NSymbolRef)
  VISIT(NFieldRef)
  VISIT(NArrayRef)
  VISIT(NIntLiteral)
  VISIT(NStringLiteral)
  VISIT(NLoad)
  VISIT(NBlock)

};

#define ACCEPT void accept(Visitor* visitor) { visitor->visit(this); }

enum TypeId {
  TypeNode,
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

  virtual ~Node() {}

  YYLTYPE location;
  std::vector<Node*> children;

  void add(Node* child) {
    children.push_back(child);
  }

  virtual void accept(Visitor* visitor) {
    visitor->visit(this);
  }

  const TypeId mType;

  static inline bool classof(const Node* T) {
    return true;
  }
};

class NImport : public Node {
public:
  NImport(const FQPath& path)
    : mPath(path) {}

  FQPath mPath;
  ACCEPT
};

class NCompileUnit : public Node {
public:

  void setPackage(const FQPath& package) {
    mPackage = package;
  }

  File mFile;
  FQPath mPackage;

  ACCEPT
};

class NExternFunctionDecl : public Node {
public:
  NExternFunctionDecl(const std::string& name, NType* returnType,
                      ParamList* params, bool isVarg)
    : Node(TypeId::FunctionDecl), mName(name), mReturnType(returnType),
      mParams(*params), mIsVarg(isVarg) { }

  std::string mName;
  NType* mReturnType;
  ParamList mParams;
  bool mIsVarg;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::FunctionDecl;
  }
};

class NFunctionDecl : public Node {
public:
  NFunctionDecl(const std::string& name, NType* returnType,
                ParamList* params, StmtList* stmtList)
    : Node(TypeId::Function), mName(name), mReturnType(returnType),
      mParams(*params), mStmts(stmtList) { }

  std::string mName;
  NType* mReturnType;
  ParamList mParams;
  StmtList* mStmts;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::Function;
  }

};

class NFieldDecl : public Node {
public:

  NFieldDecl(NType* type, const std::string& name)
    : Node(TypeId::Field), mType(type), mName(name) { }

  NType* mType;
  std::string mName;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::Field;
  }

};

class NMethodDecl : public Node {
public:
  NMethodDecl(const std::string& name)
    : Node(TypeId::Method), mName(name) {}

  std::string mName;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::Method;
  }
};

class NClassDecl : public Node {
public:
  NClassDecl(const std::string& name, Node* classparts);

  std::string mName;

  ACCEPT

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::Class;
  }

};

class NType : public Node {
public:

  enum VariantType {
    Named,
    Array,
    Pointer,
    Varg
  };

  NType(VariantType variant)
    : Node(TypeId::TypeNode), mVariant(variant) {}

  static inline bool classof(const Node* T) {
    return T->mType == TypeId::TypeNode;
  }

  VariantType mVariant;

  ACCEPT
};

class NNamedType : public NType {
public:
  NNamedType(const FQPath& path)
    : NType(VariantType::Named), mTypeName(path) {}

  FQPath mTypeName;

  static inline bool classof(const NType* T) {
    return T->mVariant == VariantType::Named;
  }
};

class NArrayType : public NType {
public:

  NArrayType(NType* baseType)
    : NType(NType::VariantType::Array), mBase(baseType) { }

  NType* mBase;

  static inline bool classof(const NType* T) {
    return T->mVariant == VariantType::Array;
  }
};

class NPointerType : public NType {
public:

  NPointerType(NType* baseType)
    : NType(NType::VariantType::Pointer), mBase(baseType) { }

  NType* mBase;

  static inline bool classof(const NType* T) {
    return T->mVariant == VariantType::Pointer;
  }
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

class NExpr : public Node {
public:

};

class NIfStmt : public NStmt {
public:
  NIfStmt(NExpr* condition, NStmt* thenStmt, NStmt* elseStmt = nullptr)
    : mCondition(condition), mThenStmt(thenStmt), mElseStmt(elseStmt) {
    add(condition);
    add(thenStmt);
    if(elseStmt != nullptr) {
      add(elseStmt);
    }
  }

  ACCEPT

  NExpr* mCondition;
  NStmt* mThenStmt;
  NStmt* mElseStmt;
};

class NAssign : public NStmt {
public:
  NAssign(NExpr* l, NExpr* r)
    : mLeft(l), mRight(r) {
    children.push_back(l);
    children.push_back(r);
  }

  ACCEPT

  NExpr* mLeft;
  NExpr* mRight;
};

class NStmtExpr : public NStmt {
public:
  NStmtExpr(NExpr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT

  NExpr* mExpr;
};

class NReturn : public NStmt {
public:
  NReturn(NExpr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  NExpr* mExpr;
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
    : mName(name), mType(type), mInitializer(nullptr) { };

  NLocalVar(const std::string& name, NType* type, NExpr* initializer)
    : mName(name), mType(type), mInitializer(initializer) {}

  ACCEPT
  std::string mName;
  NType* mType;
  NExpr* mInitializer;
};

class NArrayDecl : public NLocalVar {
public:
  NArrayDecl(const std::string& name, NType* type, uint32_t size)
    : NLocalVar(name, type), mSize(size) { };

  ACCEPT
  uint32_t mSize;
};

class NOperation : public NExpr {
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

  NOperation(Type type, NExpr* left, NExpr* right)
    : mOp(type), mLeft(left), mRight(right) {
    add(left);
    add(right);
  }

  ACCEPT
  Type mOp;
  NExpr* mLeft;
  NExpr* mRight;
};

class NNot : public NExpr {
public:
  NNot(NExpr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  NExpr* mExpr;
};

class NNeg : public NExpr {
public:
  NNeg(NExpr* expr)
    : mExpr(expr) {
    children.push_back(expr);
  }

  ACCEPT
  NExpr* mExpr;

};

class NCall : public NExpr {
public:
  NCall(const std::string& name, ExprList* args)
    : mName(name), mArgList(*args) {
    children.insert(children.end(), mArgList.begin(), mArgList.end());
  }

  ACCEPT
  const std::string mName;
  ExprList mArgList;
};

class NSymbolRef : public NExpr {
public:
  NSymbolRef(const std::string& name)
    : mName(name) {}

  ACCEPT
  const std::string mName;

};

class NFieldRef : public NExpr {
public:
  NFieldRef(NExpr* base, const std::string& field)
    : mBase(base), mField(field) {}

  ACCEPT
  NExpr* mBase;
  const std::string mField;
};

class NArrayRef : public NExpr {
public:
  NArrayRef(NExpr* base, NExpr* index)
    : mBase(base), mIndex(index) {}

  ACCEPT

  NExpr* mBase;
  NExpr* mIndex;
};

class NIntLiteral : public NExpr {
public:
  NIntLiteral(int64_t value)
    : mValue(value) {}

  ACCEPT
  const int64_t mValue;
};

class NStringLiteral : public NExpr {
public:
  NStringLiteral(const std::string& value)
    : mStr(value) {}

  ACCEPT
  const std::string mStr;
};

class NLoad : public NExpr {
public:
  NLoad(NExpr* expr)
    : mExpr(expr) {}

  ACCEPT
  NExpr* mExpr;
};

} // namespace staple

#endif // NODE_H_
