package com.devsmart.staple;


import com.devsmart.staple.symbol.Symbol;

import java.util.HashMap;

public class Scope {

    private HashMap<String, Symbol> mTable = new HashMap<String, Symbol>();
    public Scope mParent;

    public Scope(Scope parent) {
        mParent = parent;
    }

    public void put(String name, Symbol sym) {
        mTable.put(name, sym);
    }

    public Symbol get(String name) {
        Symbol retval = null;
        for(Scope s = this; s != null; s = s.mParent) {
            retval = s.mTable.get(name);
            if( retval != null) {
                break;
            }
        }
        return retval;
    }

    public void define(Symbol var) {
        mTable.put(var.name, var);
    }
}
