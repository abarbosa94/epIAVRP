package br.com.ab.ia.vrp;
import java.util.ArrayList;

import br.com.ab.ia.vrp.algorithm.Kmeans;
import br.com.ab.ia.vrp.algorithm.NeighboorsTransformations;
import br.com.ab.ia.vrp.model.Cluster;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.read.CreateGraph;

public class Main {

    public static void main(String args[]){
        CreateGraph Entrada = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp");
        Graph grafo = Entrada.getGraph();
        int trucks = Entrada.getNumberOfTrucks();
        Kmeans k = new Kmeans(grafo);
        k.clusterConstruction(trucks);
        ArrayList<Cluster> s = k.getSolucao();
        //k.printaSolucao(solucao);
        int fim = 0;
        for(Cluster a : k.getSolucao()){
            a.printa();
            ArrayList<Integer> t = new ArrayList<Integer>(a.getNos());
            t.add(0,0);
            t.add(0);
            NeighboorsTransformations n = new NeighboorsTransformations();
            ArrayList<Integer> b = n.calculateDistances(t, grafo);
            fim += b.get(1);

        }

        System.out.println(fim);
        //k.printaSolucao(solucao);

    }

}
