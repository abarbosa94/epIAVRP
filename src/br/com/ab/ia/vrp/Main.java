package br.com.ab.ia.vrp;
import java.util.ArrayList;
import java.util.Iterator;

import br.com.ab.ia.vrp.algorithm.Kmeans;
import br.com.ab.ia.vrp.algorithm.NeighboorsTransformations;
import br.com.ab.ia.vrp.model.Cluster;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.read.CreateGraph;

public class Main {

    public static void main(String args[]){
        long startTime = System.currentTimeMillis();
        CreateGraph Entrada = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/P-VRP/P-n23-k8.vrp");
        Graph grafo = Entrada.getGraph();
        int trucks = Entrada.getNumberOfTrucks();
        Kmeans k = new Kmeans(grafo);
        ArrayList<Cluster> s = k.clusterConstruction(trucks);
        int fim = 0;
        int count = 0;
        for(Cluster a : s){

            ArrayList<Integer> t = new ArrayList<Integer>(k.NN(a));
            t.add(0,0);
            t.add(0);
            Iterator<Integer> it = t.iterator();
            System.out.print("Rota #"+(count+1)+" ");
            while(it.hasNext()){
                System.out.print(it.next()+" ");
            }
            System.out.print(" ");
            NeighboorsTransformations n = new NeighboorsTransformations();
            ArrayList<Integer> b = n.calculateDistances(t, grafo);
            ArrayList<Integer> p = n.calculateDemand(t, grafo);
            System.out.print("custo: "+b.get(1) +" ");
            System.out.print("demanda atendida: "+p.get(1));
            System.out.println();
            fim += b.get(1);
            count++;

        }

        System.out.println("Custo: "+fim);
        long endTime   = System.currentTimeMillis();
        System.out.println("Tempo: "+(endTime-startTime));

    }




}
