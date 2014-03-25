package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.Symbol;

public class SymbolRef extends ASTNode {

    public final Symbol symbol;

    public SymbolRef(Symbol symbol) {
        this.type = symbol.type;
        this.symbol = symbol;
    }
}
