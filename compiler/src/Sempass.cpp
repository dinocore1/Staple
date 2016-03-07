#include "stdafx.h"

#include "Sempass.h"

namespace staple {

class SemPassBaseVisitor : public Visitor {
public:
  using Visitor::visit;
  SemPassBaseVisitor(CompilerContext& ctx)
    : mCtx(ctx) {}

  CompilerContext& mCtx;
  FQPath mCurrentPackage;

  void visit(NCompileUnit* compileUnit) {
    mCurrentPackage = compileUnit->mPackage;
    visitChildren(compileUnit);
  }
};

class SemPass1Visitor : public SemPassBaseVisitor {
public:
  using Visitor::visit;
  SemPass1Visitor(CompilerContext& ctx)
    : SemPassBaseVisitor(ctx) {}

  void visit(NClass* function) {
    FQPath classFQPath = mCurrentPackage;
    classFQPath.add(function->mName);

    ClassType* classType = new ClassType(classFQPath);

    mCtx.mClasses[classFQPath.getFullString()] = classType;
  }


};

class SemPass2Visitor : public SemPassBaseVisitor {
public:
  using Visitor::visit;

  SemPass2Visitor(CompilerContext& ctx)
    : SemPassBaseVisitor(ctx) {}

  void visit(NType* n) {
    if(n->mTypeName.getNumParts() == 1) {
      std::string simpleName = n->mTypeName.getSimpleName();
      if(simpleName.compare("void") == 0) {
        mCtx.mTypeTable[n] = const_cast<Type*>(&Primitives::Void);

      } else if(simpleName.compare("bool") == 0) {
        mCtx.mTypeTable[n] = const_cast<Type*>(&Primitives::Bool);

      } else if(simpleName.compare("int") == 0) {
        mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int32);

      } else {
        //class type
        FQPath fqName = mCurrentPackage;
        fqName.add(simpleName);

        auto ct = mCtx.mClasses.find(fqName.getFullString());
        if(ct != mCtx.mClasses.end()) {
          mCtx.mTypeTable[n] = (*ct).second;
        } else {
          mCtx.addError("unknown type: '" + n->mTypeName.getFullString() + "'",
                        n->location.first_line, n->location.first_column);
        }
      }
    }
  }

  Type* getType(Node* node) {
    auto it = mCtx.mTypeTable.find(node);
    if(it != mCtx.mTypeTable.end()) {
      return (*it).second;
    }

    node->accept(this);

    it = mCtx.mTypeTable.find(node);
    if(it != mCtx.mTypeTable.end()) {
      return (*it).second;
    } else {

      return nullptr;
    }
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

class SemPass3Visitor : public SemPass2Visitor {

  class Scope {
  public:
    Scope* mParent;
    std::map<std::string, Type*> mSymbolTable;

    Scope(Scope* parent)
      : mParent(parent) {}

    ~Scope() {
    }

    void defineSymbol(const std::string& symbolName, Type* type) {
      mSymbolTable[symbolName] = type;
    }

    Type* lookup(const std::string& symbolName) {
      auto it = mSymbolTable.find(symbolName);
      if(it != mSymbolTable.end()) {
        return (*it).second;
      }

      if(mParent != nullptr) {
        return mParent->lookup(symbolName);
      }

      return nullptr;
    }

  };

public:
  using Visitor::visit;
  using SemPass2Visitor::visit;

  Scope* mScope;

  SemPass3Visitor(CompilerContext& ctx)
    : SemPass2Visitor(ctx) {
    mScope = new Scope(nullptr);
  }

  void push() {
    mScope = new Scope(mScope);
  }

  void pop() {
    Scope* oldScope = mScope;
    mScope = mScope->mParent;
    delete oldScope;
  }

  void defineSymbol(const std::string& symbolName, Type* type) {
    mScope->defineSymbol(symbolName, type);
  }

  void visit(NFunction* fun) {
    push();
    for(NParam* param : fun->mParams) {
      Type* paramType = getType(param->mType);
      if(paramType != nullptr) {
        defineSymbol(param->mName, paramType);
        mCtx.mTypeTable[param] = paramType;
      }
    }

    mCtx.mTypeTable[fun->mReturnType] = getType(fun->mReturnType);

    for(NStmt* stmt : *fun->mStmts) {
      stmt->accept(this);
    }

    pop();
  }

  void visit(NFunctionDecl* funDecl) {}

  void visit(Assign* assign) {
    Type* ltype = getType(assign->mLeft);
    Type* rtype = getType(assign->mRight);

  }

};


Sempass::Sempass(CompilerContext* ctx)
  : mCtx(ctx) {}

void Sempass::doit() {
  SemPass1Visitor sempass1(*mCtx);
  mCtx->rootNode->accept(&sempass1);

  SemPass2Visitor sempass2(*mCtx);
  mCtx->rootNode->accept(&sempass2);

  SemPass3Visitor sempass3(*mCtx);
  mCtx->rootNode->accept(&sempass3);

}

} // namespace staple
