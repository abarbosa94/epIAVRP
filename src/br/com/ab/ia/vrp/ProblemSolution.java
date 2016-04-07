package br.com.ab.ia.vrp;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.read.CreateGraph;

public class ProblemSolution {


    public static void main(String[] args) {
        CreateGraph read = new CreateGraph("/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp");
        Graph graphT = read.getGraph();
        int[][] result = graphT.getAdjacentMatrix();
        /*for(int i = 0; i<result.length; i++) {
            for(int j = 0; j<result.length; j++) {
                System.out.print(result[i][j]+" ");
            }
            System.out.println();
        }*/
        //System.out.println(result[1][2]);

    }

}
