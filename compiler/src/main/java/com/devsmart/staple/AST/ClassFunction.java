package com.devsmart.staple.AST;


import com.devsmart.staple.type.Type;

import java.util.ArrayList;

public class ClassFunction extends ASTNode {

    public String name;
    public Type returnType;
    public ArrayList<VarDecl> args = new ArrayList<VarDecl>();
    public Block block;


    public Type[] getArgTypes() {
        Type[] retval = new Type[args.size()];
        for(int i=0;i<args.size();i++){
            retval[i] = args.get(i).type;
        }
        return retval;
    }
}
