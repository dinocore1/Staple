package com.devsmart.staple.ir;


import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.Set;

public class Dominators<V, E> {

    public static <Y, Z> Dominators<Y, Z> compute(DirectedGraph<Y, Z> graph, Y entry) {
        Dominators<Y, Z> dom = new Dominators<Y, Z>(graph, entry);
        dom.compute();
        return dom;
    }

    private final DirectedGraph<V, E> graph;
    private final V start;
    private SetMultimap<V, V> in = HashMultimap.create();
    private SetMultimap<V, V> out = HashMultimap.create();

    private Dominators(DirectedGraph<V, E> graph, V start) {
        this.graph = graph;
        this.start = start;
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

    public Set<V> getDominanceFrontiers(V node) {
        Set<V> dominated = new HashSet<V>();
        for(V n : graph.vertexSet()){
            Set<V> set = getDominators(n);
            if(set.contains(node)){
                dominated.add(n);
            }
        }

        Set<V> frontiers = new HashSet<V>();
        for(V n : graph.vertexSet()){
            if(!dominated.contains(n) && anyPredesesorsContainsAny(n, dominated)){
                frontiers.add(n);
            }
        }

        return frontiers;
    }

    private boolean anyPredesesorsContainsAny(V node, final Set<V> collection){
        return Iterables.any(getPredesesor(node), new Predicate<V>() {
            @Override
            public boolean apply(V input) {
                return collection.contains(input);
            }
        });
    }

}
