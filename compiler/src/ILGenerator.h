
#ifndef STAPLE_ILGENERATOR_H
#define STAPLE_ILGENERATOR_H

#include "stdafx.h"

namespace staple {

class ILGenerator : public Visitor {
public:
    using Visitor::visit;

    virtual void visit(Assign*);

};

}

#endif //STAPLE_ILGENERATOR_H
