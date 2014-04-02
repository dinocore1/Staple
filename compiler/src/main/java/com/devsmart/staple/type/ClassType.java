package com.devsmart.staple.type;


import java.util.HashMap;

public class ClassType extends Type {

    public HashMap<String, Type> members = new HashMap<String, Type>();

    public ClassType(String name) {
        super(name);
    }

    public FunctionType getMemberFunction(String name) {
        Type value = members.get(name);
        if(value != null && value instanceof FunctionType){
            return (FunctionType) value;
        } else {
            return null;
        }
    }

    @Override
    public boolean isAssignableTo(Type t) {
        return false;
    }


}
