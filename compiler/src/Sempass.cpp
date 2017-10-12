#include "stdafx.h"

#include "Sempass.h"

using namespace llvm;

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

  void visit(NClass* clazz) {
    FQPath classFQPath = mCurrentPackage;
    classFQPath.add(clazz->mName);

    ClassType* classType = new ClassType(classFQPath);
    mCtx.mClasses[classFQPath.getFullString()] = classType;
  }


};

class SemPass2Visitor : public SemPassBaseVisitor {
public:
  using Visitor::visit;

  SemPass2Visitor(CompilerContext& ctx)
    : SemPassBaseVisitor(ctx) {}

  Type* getType(NNamedType* n) {
    if(n->mTypeName.getNumParts() == 1) {
      std::string simpleName = n->mTypeName.getSimpleName();
      if(simpleName.compare("void") == 0) {
        return const_cast<Type*>(Primitives::Void);

      } else if(simpleName.compare("bool") == 0) {
        return const_cast<Type*>(Primitives::Bool);

      } else if(simpleName.compare("int") == 0) {
        return const_cast<IntegerType*>(&Primitives::Int32);

      } else if(simpleName.compare("i8") == 0) {
        return const_cast<IntegerType*>(&Primitives::Int8);
          
      } else if(simpleName.compare("i16") == 0) {
        return const_cast<IntegerType*>(&Primitives::Int16);
          
      } else if(simpleName.compare("byte") == 0) {
        return const_cast<IntegerType*>(&Primitives::UInt8);

      } else {
        //class type
        FQPath fqName = mCurrentPackage;
        fqName.add(simpleName);

        auto ct = mCtx.mClasses.find(fqName.getFullString());
        if(ct != mCtx.mClasses.end()) {
          return (*ct).second;
        } else {
          mCtx.addError("unknown type: '" + n->mTypeName.getFullString() + "'",
                        n->location.first_line, n->location.first_column);
        }
      }
    }

    return nullptr;
  }

  Type* getType(NPointerType* pointerType) {
    Type* baseType = getType(pointerType->mBase);
    return new PointerType(baseType);
  }

  Type* getType(NArrayType* arrayType) {
    Type* baseType = getType(arrayType->mBase);
    return new ArrayType(baseType);
  }

  Type* getType(NType* n) {
    if(isa<NNamedType>(n)) {
      NNamedType* namedType = cast<NNamedType>(n);
      return getType(namedType);

    } else if(isa<NPointerType>(n)){
      NPointerType* pointerType = cast<NPointerType>(n);
      return new PointerType(getType(pointerType->mBase));

    } else if(isa<NArrayType>(n)) {
      NArrayType* arrayType = cast<NArrayType>(n);
      return new ArrayType(getType(arrayType->mBase));

    } else if(n->mVariant == NType::VariantType::Varg) {
        return const_cast<Type*>(Primitives::Vargs);
    }

    return nullptr;
  }

  void visit(NType* n) {
    mCtx.mTypeTable[n] = getType(n);
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

  void visit(NIntLiteral* intLiteral) {
    mCtx.mTypeTable[intLiteral] = const_cast<IntegerType*>(&Primitives::Int32);
  }
  
  void visit(NStringLiteral* strLiteral) {
      mCtx.mTypeTable[strLiteral] = const_cast<ArrayType*>(&Primitives::StringLiteral);
  }

  void visit(NFunction* fun) {
    std::vector<Type*> paramTypes;
    for(NParam* param : fun->mParams) {
      paramTypes.push_back(getType(param->mType));
    }
    Type* returnType = getType(fun->mReturnType);
    mCtx.mTypeTable[fun->mReturnType] = returnType;

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
    mCtx.mTypeTable[funDecl->mReturnType] = returnType;

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

  Type* lookup(const std::string& symbolName) {
    return mScope->lookup(symbolName);
  }

  void visit(NCompileUnit* compileUnit) {
    mCurrentPackage = compileUnit->mPackage;

    for(auto it : mCtx.mFunctions) {
      FQPath funName(it.first);
      if(mCurrentPackage.getFullString().compare(funName.getPackageName()) == 0) {
        defineSymbol(funName.getSimpleName(), it.second);
      }
    }

    visitChildren(compileUnit);
  }

  void visit(NCall* funcall) {
    FunctionType* funType = cast<FunctionType>(lookup(funcall->mName));
    if(funType == nullptr) {
      mCtx.addError("undefined function: '" + funcall->mName + "'",
                    funcall->location.first_line, funcall->location.first_column);
    }

    mCtx.mTypeTable[funcall] = funType->mReturnType;

    for(int i=0; i<funType->mParams.size(); i++) {
      Type* declType = funType->mParams[i];

      Expr* expr = funcall->mArgList[i];
      Type* paramType = getType(expr);
      if(!declType->isAssignableFrom(paramType)) {
        mCtx.addError("expression cannot be assigned to type: '" + declType->toString() + "'",
                      expr->location.first_line, expr->location.first_column);
      }
    }
  }

  void visit(NLocalVar* localVar) {
    Type* t = getType(localVar->mType);
    defineSymbol(localVar->mName, t);
  }

  void visit(NSymbolRef* symbolRef) {
    Type* t = lookup(symbolRef->mName);
    if(t == nullptr) {
      mCtx.addError("undefined symbol: '" + symbolRef->mName + "'",
                    symbolRef->location.first_line, symbolRef->location.first_column);
    }
    mCtx.mTypeTable[symbolRef] = t;
  }

  void visit(NArrayRef* arrayRef) {
    Type* baseType = getType(arrayRef->mBase);
    IntegerType* index = cast<IntegerType>(getType(arrayRef->mIndex));

    if(index == nullptr) {
      mCtx.addError("cannot assign array reference index to integer type",
          arrayRef->mIndex->location.first_line, arrayRef->mIndex->location.first_column);
    }

    mCtx.mTypeTable[arrayRef] = new PointerType(baseType);
  }

  void visit(NFunctionDecl* funDecl) { }

  void visit(NFunction* fun) {

    push();

    for(NParam* param : fun->mParams) {
      Type* paramType = getType(param->mType);
      defineSymbol(param->mName, paramType);
    }

    for(NStmt* stmt : *fun->mStmts) {
      stmt->accept(this);
    }

    pop();
  }

  void visit(Assign* assign) {
    Type* ltype = getType(assign->mLeft);
    Type* rtype = getType(assign->mRight);

    //TODO: ensure that rtype can be assigned to ltype

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
