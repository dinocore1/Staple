#ifndef SEMPASS_H_
#define SEMPASS_H_

namespace staple {

class Sempass {
public:
  Sempass(CompilerContext*);
  void doit();

  CompilerContext* mCtx;
};


} // namespace staple

#endif // SEMPASS_H_
