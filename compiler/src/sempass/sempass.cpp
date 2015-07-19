
#include "../node.h"
#include "../types/stapletype.h"

#include "sempass.h"
#include "../typehelper.h"
#include "typevisitor.h"
#include "../stringutils.h"

namespace staple {

using namespace std;
using namespace llvm;

#define CheckType(type, location, name, positive) \
if(type == NULL) { \
    sempass->ctx->logError(location, "undefined type: '%s'", name.c_str()); \
} else { \
    positive \
}


SemPass::SemPass(CompilerContext* ctx)
: ctx(ctx) { }


void SemPass::doIt()
{

    Pass1TypeVisitor p1ClassVisitor(ctx);
    p1ClassVisitor.visit(ctx->mCompileUnit);

    Pass2TypeVisitor pass2ClassVisitor(ctx);
    pass2ClassVisitor.visit(ctx->mCompileUnit);

    Pass3TypeVisitor pass3ClassVisitor(ctx);
    pass3ClassVisitor.visit(ctx->mCompileUnit);
}

bool hasEnding (std::string const &fullString, std::string const &ending) {
    if (fullString.length() >= ending.length()) {
        return (0 == fullString.compare (fullString.length() - ending.length(), ending.length(), ending));
    } else {
        return false;
    }
}

StapleType* getStapleType(const std::string& value, NCompileUnit* compileUnit, CompilerContext* ctx) {
    StapleType* retval = NULL;
    if(value.compare("void") == 0) {
        retval = StapleType::getVoidType();
    } else if(value.compare("uint") == 0 || value.compare("int") == 0 || value.compare("int32") == 0){
        retval = StapleType::getInt32Type();
    } else if(value.compare("uint8") == 0 || value.compare("int8") == 0) {
        retval = StapleType::getInt8Type();
    } else if(value.compare("uint16") == 0 || value.compare("int16") == 0) {
        retval = StapleType::getInt16Type();
    } else if(value.compare("float") == 0 || value.compare("float32") == 0) {
        retval = StapleType::getFloat32Type();
    } else if(value.compare("bool") == 0) {
        retval = StapleType::getBoolType();
    } else if(value.compare("obj") == 0){
        retval = CompilerContext::getStpObjClass();

    } else {
        retval = searchNamespace(ctx, compileUnit, value);
        if(retval == nullptr) {
            return nullptr;
        }
    }

    return retval;
}

StapleType* getStapleType(NType* type, CompilerContext* ctx, NCompileUnit* compileUnit, const Scope& scope) {
    const string name = type->name;

    StapleType* retval = NULL;
    if(name.compare("void") == 0) {
        retval = StapleType::getVoidType();
    } else if(name.compare("uint") == 0 || name.compare("int") == 0 || name.compare("int32") == 0){
        retval = StapleType::getInt32Type();
    } else if(name.compare("uint8") == 0 || name.compare("int8") == 0) {
        retval = StapleType::getInt8Type();
    } else if(name.compare("uint16") == 0 || name.compare("int16") == 0) {
        retval = StapleType::getInt16Type();
    } else if(name.compare("float") == 0 || name.compare("float32") == 0) {
        retval = StapleType::getFloat32Type();
    } else if(name.compare("bool") == 0) {
        retval = StapleType::getBoolType();
    } else if(name.compare("obj") == 0){
        retval = CompilerContext::getStpObjClass();

    } else {

        retval = scope.get(name);
        if(retval == nullptr) {
            retval = searchNamespace(ctx, compileUnit, type->name);
            if(retval == nullptr) {
                return nullptr;
            }
        }
    }

    if(type->isArray) {
        retval = new StapleArray(retval, type->size);
    } else {
        for(int i=0;i<type->numPointers;i++) {
            retval = new StaplePointer(retval);
        }
    }
    return retval;
}

StapleType* searchNamespace(CompilerContext *context, NCompileUnit *startingCompileUnit, const string &name) {

    StapleType* retval = nullptr;
    string fqName = startingCompileUnit->package + '.' + name;
    retval = context->mRootScope.get(fqName);

    if(retval != nullptr) {
        return retval;
    }

    for(string namespacePackage : context->mCompileUnit->usingNamespaces) {
        fqName = namespacePackage + '.' + name;
        retval = context->mRootScope.get(fqName);
        if(retval != nullptr) {
            break;
        }
    }

    return retval;
}

}


