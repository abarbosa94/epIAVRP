package br.com.ab.ia.vrp.model;

import java.util.ArrayList;

public class Route {

    public int allowed;
    public int actual;
    public int totalCost;


    public int[] nodes;
    public Edge[] inEdges;
    public Edge[] outEdges;


    ArrayList<Edge> edges;

    public Route(int nodesNumber){
        edges = new ArrayList<Edge>();

        nodes = new int[nodesNumber];
        inEdges = new Edge[nodesNumber];
        outEdges = new Edge[nodesNumber];
    }

    public void add(Edge e){
        edges.add(e);

        outEdges[e.getN1().getIndex()] = e;
        inEdges[e.getN2().getIndex()] = e;

        e.getN1().route = this;
        e.getN2().route = this;

        totalCost+= e.getVal();
    }

    public void removeEdgeToNode(int index){
        Edge e = inEdges[index];
        outEdges[e.getN1().getIndex()] = null;

        totalCost-= e.getVal();

        edges.remove(e);
        inEdges[index] = null;
    }

    public void removeEdgeFromNode(int index){
        Edge e = outEdges[index];
        inEdges[e.getN2().getIndex()] = null;

        totalCost-=e.getVal();
        edges.remove(e);
        outEdges[index] = null;
    }

    public int predecessor(int nodeIndex){
        return inEdges[nodeIndex].getN1().getIndex();
    }


    public int successor(int nodeIndex){
        return outEdges[nodeIndex].getN2().getIndex();
    }

    public boolean merge(Route r2,Edge mergingEdge){

        int from = mergingEdge.getN1().getIndex();
        int to = mergingEdge.getN2().getIndex();

        int predecessorI = this.predecessor(from);
        int predecessorJ = r2.predecessor(to);

        int successorI = this.successor(from);
        int successorJ = r2.successor(to);

        if(successorI == 0 && predecessorJ == 0){
            this.removeEdgeToNode(0);
            r2.removeEdgeFromNode(0);
            for(Edge e:r2.edges){
                this.add(e);
            }
            this.actual+= r2.actual;
            this.add(mergingEdge);
            return true;

        }else if(successorJ == 0 && predecessorI == 0){
            mergingEdge.reverse();
            this.removeEdgeFromNode(0);
            r2.removeEdgeToNode(0);
            for(Edge e:r2.edges){
                this.add(e);
            }
            this.actual+= r2.actual;
            this.add(mergingEdge);
            return true;
        }

        return false;
    }
}
