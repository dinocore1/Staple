package com.devsmart.staple.ir;


import com.devsmart.staple.symbol.FunctionSymbol;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionCallInst extends AssignmentInst {

    public final FunctionSymbol functionSymbol;
    public final Operand[] argOperands;


    public FunctionCallInst(FunctionSymbol functionSymbol, Var retval, ArrayList<Operand> argOperands) {
        this.functionSymbol = functionSymbol;
        this.result = retval;
        this.argOperands = new Operand[argOperands.size()];
        argOperands.toArray(this.argOperands);
    }

    @Override
    public String toString() {
        return String.format("%s = call %s (%s)", result.name, functionSymbol.name, Arrays.toString(argOperands));
    }
}
