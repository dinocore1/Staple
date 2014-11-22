package com.devsmart.staple.type;


import com.devsmart.staple.Namespace;
import com.devsmart.staple.Scope;
import com.devsmart.staple.symbols.Field;

import java.util.ArrayList;
import java.util.List;

public class ClassType implements Type {

    public final String name;
    public ClassType parent;
    public Scope scope;
    public List<Field> fields = new ArrayList<Field>();
    public List<FunctionType> functions = new ArrayList<FunctionType>();
    public Namespace namespace = Namespace.defaultNameSpace;

    public ClassType(String name){
        this.name = name;
    }


    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public boolean isAssignableTo(Type dest) {
        boolean retval = false;
        if(dest instanceof ClassType){
            //TODO: implement subclass checking
            retval = name.equals(((ClassType) dest).name);
        }

        return retval;
    }

    @Override
    public String toString() {
        return name;
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

    public FunctionType getFunction(String name) {
        FunctionType retval = null;
        for(FunctionType function : functions) {
            if(function.name.equals(name)){
                retval = function;
                break;
            }
        }

        if(retval == null && parent != null){
            retval = parent.getFunction(name);
        }

        return retval;
    }

    public boolean hasFunction(String name) {
        boolean retval = false;
        for(FunctionType function : functions) {
            if (function.name.equals(name)) {
                retval = true;
            }
        }
        return retval;
    }
}
