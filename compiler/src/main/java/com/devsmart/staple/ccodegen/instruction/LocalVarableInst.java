package com.devsmart.staple.ccodegen.instruction;


import com.devsmart.staple.ccodegen.CCodeGen;
import com.devsmart.staple.symbols.LocalVariable;

public class LocalVarableInst implements Instruction {
    private final LocalVariable symbol;

    public LocalVarableInst(LocalVariable variableSymbol) {
        this.symbol = variableSymbol;
    }

    @Override
    public String render() {
        return String.format("%s %s;", CCodeGen.renderType(symbol.type), symbol.name);
    }
}
