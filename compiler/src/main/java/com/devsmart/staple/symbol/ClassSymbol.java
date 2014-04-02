package com.devsmart.staple.symbol;


import com.devsmart.staple.type.ClassType;
import com.devsmart.staple.type.Type;

public class ClassSymbol extends Symbol {

    public ClassSymbol(ClassType type) {
        super(type.name, type);
    }

    public ClassType getType() {
        return (ClassType) type;
    }
}
