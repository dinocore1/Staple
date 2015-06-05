

#ifndef STAPLE_TYPEHELPER_H
#define STAPLE_TYPEHELPER_H


namespace staple {

    StapleType* getStapleType(NType* type, CompilerContext* ctx, NCompileUnit* compileUnit, const Scope& scope);
}

#endif //STAPLE_TYPEHELPER_H
