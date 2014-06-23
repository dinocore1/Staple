package com.devsmart.staple.ir;


import java.util.LinkedList;

public class BasicBlock {

    public final Label label;
    public LinkedList<SSAInst> code = new LinkedList<SSAInst>();

    public BasicBlock(Label label) {
        this.label = label;
    }





}
