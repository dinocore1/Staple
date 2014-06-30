package com.devsmart.staple.ir;


import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.LinkedList;

public class CodeEmitter {
    private final LinkedList<SSAInst> code;
    private final DirectedGraph<BasicBlock, DefaultEdge> cfg;
    private final BasicBlock start;

    private int tmpNum = 0;
    private int labelNum = 0;

    public CodeEmitter(DirectedGraph<BasicBlock, DefaultEdge> cfg, BasicBlock start, LinkedList<SSAInst> code) {
        this.cfg = cfg;
        this.start = start;
        this.code = code;
    }

    public void doIt() {
        BreadthFirstIterator<BasicBlock, DefaultEdge> it = new BreadthFirstIterator<BasicBlock, DefaultEdge>(cfg, start);
        while(it.hasNext()){
            BasicBlock block = it.next();
            for(SSAInst inst : block.code){
                if(inst instanceof Label){
                    Label label = (Label)inst;
                    giveName(label);
                } else if(inst instanceof AssignmentInst) {
                    AssignmentInst op = (AssignmentInst) inst;
                    giveName(op.result);
                }
                code.add(inst);
            }
        }
    }

    private void giveName(Label label){
        if(label.name == null){
            label.name = String.format(label.nameFormat, labelNum++);
        }
    }

    private void giveName(Var var) {
        if(var.name == null){
            var.name = String.format(var.nameFormat, tmpNum++);
        }
    }
}
