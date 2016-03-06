#include "stdafx.h"

#include "Sempass.h"

namespace staple {

class SempPass1Visitor : public Visitor {
public:
  using Visitor::visit;
  SempPass1Visitor(CompilerContext& ctx)
  : mCtx(ctx) {}

  CompilerContext& mCtx;
  FQPath mCurrentPackage;

  void visit(NCompileUnit* compileUnit) {
    mCurrentPackage = compileUnit->mPackage;
    visitChildren(compileUnit);
  }

  void visit(NClass* function) {
    FQPath classFQPath = mCurrentPackage;
    classFQPath.add(function->mName);

    ClassType* classType = new ClassType(classFQPath);

    mCtx.mClasses[classFQPath.getFullString()] = classType;
  }


};


Sempass::Sempass(CompilerContext* ctx)
 : mCtx(ctx) {}

void Sempass::doit() {
  SempPass1Visitor sempass1(*mCtx);
  mCtx->rootNode->accept(&sempass1);

}

} // namespace staple
