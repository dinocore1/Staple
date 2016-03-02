
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

NBlock::NBlock(StmtList *stmts)
 : NStmt(TypeId::Block), mStmts(*stmts) {
  children.insert(children.end(), mStmts.begin(), mStmts.end());
}


} // namespace staple
