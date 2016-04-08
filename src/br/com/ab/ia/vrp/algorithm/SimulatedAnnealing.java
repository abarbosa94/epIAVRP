package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;

/*
Algorithm SA implementation
 */
public class SimulatedAnnealing {

    public HashMap<Integer,Route> routes = new HashMap<Integer,Route>();


    public void InitialSolution(Graph graph, Node[] node, int numberOfTrucks){
        for(int i = 0; i<numberOfTrucks;i++) {
            Route r = new Route();
            r.setCapacityRoute(0);
            routes.put(i, r);
            //the first node is the depot node
            for(int j = 1; j<graph.getAdjacentMatrix().length;j++) {
                if(node[j].visited == false && routes.get(i).getCapacityRoute()+ graph.getDemand()[j] < graph.getCapacity()) {
                    r.setCapacityRoute(graph.getDemand()[j]);
                    r.setRoute(j);
                    node[j].visited = true;
                }
            }
            r.setRoute(0);
            routes.put(i, r);
        }
    }

    public void MoveTransformation(Graph graph, int numberOfTrucks) {
        List<Node> minimumValues = MoveMinimumNeighborhood(graph);
        List<Integer> random = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            random.add(i);
        }
        Collections.shuffle(random);
        for (int c = 0; c< random.size(); c++) {
            int randomNumber = random.get(c);
            Node randomNode = minimumValues.get(randomNumber);
            int indexCity = (int)randomNode.getX();
            for(Integer keyRoutes: routes.keySet()){
                if(routes.get(keyRoutes).getRoute().contains(indexCity)) {
                    //indexToRemove is the index of the rout list
                    //indexCity is the 'real' index
                    int indexToRemove = routes.get(keyRoutes).getRoute().indexOf(indexCity);
                    routes.get(keyRoutes).getRoute().remove(indexToRemove);
                    routes.get(keyRoutes).setCapacityRoute(-graph.getDemand()[indexCity]);
                    break;
                }

           }
           random = new ArrayList<Integer>();
           for (int i = 0; i < routes.keySet().size(); i++) {
               random.add(i);
           }
           Collections.shuffle(random);
           for(Integer keyRoutes: random){
               if(routes.get(keyRoutes).getCapacityRoute() + graph.getDemand()[indexCity] < graph.getCapacity()) {
                   int lastIndex = routes.get(keyRoutes).getRoute().size()-1;
                   //This value is the depot return. Thus, it is an 0
                   routes.get(keyRoutes).getRoute().remove(lastIndex);
                   routes.get(keyRoutes).getRoute().add(indexCity);
                   routes.get(keyRoutes).setCapacityRoute(graph.getDemand()[indexCity]);
                   //Add the depot return again
                   routes.get(keyRoutes).getRoute().add(0);
                   break;
               }

           }
        }
    }

    private List<Node> MoveMinimumNeighborhood(Graph graph) {

        //reutilization of data structure
        //'node' index = distance value
        //X = i value
        //Y = j value
        List<Node> minimumValues = new ArrayList<Node>();
        int[][] adjacentMatrix = graph.getAdjacentMatrix();
        for(int i = 0; i<adjacentMatrix.length; i++) {
            for(int j = i+1; j<adjacentMatrix.length; j++) {
                if(i>j) {
                    break;
                }
                if(minimumValues.size()<5){
                    Node node = new Node();
                    node.setIndex(adjacentMatrix[i][j]);
                    node.setX(i);
                    node.setY(j);
                    minimumValues.add(node);
                }
                else {
                    for(Node nodeIndex: minimumValues) {
                        if(adjacentMatrix[i][j]< nodeIndex.getIndex()){
                            nodeIndex.setIndex(adjacentMatrix[i][j]);
                            nodeIndex.setX(i);
                            nodeIndex.setY(j);
                            break;
                        }
                    }
                }
            }
        }
        return minimumValues;
    }

}

