package com.devsmart.staple.symbols;


import com.devsmart.staple.type.Type;

import java.util.Collections;
import java.util.Set;

public class Field extends Variable {

    public static enum Modifier {
        Strong,
        Weak,
        Static
    }

    private final Set<Modifier> modifiers;

    public Field(Type type, String name) {
        super(type, name);
        modifiers = Collections.EMPTY_SET;
    }

    public Field(Type type, String name, Set<Modifier> modifiers){
        super(type, name);
        this.modifiers = modifiers;
    }

}
