package com.devsmart.staple.AST;


import com.devsmart.staple.Scope;

import java.util.LinkedList;

public class Block extends ASTNode {
    public Scope scope;
    public LinkedList<ASTNode> statements = new LinkedList<ASTNode>();

}
