package com.devsmart.staple.type;

import com.devsmart.staple.symbols.Argument;
import com.google.common.base.Joiner;

import java.util.Arrays;

public class FunctionType implements Type {

    public final String name;
    public final Type returnType;
    public final Argument[] arguments;

    public boolean isMember;
    public boolean isAnonomus;

    public static FunctionType memberFunction(String name, Type returnType, Argument[] args) {
        FunctionType retval = new FunctionType(name, returnType, args);
        retval.isMember = true;
        retval.isAnonomus = false;
        return retval;
    }

    public static FunctionType anomousFunction(Type returnType, Argument[] args){
        FunctionType retval = new FunctionType(null, returnType, args);
        retval.isMember = false;
        retval.isAnonomus = true;
        return retval;
    }

    private FunctionType(String name, Type returnType, Argument[] arguments){
        this.name = name;
        this.returnType = returnType;
        this.arguments = arguments;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public boolean isAssignableTo(Type dest) {
        boolean retval = false;
        if(dest instanceof FunctionType){
            //TODO: handle member and static functions properly
            FunctionType other = (FunctionType)dest;
            retval = returnType.equals(other.returnType)
                    && Arrays.equals(arguments, other.arguments);
        }
        return retval;
    }

    @Override
    public String toString() {
        if(isAnonomus) {
            return String.format("%s -->(%s)", returnType, Joiner.on(",").join(arguments));
        } else {
            return String.format("%s %s(%s)", returnType, name, Joiner.on(",").join(arguments));
        }
    }
}
