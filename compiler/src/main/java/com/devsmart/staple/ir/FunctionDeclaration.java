package com.devsmart.staple.ir;


import com.devsmart.staple.AST.ClassFunction;
import com.devsmart.staple.AST.VarDecl;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import java.util.Collection;

public class FunctionDeclaration extends SSAInst {

    public final ClassFunction mFunctionSymbol;

    public FunctionDeclaration(ClassFunction memberSymbol) {
        mFunctionSymbol = memberSymbol;
    }


    @Override
    public String toString() {
        Collection<String> argstr = Collections2.transform(mFunctionSymbol.args, new Function<VarDecl, String>() {
            @Override
            public String apply(VarDecl input) {
                return String.format("%s %s", input.symbol.type, input.symbol.name);
            }
        });

        return String.format("\nfunc %s (%s)", mFunctionSymbol.name, Joiner.on(",").join(argstr));
    }
}
