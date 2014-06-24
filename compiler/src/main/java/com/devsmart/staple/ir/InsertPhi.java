package com.devsmart.staple.ir;


import com.devsmart.staple.AST.VarDecl;
import com.devsmart.staple.symbol.Symbol;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class InsertPhi implements Runnable {

    private final DirectedGraph<BasicBlock, DefaultEdge> cfg;
    private final BasicBlock start;
    private final Symbol var;
    private HashSet<BasicBlock> placed = new HashSet<BasicBlock>();

    public InsertPhi(DirectedGraph<BasicBlock, DefaultEdge> cfg, BasicBlock start, Symbol var) {
        this.cfg = cfg;
        this.start = start;
        this.var = var;

    }

    private PhiInst getPhiInst(BasicBlock block) {
        for(SSAInst inst : block.code){
            if(inst instanceof PhiInst && ((PhiInst) inst).result.tag.equals(var)){
                return (PhiInst) inst;
            }
        }
        return null;
    }

    private void addPhi(BasicBlock block, Label label, Operand value){

        PhiInst inst = getPhiInst(block);
        if(inst == null){
            Var result = new Var(var.type, var.name);
            result.tag = var;
            inst = new PhiInst(result);
            block.code.addFirst(inst);
        }
        inst.args.add(new PhiInst.Predecessor(label, value));
    }

    @Override
    public void run() {

        Dominators<BasicBlock, DefaultEdge> dom = Dominators.compute(cfg, start);
        BreadthFirstIterator<BasicBlock, DefaultEdge> it = new BreadthFirstIterator<BasicBlock, DefaultEdge>(cfg, start);

        while(it.hasNext()){
            BasicBlock block = it.next();
            for(SSAInst inst : block.code){
                if(inst instanceof MathOpInst){
                    MathOpInst dec = (MathOpInst)inst;
                    if(dec.result.tag != null && dec.result.tag.equals(var)){
                        for(BasicBlock frontier : dom.getDominanceFrontiers(block)){
                            addPhi(frontier, block.label, dec.result);
                        }
                    }

                }
            }

            Set<BasicBlock> frontieres = dom.getDominanceFrontiers(block);
            for(BasicBlock fontier : frontieres){

            }


        }


    }


}
