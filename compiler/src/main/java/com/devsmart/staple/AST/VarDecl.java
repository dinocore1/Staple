package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.Symbol;

public class VarDecl extends ASTNode {

    public final Symbol symbol;

    public VarDecl(Symbol symbol) {
        this.symbol = symbol;
    }
}
