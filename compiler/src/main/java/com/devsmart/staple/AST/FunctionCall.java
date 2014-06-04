package com.devsmart.staple.AST;


import com.devsmart.staple.symbol.FunctionSymbol;
import com.devsmart.staple.type.FunctionType;

import java.util.ArrayList;

public class FunctionCall extends ASTNode {

    public final FunctionSymbol functionSymbol;
    public final ArrayList<ASTNode> args = new ArrayList<ASTNode>();

    public FunctionCall(FunctionSymbol symbol) {
        functionSymbol = symbol;

        FunctionType functionType = (FunctionType) functionSymbol.type;
        type = functionType.returnType;
    }
}
