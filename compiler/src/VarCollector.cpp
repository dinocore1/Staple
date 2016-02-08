
#include "stdafx.h"

using namespace std;

namespace staple {

void VarCollector::visit(Block* block) {
    vector<Stmt*> vars;

    for(auto it=block->mStmts.begin();it!=block->mStmts.end();it++) {
      Stmt* n = *it;
      if(n->getType() == Node::Type::VarDecl) {
        vars.push_back(n);
        block->mStmts.erase(it);
      }
    }
    block->mStmts.insert(block->mStmts.begin(), vars.begin(), vars.end());
    visitChildren(block);

}

} // namespace staple
