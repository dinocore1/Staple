package com.devsmart.staple.ccodegen.instruction;


public class ObjectAssignInst implements Instruction {
    private final String lvalueText;
    private final String rvalueText;

    public ObjectAssignInst(String lvalueCtxText, String rvalueCtxText) {
        this.lvalueText = lvalueCtxText;
        this.rvalueText = rvalueCtxText;
    }

    @Override
    public String render() {
        return String.format("OBJASSIGN(%s, %s);", lvalueText, rvalueText);
    }
}
