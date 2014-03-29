package com.devsmart.staple;


import com.devsmart.staple.AST.ASTNode;
import com.devsmart.staple.ir.SSAInst;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.LinkedList;

public class CompilerContext {

    public ErrorStream errorStream = new ErrorStream();
    public ParseTreeProperty<ASTNode> astTreeProperties = new ParseTreeProperty<ASTNode>();
    public LinkedList<SSAInst> code = new LinkedList<SSAInst>();
    public Scope rootScope = new Scope(null);
}
