package com.devsmart.staple.ir;


import com.devsmart.staple.AST.FunctionCall;
import com.devsmart.staple.symbol.FunctionSymbol;
import com.devsmart.staple.type.FunctionType;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionCallInst extends SSAInst {

    public final FunctionSymbol functionSymbol;
    public final Operand[] argOperands;
    public final Operand returnVal;


    public FunctionCallInst(FunctionSymbol functionSymbol, Operand retval, ArrayList<Operand> argOperands) {
        this.functionSymbol = functionSymbol;
        this.returnVal = retval;
        this.argOperands = new Operand[argOperands.size()];
        argOperands.toArray(this.argOperands);
    }

    @Override
    public String toString() {
        return String.format("%s = call %s (%s)", returnVal, functionSymbol.name, Arrays.toString(argOperands));
    }
}
