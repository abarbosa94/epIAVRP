package br.com.ab.ia.vrp;

import java.util.HashMap;

import br.com.ab.ia.vrp.algorithm.SimulatedAnnealing;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;
import br.com.ab.ia.vrp.read.CreateGraph;

public class ProblemSolution {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        CreateGraph read = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp");
        Graph graphT = read.getGraph();
        Node[] nodes = graphT.getNodes();

        SimulatedAnnealing sa = new SimulatedAnnealing(graphT, nodes, 5);
        sa.AnnealingCVRP(0.99, 1.05, 5, 5000,50000000);
        long endTime   = System.currentTimeMillis();
        HashMap<Integer, Route> solution = sa.getRoutes();
        long totalTime = endTime - startTime;
       System.out.println("***SOLUTION****");
        for(int i=0; i< solution.keySet().size();i++) {
            System.out.print("Rota #"+(i+1)+": ");
            for(int j = 0; j<solution.get(i).getRoute().size(); j++) {
                System.out.print(solution.get(i).getRoute().get(j)+" ");
            }
            System.out.print("custo: ");
            System.out.print(solution.get(i).calculateRouteCost(graphT)+" ");
            System.out.println("demanda atendida: "+solution.get(i).getCapacityRoute(graphT));

        }
        System.out.println("Custo: "+sa.costFunction(solution, graphT));
        System.out.println("Tempo: "+totalTime);

    }

}
