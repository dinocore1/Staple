package com.devsmart.staple.ir;


import com.devsmart.staple.symbol.Symbol;
import com.devsmart.staple.type.Type;

public class Operand {

    public final Type type;
    public Symbol tag;

    public Operand(Type type) {
        this.type = type;
    }
}
