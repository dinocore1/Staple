package com.devsmart.staple.ir;


import java.util.HashSet;
import java.util.LinkedList;

public class BasicBlock {

    public LinkedList<SSAInst> code = new LinkedList<SSAInst>();
    public HashSet<BasicBlock> outEdges = new HashSet<BasicBlock>();

}
