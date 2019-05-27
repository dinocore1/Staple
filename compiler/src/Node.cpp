
#include "stdafx.h"

namespace staple {

void Visitor::visitChildren(Node* node) {
  for(Node* n : node->children) {
    n->accept(this);
  }
}

void Visitor::visit(Node* node) {
  visitChildren(node);
}

NBlock::NBlock(StmtList* stmts)
  : NStmt(TypeId::Block), mStmts(*stmts) {
  children.insert(children.end(), mStmts.begin(), mStmts.end());
}

NClassDecl::NClassDecl(const std::string& name, Node* classparts)
 : Node(TypeId::Class), mName(name)
{
  children = classparts->children;
  delete classparts;
}

} // namespace staple
