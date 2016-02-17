
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


} // namespace staple
