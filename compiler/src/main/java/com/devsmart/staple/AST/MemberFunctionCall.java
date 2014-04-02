package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.ClassSymbol;

import java.util.ArrayList;

public class MemberFunctionCall extends ASTNode {

    private final ASTNode left;
    private final ClassSymbol baseClassSymbol;
    private final String functionName;
    private final ArrayList<ASTNode> args;

    public MemberFunctionCall(ASTNode left, ClassSymbol baseClassSymbol, String functionName, ArrayList<ASTNode> args) {
        this.left = left;
        this.baseClassSymbol = baseClassSymbol;
        this.functionName = functionName;
        this.args = args;
        this.type = baseClassSymbol.getType().getMemberFunction(functionName).returnType;
    }
}
