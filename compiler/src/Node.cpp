
#include "stdafx.h"

namespace staple {

void Visitor::visit(Node *node) {
    for(Node* n : node->children) {
        n->accept(this);
    }
}


} // namespace staple