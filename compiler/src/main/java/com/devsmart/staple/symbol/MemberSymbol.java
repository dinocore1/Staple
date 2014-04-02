package com.devsmart.staple.symbol;


import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.Type;

public class MemberSymbol extends Symbol {

    public final ClassType parent;

    public MemberSymbol(String name, Type type, ClassType parent) {
        super(name, type);
        this.parent = parent;
    }
}
