package com.devsmart.staple;

import com.devsmart.staple.ir.Dominators;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class DominanceTest {

    class Node {
        final String name;

        public Node(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }




    @Test
    public void DominatorTest() throws Exception {

        DirectedGraph<Node, DefaultEdge> graph;
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

        graph = new DefaultDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);

        for(int i=1;i<11;i++){
            Node node = new Node(String.format("%d", i));
            nodes.put(i, node);
            graph.addVertex(node);
        }

        graph.addEdge(nodes.get(1), nodes.get(2));
        graph.addEdge(nodes.get(1), nodes.get(3));
        graph.addEdge(nodes.get(2), nodes.get(3));
        graph.addEdge(nodes.get(3), nodes.get(4));
        graph.addEdge(nodes.get(4), nodes.get(3));
        graph.addEdge(nodes.get(4), nodes.get(5));
        graph.addEdge(nodes.get(4), nodes.get(6));
        graph.addEdge(nodes.get(5), nodes.get(7));
        graph.addEdge(nodes.get(6), nodes.get(7));
        graph.addEdge(nodes.get(7), nodes.get(4));
        graph.addEdge(nodes.get(7), nodes.get(8));
        graph.addEdge(nodes.get(8), nodes.get(3));
        graph.addEdge(nodes.get(8), nodes.get(9));
        graph.addEdge(nodes.get(8), nodes.get(10));
        graph.addEdge(nodes.get(9), nodes.get(1));
        graph.addEdge(nodes.get(10), nodes.get(7));

        Dominators<Node, DefaultEdge> dom = Dominators.compute(graph, nodes.get(1));

        Set<Node> set = dom.getDominators(nodes.get(1));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(1))));

        set = dom.getDominators(nodes.get(2));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(1), nodes.get(2))));

        set = dom.getDominators(nodes.get(3));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(1), nodes.get(3))));

        set = dom.getDominators(nodes.get(4));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(1), nodes.get(3), nodes.get(4))));

        set = dom.getDominators(nodes.get(5));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(1), nodes.get(3), nodes.get(4), nodes.get(5))));


    }

    @Test
    public void dominanceFrontierTest() throws Exception {

        DirectedGraph<Node, DefaultEdge> graph;
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

        graph = new DefaultDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);

        for(int i=1;i<14;i++){
            Node node = new Node(String.format("%d", i));
            nodes.put(i, node);
            graph.addVertex(node);
        }

        graph.addEdge(nodes.get(1), nodes.get(2));
        graph.addEdge(nodes.get(1), nodes.get(9));
        graph.addEdge(nodes.get(1), nodes.get(5));
        graph.addEdge(nodes.get(2), nodes.get(3));
        graph.addEdge(nodes.get(3), nodes.get(3));
        graph.addEdge(nodes.get(3), nodes.get(4));
        graph.addEdge(nodes.get(4), nodes.get(13));
        graph.addEdge(nodes.get(5), nodes.get(6));
        graph.addEdge(nodes.get(5), nodes.get(7));
        graph.addEdge(nodes.get(6), nodes.get(4));
        graph.addEdge(nodes.get(6), nodes.get(8));
        graph.addEdge(nodes.get(7), nodes.get(8));
        graph.addEdge(nodes.get(7), nodes.get(12));
        graph.addEdge(nodes.get(8), nodes.get(5));
        graph.addEdge(nodes.get(8), nodes.get(13));
        graph.addEdge(nodes.get(9), nodes.get(10));
        graph.addEdge(nodes.get(9), nodes.get(11));
        graph.addEdge(nodes.get(10), nodes.get(12));
        graph.addEdge(nodes.get(11), nodes.get(12));
        graph.addEdge(nodes.get(12), nodes.get(13));

        Dominators<Node, DefaultEdge> dom = Dominators.compute(graph, nodes.get(1));

        Set<Node> finalset = new HashSet<Node>();
        for(Node n : graph.vertexSet()){
            Set<Node> set = dom.getDominators(n);
            if(set.contains(nodes.get(5))){
                finalset.add(n);
            }
        }

        assertTrue(finalset.equals(ImmutableSet.of(nodes.get(5), nodes.get(6), nodes.get(7), nodes.get(8))));

        Set<Node> set = dom.getDominanceFrontiers(nodes.get(5));
        assertTrue(set.equals(ImmutableSet.of(nodes.get(4), nodes.get(13), nodes.get(12))));


    }

    @Test
    public void testSimple() {
        DirectedGraph<Node, DefaultEdge> graph = new DefaultDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

        for (int i = 0; i < 4; i++) {
            Node node = new Node(String.format("%d", i));
            nodes.put(i, node);
            graph.addVertex(node);
        }

        graph.addEdge(nodes.get(0), nodes.get(1));
        graph.addEdge(nodes.get(0), nodes.get(2));
        graph.addEdge(nodes.get(1), nodes.get(3));
        graph.addEdge(nodes.get(2), nodes.get(3));

        Dominators<Node, DefaultEdge> dom = Dominators.compute(graph, nodes.get(0));
        Set<Node> frontiers = dom.getDominanceFrontiers(nodes.get(1));

        assertTrue(frontiers.equals(ImmutableSet.of(nodes.get(3))));

    }
}
