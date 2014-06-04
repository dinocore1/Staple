package com.devsmart.staple.ir;


import com.devsmart.staple.AST.ClassFunction;

public class FunctionDeclaration extends SSAInst {

    public final ClassFunction mFunctionSymbol;

    public FunctionDeclaration(ClassFunction memberSymbol) {
        mFunctionSymbol = memberSymbol;
    }


    @Override
    public String toString() {
        return String.format("\nfunc %s", mFunctionSymbol.name);
    }
}
