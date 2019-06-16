#include "stdafx.h"

#include "Sempass.h"

using namespace llvm;

namespace staple {

class Sempass2Visitor::Scope {
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

Sempass2Visitor::Sempass2Visitor(CompilerContext& ctx)
  : mCtx(ctx), mCurrentPackage(nullptr), mCurrentImport(nullptr),
    mCurrentClassType(nullptr), mScope(new Scope(nullptr))
{}

Sempass2Visitor::~Sempass2Visitor() {
  delete mScope;
}

void Sempass2Visitor::push() {
  mScope = new Scope(mScope);
}

void Sempass2Visitor::pop() {
  Scope* oldScope = mScope;
  mScope = mScope->mParent;
  delete oldScope;
}

Type* Sempass2Visitor::getType(Node* n) {
  n->accept(this);
  return mCtx.mTypeTable[n];
}

void Sempass2Visitor::visit(NCompileUnit* compileUnit) {
  FQPath* oldPackage = mCurrentPackage;
  mCurrentPackage = &compileUnit->mPackage;
  visitChildren(compileUnit);
  mCurrentPackage = oldPackage;
}

void Sempass2Visitor::visit(NImport* n) {
  NImport* oldImport = mCurrentImport;
  mCurrentImport = n;
  visitChildren(n);
  mCurrentImport = oldImport;
}

void Sempass2Visitor::visit(NClassDecl* n) {
  FQPath classFQPath = *mCurrentPackage;
  classFQPath.add(n->mName);

  ClassType* oldClassType = mCurrentClassType;
  mCurrentClassType = dyn_cast_or_null<ClassType>(mCtx.mKnownTypes[classFQPath]);
  mCtx.mTypeTable[n] = mCurrentClassType;
  visitChildren(n);
  mCurrentClassType = oldClassType;
}

void Sempass2Visitor::visit(NFieldDecl* n) {
  Type* type = getType(n->mType);
  mCurrentClassType->mFields[n->mName] = type;
}

void Sempass2Visitor::visit(NFunctionDecl* funDecl) {
  push();

  FQPath fqFunName = *mCurrentPackage;
  fqFunName.add(funDecl->mName);
  FunctionType* funType = dyn_cast_or_null<FunctionType>(mCtx.mKnownTypes[fqFunName]);
  mCurrentFunctionType = funType;

  for(NParam* param : funDecl->mParams) {
    Type* paramType = getType(param->mType);
    funType->mParams.push_back(paramType);
    mScope->defineSymbol(param->mName, paramType);
  }
  funType->mReturnType = getType(funDecl->mReturnType);
  mScope->defineSymbol(funDecl->mName, funType);

  //only do statement type checking for the compile unit, not its imports
  if(mCurrentImport == nullptr) {
    for(NStmt* stmt : *funDecl->mStmts) {
      stmt->accept(this);
    }
  }

  pop();
}

void Sempass2Visitor::visit(NExternFunctionDecl* funDecl) {
  FQPath fqFunName = *mCurrentPackage;
  fqFunName.add(funDecl->mName);
  FunctionType* funType = dyn_cast_or_null<FunctionType>(mCtx.mKnownTypes[fqFunName]);

  for(NParam* param : funDecl->mParams) {
    Type* paramType = getType(param->mType);
    funType->mParams.push_back(paramType);
  }
  funType->mReturnType = getType(funDecl->mReturnType);
  mScope->defineSymbol(funDecl->mName, funType);
}

void Sempass2Visitor::visit(NType* n) {
  if(isa<NNamedType>(n)) {
    visit(cast<NNamedType>(n));
  } else if(isa<NPointerType>(n)) {
    visit(cast<NPointerType>(n));
  } else if(isa<NArrayType>(n)) {
    visit(cast<NArrayType>(n));
  }

}

void Sempass2Visitor::visit(NNamedType* n) {
  if(n->mTypeName.getNumParts() == 1) {
    std::string simpleName = n->mTypeName.getSimpleName();
    if(simpleName.compare("void") == 0) {
      mCtx.mTypeTable[n] = const_cast<Type*>(Primitives::Void);

    } else if(simpleName.compare("bool") == 0) {
      mCtx.mTypeTable[n] = const_cast<Type*>(Primitives::Bool);

    } else if(simpleName.compare("i8") == 0) {
      mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int8);

    } else if(simpleName.compare("i16") == 0) {
      mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int16);

    } else if(simpleName.compare("i32") == 0 ||
              simpleName.compare("int") == 0) {
      mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int32);

    } else if(simpleName.compare("i64") == 0) {
      mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int64);

    } else if(simpleName.compare("f32") == 0 ||
              simpleName.compare("float") == 0) {
      mCtx.mTypeTable[n] = const_cast<FloatType*>(&Primitives::F32);

    } else if(simpleName.compare("f64") == 0) {
      mCtx.mTypeTable[n] = const_cast<FloatType*>(&Primitives::F64);
    } else {
      //class type
      FQPath fqName = *mCurrentPackage;
      fqName.add(simpleName);

      auto ct = mCtx.mKnownTypes.find(fqName);
      if(ct != mCtx.mKnownTypes.end()) {
        mCtx.mTypeTable[n] = (*ct).second;
      } else {
        mCtx.addError("unknown type: '" + n->mTypeName.getFullString() + "'",
                      n->location.first_line, n->location.first_column);
      }
    }
  }
}

