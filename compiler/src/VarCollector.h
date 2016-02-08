#ifndef VARCOLLECTOR_H_
#define VARCOLLECTOR_H_

namespace staple {

class VarCollector : public Visitor {
public:
  using Visitor::visit;
  VarCollector();

  void visit(Block*);
};

}

#endif // VARCOLLECTOR_H_
