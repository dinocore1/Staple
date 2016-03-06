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

class SempPass2Visitor : public Visitor {
public:
  using Visitor::visit;

  CompilerContext& mCtx;
  FQPath mCurrentPackage;

  SempPass2Visitor(CompilerContext& ctx)
  : mCtx(ctx) { }

  void visit(NCompileUnit* compileUnit) {
    mCurrentPackage = compileUnit->mPackage;
    visitChildren(compileUnit);
  }

  Type* getType(NType* n) {
    if(n->mTypeName.getNumParts() == 1) {
      std::string simpleName = n->mTypeName.getSimpleName();
      if(simpleName.compare("void") == 0) {
        return (Type*)&Primitives::Void;
      } else if(simpleName.compare("bool") == 0) {
        return (Type*)&Primitives::Bool;
      } else if(simpleName.compare("int") == 0) {
        return (Type*)&Primitives::Int32;
      } else {
        //class type
        FQPath fqName = mCurrentPackage;
        fqName.add(simpleName);

        auto ct = mCtx.mClasses.find(fqName.getFullString());
        if(ct != mCtx.mClasses.end()) {
          return (*ct).second;
        }
      }
    }

    mCtx.addError("unknown type: '" + n->mTypeName.getFullString() + "'",
      n->location.first_line, n->location.first_column);
    return NULL;
  }

  void visit(NFunction* fun) {
    std::vector<Type*> paramTypes;
    for(NParam* param : fun->mParams) {
      paramTypes.push_back(getType(param->mType));
    }
    Type* returnType = getType(fun->mReturnType);

    FunctionType* funType = new FunctionType(paramTypes, returnType);

    FQPath fqFunName = mCurrentPackage;
    fqFunName.add(fun->mName);

    mCtx.mFunctions[fqFunName.getFullString()] = funType;
  }

  void visit(NFunctionDecl* funDecl) {
    std::vector<Type*> paramTypes;
    for(NParam* param : funDecl->mParams) {
      paramTypes.push_back(getType(param->mType));
    }
    Type* returnType = getType(funDecl->mReturnType);

    FunctionType* funType = new FunctionType(paramTypes, returnType);

    FQPath fqFunName = mCurrentPackage;
    fqFunName.add(funDecl->mName);

    mCtx.mFunctions[fqFunName.getFullString()] = funType;

  }


};


Sempass::Sempass(CompilerContext* ctx)
 : mCtx(ctx) {}

void Sempass::doit() {
  SempPass1Visitor sempass1(*mCtx);
  mCtx->rootNode->accept(&sempass1);

  SempPass2Visitor sempass2(*mCtx);
  mCtx->rootNode->accept(&sempass2);

}

} // namespace staple
