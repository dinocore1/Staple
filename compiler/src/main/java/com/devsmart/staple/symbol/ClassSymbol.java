package com.devsmart.staple.symbol;


import com.devsmart.staple.type.ClassType;

public class ClassSymbol extends Symbol {

    public ClassSymbol(String name) {
        super(name, new ClassType(name));
    }
}
