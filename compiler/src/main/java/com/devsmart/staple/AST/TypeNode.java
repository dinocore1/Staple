package com.devsmart.staple.AST;


import com.devsmart.staple.type.Type;

public class TypeNode extends ASTNode {

    public Type type;

    public TypeNode(Type type) {
        this.type = type;
    }


}
