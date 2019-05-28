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


} // namespace staple

#endif // SEMPASS_H_
