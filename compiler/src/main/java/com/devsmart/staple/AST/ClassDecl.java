package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.ClassSymbol;

import java.util.ArrayList;

public class ClassDecl extends ASTNode {

    public final ClassSymbol symbol;
    public final ArrayList<ClassMember> members = new ArrayList<ClassMember>();
    public final ArrayList<ClassFunction> functions = new ArrayList<ClassFunction>();

    public ClassDecl(ClassSymbol symbol) {
        this.symbol = symbol;
        this.type = symbol.type;
    }



}
