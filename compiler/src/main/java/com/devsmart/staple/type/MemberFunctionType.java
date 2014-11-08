package com.devsmart.staple.type;


import com.devsmart.staple.symbols.Argument;

public class MemberFunctionType extends FunctionType {

    public MemberFunctionType(String name, Type returnType, Argument[] arguments) {
        super(name, returnType, arguments);
    }
}
