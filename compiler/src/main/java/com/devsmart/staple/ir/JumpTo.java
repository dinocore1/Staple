package com.devsmart.staple.ir;

public class JumpTo extends SSAInst {

    public final Label dest;

    public JumpTo(Label dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("j %s", dest.name);
    }
}
