
#ifndef _STAPLE_POINTERSCOPEPASS_H_
#define _STAPLE_POINTERSCOPEPASS_H_

#include <llvm/Pass.h>

using namespace llvm;

class PointerScopePass : public BasicBlockPass {

public:
    PointerScopePass(char &pid) : BasicBlockPass(pid) {
    }
};


#endif //_STAPLE_POINTERSCOPEPASS_H_
