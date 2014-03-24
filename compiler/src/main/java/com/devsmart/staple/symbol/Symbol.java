package com.devsmart.staple.symbol;


import com.devsmart.staple.type.Type;

public class Symbol {

    public final String name;
    public final Type type;

    public Symbol(String name, Type type){
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("[%s %s]", type.toString(), name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean retval = false;
        if(obj instanceof Symbol){
            retval = name.equals(((Symbol) obj).name) && type.equals(((Symbol) obj).type);
        }
        return retval;
    }
}
