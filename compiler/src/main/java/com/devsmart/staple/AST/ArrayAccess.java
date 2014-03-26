package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.Symbol;

import java.util.ArrayList;

public class ArrayAccess extends ASTNode {

    public final Symbol var;
    public final ArrayList indexes;

    public ArrayAccess(Symbol var, ArrayList<ASTNode> indexes) {
        this.var = var;
        this.indexes = indexes;
    }
}
