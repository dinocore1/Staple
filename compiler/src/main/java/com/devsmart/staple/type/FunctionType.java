package com.devsmart.staple.type;

public class FunctionType implements Type {

    public final String name;
    public final Type returnType;
    public final Type[] arguments;

    public FunctionType(String name, Type returnType, Type[] arguments){
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public String getTypeName() {
        return name;
    }
}