void Sempass2Visitor::visit(NPointerType* n) {
  Type* baseType = getType(n->mBase);
  mCtx.mTypeTable[n] = new PointerType(baseType);
}

void Sempass2Visitor::visit(NArrayType* n) {
  Type* baseType = getType(n->mBase);
  mCtx.mTypeTable[n] = new ArrayType(baseType);
}

void Sempass2Visitor::visit(NLocalVar* n) {
  Type* type = getType(n->mType);
  mScope->defineSymbol(n->mName, type);

  if(n->mInitializer) {
    Type* rtype = getType(n->mInitializer);
    if(!type->isAssignableFrom(rtype)) {
      std::ostringstream strBuilder;
      strBuilder << "cannot assign type '" << type->toString() << "' from: '" << rtype->toString() << "'";
      mCtx.addError(strBuilder.str(), n->location.first_line, n->location.first_column);
    }
  }
}

void Sempass2Visitor::visit(Assign* n) {
  Type* ltype = getType(n->mLeft);
  Type* rtype = getType(n->mRight);

  if(!ltype->isAssignableFrom(rtype)) {
    std::ostringstream strBuilder;
    strBuilder << "cannot assign type '" << ltype->toString() << "' from: '" << rtype->toString() <<
               "'";
    mCtx.addError(strBuilder.str(), n->location.first_line, n->location.first_column);
  }
}

void Sempass2Visitor::visit(NSymbolRef* n) {
  Type* type = mScope->lookup(n->mName);
  if(type != nullptr) {
    mCtx.mTypeTable[n] = type;
  } else {
    mCtx.addError("undefined symbol: '" + n->mName + "'",
                  n->location.first_line, n->location.first_column);
  }

}

void Sempass2Visitor::visit(NFieldRef* n) {
  Type* baseType = getType(n->mBase);
  if(!isa<ClassType>(baseType)) {
    mCtx.addError("not an object type", n->location.first_line, n->location.first_column);
  } else {
    ClassType* classType = cast<ClassType>(baseType);
    Type* fieldType = classType->mFields[n->mField];
    mCtx.mTypeTable[n] = fieldType;
  }
}

void Sempass2Visitor::visit(NIntLiteral* n) {
  mCtx.mTypeTable[n] = const_cast<IntegerType*>(&Primitives::Int32);
}

void Sempass2Visitor::visit(NStringLiteral* n) {
  mCtx.mTypeTable[n] = const_cast<ArrayType*>(&Primitives::StringLiteral);
}

void Sempass2Visitor::visit(NCall* n) {
  FunctionType* funType = dyn_cast_or_null<FunctionType>(mScope->lookup(n->mName));
  if(funType == nullptr) {
    mCtx.addError("undefined function: '" + n->mName + "'",
                  n->location.first_line, n->location.first_column);
    return;
  }

  mCtx.mTypeTable[n] = funType->mReturnType;

  for(size_t i=0; i<funType->mParams.size(); i++) {
    Type* declType = funType->mParams[i];

    Expr* expr = n->mArgList[i];
    Type* paramType = getType(expr);
    if(!declType->isAssignableFrom(paramType)) {
      mCtx.addError("expression cannot be assigned to type: '" + declType->toString() + "'",
                    expr->location.first_line, expr->location.first_column);
    }
  }
}

void Sempass2Visitor::visit(Return* n) {
  Type* returnType = getType(n->mExpr);

  if(!mCurrentFunctionType->mReturnType->isAssignableFrom(returnType)) {
    mCtx.addError("expression cannot be assigned to type: '" +
                  mCurrentFunctionType->mReturnType->toString() + "'",
                  n->location.first_line, n->location.first_column);
  }

}

} // namespace staple