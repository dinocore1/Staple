package com.devsmart.staple.AST;

import com.devsmart.staple.symbol.ClassSymbol;


public class MemberAccess extends ASTNode {

    private final ASTNode baseNode;
    private final ClassSymbol classSymbol;
    private final String memberName;

    public MemberAccess(ASTNode baseNode, ClassSymbol classSymbol, String memberName) {
        this.baseNode = baseNode;
        this.classSymbol = classSymbol;
        this.memberName = memberName;
        this.type = classSymbol.getType().members.get(memberName);
    }
}
