package com.devsmart.staple.ir;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.Set;

public class Dominators<V, E> {

    public static <Y, Z> Dominators<Y, Z> compute(DirectedGraph<Y, Z> graph, Y entry) {
        Dominators<Y, Z> dom = new Dominators<Y, Z>(graph, entry);

        return dom;
    }

    private final DirectedGraph<V, E> graph;
    private final V start;
    private SetMultimap<V, V> cache = HashMultimap.create();

    private Dominators(DirectedGraph<V, E> graph, V start) {
        this.graph = graph;
        this.start = start;
    }

    public Set<V> getDominators(V node) {
        Set<V> retval = cache.get(node);
        if(!cache.containsKey(node)) {
            retval = new HashSet<V>();
            retval.add(node);
            if(!start.equals(node)) {
                for (E e : graph.incomingEdgesOf(node)) {
                    V pred = graph.getEdgeSource(e);
                    retval = Sets.intersection(retval, getDominators(pred));
                }
            }
            cache.putAll(node, retval);
        }

        return retval;
    }

}
