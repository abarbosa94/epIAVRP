package br.com.ab.ia.vrp;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.ab.ia.vrp.algorithm.SimulatedAnnealing;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.read.CreateGraph;

public class ProblemSolution {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        CreateGraph read = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp");
        Graph graphT = read.getGraph();
        Node[] nodes = graphT.getNodes();
        int numberOfTrucks = read.getNumberOfTrucks();
        SimulatedAnnealing sa = new SimulatedAnnealing(graphT, nodes, numberOfTrucks);
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
        result = sa.AnnealingCVRP(0.99, 1.05, 5, 5000,5000000);
        result.get(0).add(0,0);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        int count = 0;
        int current = 0;
        for(int i = 0; i<result.get(0).size(); i++) {
            if(result.get(0).get(i) == 0 && count == 0) {
                System.out.print("Rota #"+(current+1)+" ");
                count++;
            }
            else if(result.get(0).get(i) == 0 ){
                count++;
            }
            if(count==2) {
                count = 0;
                System.out.println(result.get(0).get(i));
                System.out.print("custo: "+result.get(1).get(current) +" ");
                System.out.print("demanda atendida: "+result.get(2).get(current));
                System.out.println();
                current++;
                if(i==result.get(0).size()-1) break;
                System.out.print("Rota #"+(current+1)+" ");
                count++;
            }
            else{
                System.out.print(result.get(0).get(i)+" ");
            }
        }
        int fim = 0;
        for(Integer i: result.get(1)) {
            fim+= i;
        }
        System.out.println("Custo: "+fim);
        System.out.println("Tempo: "+totalTime);

    }

}
