
#include "stdafx.h"

#include <regex>

namespace staple {

void Visitor::visitChildren(Node* node) {
  for(Node* n : node->children) {
    n->accept(this);
  }
}

void Visitor::visit(Node* node) {
  visitChildren(node);
}

NStringLiteral::NStringLiteral(const std::string& value) {
  mStr = doEscapeProcessing(value);
}

std::string NStringLiteral::doEscapeProcessing(const std::string& input) {
  static const std::regex new_line("\\\\n");

  std::string retval = input;
  retval = std::regex_replace(retval, new_line, "\n");

  return retval;
}

NBlock::NBlock(StmtList* stmts)
  : NStmt(TypeId::Block), mStmts(*stmts) {
  children.insert(children.end(), mStmts.begin(), mStmts.end());
}

NClassDecl::NClassDecl(const std::string& name, Node* classparts)
  : Node(TypeId::Class), mName(name) {
  children = classparts->children;
  delete classparts;
}

} // namespace staple
