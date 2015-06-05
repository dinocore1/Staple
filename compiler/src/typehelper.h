

#ifndef STAPLE_TYPEHELPER_H
#define STAPLE_TYPEHELPER_H


namespace staple {

    StapleType* getStapleType(NType* type, CompilerContext* ctx, NCompileUnit* compileUnit, const Scope& scope);
    StapleClass* resolveClassType(CompilerContext* context, NCompileUnit *startingCompileUnit, const string &className);
}

#endif //STAPLE_TYPEHELPER_H
