#ifndef SEMPASS_H_
#define SEMPASS_H_

namespace staple {

class Sempass {
public:
  Sempass(CompilerContext*);
  void doit();

  CompilerContext* mCtx;
};

/**
 * Semantic Pass 1 job is to:
 * (1) parse all import files and add them to the parse tree.
 * (2) discover all class and function types and add entries to the CompilerContext->mKnownTypes
 */
class Sempass1Visitor : public Visitor {
public:
  Sempass1Visitor(CompilerContext& ctx);

  CompilerContext& mCtx;
  std::vector<NCompileUnit*> mCompileUnitCtx;

  void visit(NCompileUnit*);
  void visit(NImport*);
  void visit(NClassDecl*);
  void visit(NExternFunctionDecl*);
  void visit(NFunctionDecl*);

};

/**
 * Semantic Pass 2 job is to perform static type analysis
 * for all function arguments and statements. Specifically,
 * for each node, determine the correct Type and add it to the
 * CompilerContext->mTypeTable
 */
class Sempass2Visitor : public Visitor {
public:
  Sempass2Visitor(CompilerContext& ctx);
  ~Sempass2Visitor();

  Type* getType(Node*);

  void push();
  void pop();

  void visit(NCompileUnit*);
  void visit(NImport*);
  void visit(NClassDecl*);
  void visit(NFieldDecl*);
  void visit(NExternFunctionDecl*);
  void visit(NFunctionDecl*);
  void visit(NType*);
  void visit(NNamedType*);
  void visit(NPointerType*);
  void visit(NArrayType*);
  void visit(NLocalVar*);
  void visit(NAssign*);
  void visit(NSymbolRef*);
  void visit(NFieldRef*);
  void visit(NArrayRef*);
  void visit(NIntLiteral*);
  void visit(NStringLiteral*);
  void visit(NCall*);
  void visit(NReturn*);
  void visit(NIfStmt*);
  void visit(NOperation*);
  void visit(NLoad*);
  void visit(NBlock*) override;

  class Scope;

  CompilerContext& mCtx;
  FQPath* mCurrentPackage;
  NImport* mCurrentImport;
  FunctionType* mCurrentFunctionType;
  ClassType* mCurrentClassType;
  Scope* mScope;
};


} // namespace staple

#endif // SEMPASS_H_
