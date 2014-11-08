package com.devsmart.staple.type;


import com.devsmart.staple.Scope;
import com.devsmart.staple.symbols.Field;

import java.util.ArrayList;

public class ClassType implements Type {

    public final String name;
    public ClassType parent;
    public Scope scope;
    public ArrayList<Field> fields = new ArrayList<Field>();
    public ArrayList<FunctionType> functions = new ArrayList<FunctionType>();

    public ClassType(String name){
        this.name = name;
    }


    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("class: %s", name);
    }

    public Field getField(String name) {
        Field retval = null;
        for(Field field : fields){
            if(field.name.equals(name)){
                retval = field;
                break;
            }
        }

        if(retval == null && parent != null){
            retval = parent.getField(name);
        }

        return retval;
    }
}
