package br.com.ab.ia.vrp;

import java.util.HashMap;

import br.com.ab.ia.vrp.algorithm.SimulatedAnnealing;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;
import br.com.ab.ia.vrp.read.CreateGraph;

public class ProblemSolution {


    public static void main(String[] args) {
        CreateGraph read = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp");
        Graph graphT = read.getGraph();
        Node[] nodes = graphT.getNodes();
        int[][] result = graphT.getAdjacentMatrix();

        SimulatedAnnealing sa = new SimulatedAnnealing(graphT, nodes, 5);
        sa.AnnealingCVRP(0.99, 1.05, 5, 5000);
        HashMap<Integer, Route> solution = sa.getRoutes();

        //sa.InitialSolution(graphT, nodes,5);
        //sa.MoveTransformation(graphT);
        //sa.ReplaceHighestAverage(graphT);
        //int size = 0;
        /*for(int i=0; i< solution.keySet().size();i++) {
            for(int j = 0; j<solution.get(i).getRoute().size(); j++) {
                System.out.print(solution.get(i).getRoute().get(j)+" ");
            }
            System.out.println();
        }*/
        //System.out.println(size);
        /*for(int i = 0; i<result.length; i++) {
            for(int j = 0; j<result.length; j++) {
                System.out.print(result[i][j]+" ");
            }
            System.out.println();
        }*/

    }

}
