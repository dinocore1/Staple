package com.devsmart.staple.ccodegen.instruction;


public class CTextInst implements Instruction {
    private final String text;

    public CTextInst(String ouput) {
        this.text = ouput;
    }

    @Override
    public String render() {
        return text + ";";
    }
}
