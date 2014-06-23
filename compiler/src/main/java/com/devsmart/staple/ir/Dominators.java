package com.devsmart.staple.ir;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Dominators<V, E> {

    public static <Y, Z> Dominators<Y, Z> compute(DirectedGraph<Y, Z> graph, Y entry) {
        Dominators<Y, Z> dom = new Dominators<Y, Z>(graph, entry);

        return dom;
    }

    private final DirectedGraph<V, E> graph;
    private final V start;
    private SetMultimap<V, V> in = HashMultimap.create();
    private SetMultimap<V, V> out = HashMultimap.create();

    private Dominators(DirectedGraph<V, E> graph, V start) {
        this.graph = graph;
        this.start = start;
        compute();
    }

    private Set<V> getPredesesor(V n){
        Set<V> retval = new HashSet<V>();
        for(E e : graph.incomingEdgesOf(n)){
            retval.add(graph.getEdgeSource(e));
        }
        return retval;
    }

    private void compute() {

        //initialization
        out.put(start, start);
        for(V n : graph.vertexSet()){
            if(n.equals(start)) continue;
            out.putAll(n, graph.vertexSet());
        }

        boolean changed = true;
        while(changed) {
            changed = false;
            for(V n : graph.vertexSet()) {
                if(n.equals(start)) continue;

                Set<V> newIn = null;
                for(V p : getPredesesor(n)){
                    if(newIn == null) {
                        newIn = out.get(p);
                    } else {
                        newIn = Sets.intersection(newIn, out.get(p));
                    }
                }
                in.replaceValues(n, newIn);
                Sets.SetView<V> newout = Sets.union(ImmutableSet.of(n), in.get(n));
                Set<V> oldout = out.replaceValues(n, newout);
                if(!changed) {
                    changed = !newout.equals(oldout);
                }

            }
        }


    }

    public Set<V> getDominators(V node) {
        return out.get(node);
    }

}
