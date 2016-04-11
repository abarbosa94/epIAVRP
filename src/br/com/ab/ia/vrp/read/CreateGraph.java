package br.com.ab.ia.vrp.read;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import br.com.ab.ia.vrp.model.Edge;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;

public class CreateGraph {
    private Graph graph = new Graph();
    private BufferedReader reader;
    private Node[] nodes;
    private int[] demands;
    private int capacity;

    public CreateGraph(String filename) {
        try {
            ReadFile(filename);
            setGraph(this.nodes);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.err.format("An error occured while trying to read file: "+filename);
            e.printStackTrace();
        }
    }


    private void ReadFile(String filename) throws FileNotFoundException {
        //String filename = "/Users/abarbosa/Documents/workspace/ep1IA/src/br/com/ab/ia/vrp/read/A-VRP/A-n32-k5.vrp";

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null)
            {
              if(line.contains("NAME")) {

              }
              if(line.contains("CAPACITY")) {
                  String[] numbers = line.split("CAPACITY\\s+:\\s+");
                  for(String number: numbers) {
                      if(!number.isEmpty()) {
                          this.capacity = Integer.parseInt(number);
                      }
                  }

              }
              if(line.contains("DIMENSION")) {
                  String[] numbers = line.split("DIMENSION\\s+:\\s+");
                  for(String number: numbers) {
                      if(!number.isEmpty()) {
                          nodes = new Node[Integer.parseInt(number)];
                          demands = new int[Integer.parseInt(number)];
                      }
                  }

              }
              if(line.contains("NODE_COORD_SECTION")){
                  //All indices had a reduction by -1 to fit in array dimensions
                  //thus node with index 1 were fit into the array position 0
                  while(!(line = reader.readLine()).contains("DEMAND_SECTION")) {
                      String[] numbers = line.split("\\s+");
                      Node node = new Node();
                      int index = Integer.parseInt(numbers[1])-1;
                      node.setIndex(index);
                      node.setX(Integer.parseInt(numbers[2]));
                      node.setY(Integer.parseInt(numbers[3]));
                      nodes[index] = node;
                  }
              }
              if(line.contains("DEMAND_SECTION")) {
                  //same logic as I did to the node vector
                  while(!(line = reader.readLine()).contains("DEPOT_SECTION")) {
                      String[] numbers = line.split("\\s+");
                      demands[Integer.parseInt(numbers[0])-1] = Integer.parseInt(numbers[1]);
                  }
              }
              if(line.contains("DEPOT_SECTION")) {
                  while(!(line = reader.readLine()).contains("-1")) {
                      //System.out.println(line);
                  }
              }
            }
        }
        catch (Exception e)
        {
          System.err.format("Exception occurred trying to read '%s'.", filename);
          e.printStackTrace();
        }





    }

    public Graph getGraph() {
        return graph;
    }

    private void setGraph(Node[] nodes) {
        this.graph.setDemand(this.demands);
        this.graph.setCapacity(this.capacity);
        int dimension = this.graph.getDemand().length;
        int[][] matrix = new int[dimension][dimension];
        for(int i =0; i< nodes.length; i++) {
            for(int j = i+1; j<matrix.length;j++) {
                Edge edge = new Edge(nodes[i],nodes[j]);
                matrix[i][j] = edge.getVal();
                matrix[j][i] = edge.getVal();
            }
        }
        this.graph.setAdjacentMatrix(matrix);
        this.graph.setNodes(nodes);
    }



}
