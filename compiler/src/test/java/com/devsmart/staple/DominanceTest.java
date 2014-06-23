package com.devsmart.staple;

import com.devsmart.staple.ir.BasicBlock;
import com.devsmart.staple.ir.Dominators;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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


    DirectedGraph<Node, DefaultEdge> graph;
    HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();

    @Before
    public void createGraph() {
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
    }


    @Test
    public void test() throws Exception {

        Dominators<Node, DefaultEdge> dom = Dominators.compute(graph, nodes.get(1));

        Set<Node> set = dom.getDominators(nodes.get(5));
        assertTrue(set.equals(Arrays.asList(new Node[]{nodes.get(5), nodes.get(6), nodes.get(7)})));


    }
}
