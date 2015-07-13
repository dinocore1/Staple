

#ifndef STAPLE_TYPEHELPER_H
#define STAPLE_TYPEHELPER_H


namespace staple {

    StapleType* getStapleType(const std::string& value, NCompileUnit* compileUnit, CompilerContext* context);
    StapleType* getStapleType(NType* type, CompilerContext* ctx, NCompileUnit* compileUnit, const Scope& scope);
    StapleType* searchNamespace(CompilerContext *context, NCompileUnit *startingCompileUnit, const string &name);
}

#endif //STAPLE_TYPEHELPER_H
