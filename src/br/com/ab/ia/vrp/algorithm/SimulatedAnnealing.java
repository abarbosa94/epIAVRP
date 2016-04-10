package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;

/*
Algorithm SA implementation
 */
public class SimulatedAnnealing {
    private HashMap<Integer,Route> routes;
    private Graph graph;
    private Node[] node;
    private int numberOfTrucks;
    public SimulatedAnnealing(Graph g, Node[] n, int numberOfTrucks) {
        this.graph = g;
        this.node = n;
        this.numberOfTrucks = numberOfTrucks;
        this.setRoutes(InitialSolution(this.graph,this.node,this.numberOfTrucks));
    }



    public void AnnealingCVRP(double alfa,double beta,double MZero, double T){
        HashMap<Integer,Route> bestSolution = new HashMap<Integer,Route>();
        bestSolution = this.getRoutes();
        HashMap<Integer,Route> currentS = new HashMap<Integer,Route>();
        currentS = this.getRoutes();
        int currentCost = costFunction(currentS, this.graph);
        int bestCost = costFunction(bestSolution, this.graph);
        do {
            System.out.println(T);
            double M = MZero;
            do{
                HashMap<Integer,Route> newS = new HashMap<Integer,Route>();
               if(Math.random()<0.8) {
                   newS = MoveTransformation(currentS,this.graph);
               }
               newS = ReplaceHighestAverage(currentS, this.graph);
               int newCost = costFunction(newS, this.graph);
               int deltaCost = newCost - currentCost;
               if(deltaCost<0) {
                   currentS = newS;
                   currentCost = costFunction(currentS,this.graph);
                   if(newCost<bestCost) {
                       bestSolution = newS;
                       bestCost = costFunction(bestSolution,this.graph);
                   }
               }

               else if(Math.random() < Math.pow(Math.E, (-(double)deltaCost/T))) {
                   currentS = newS;
                   currentCost = costFunction(currentS, this.graph);
               }
               M = M-1;
            }while(M>=0);
            T = alfa*T;
            MZero = beta*MZero;
        }while(T>0.001);
        this.setRoutes(bestSolution);
    }

    private HashMap<Integer,Route> InitialSolution(Graph graph, Node[] node, int numberOfTrucks){
        HashMap<Integer,Route> routes = new HashMap<Integer,Route>();
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
        return routes;
    }


    public HashMap<Integer,Route> MoveTransformation(HashMap<Integer,Route> routes, Graph graph) {
        List<Node> minimumValues = MinimumDistance(graph);
        List<Integer> random = new ArrayList<Integer>();
        for (int i = 0; i < routes.keySet().size(); i++) {
            random.add(i);
        }
        Collections.shuffle(random);
        for (int c = 0; c< 5; c++) {
            int randomNumber = random.get(c);
            Node randomNode = minimumValues.get(randomNumber);
            int indexCustomer = (int)randomNode.getX();
            for(Integer keyRoutes: routes.keySet()){
                if(routes.get(keyRoutes).getRoute().contains(indexCustomer)) {
                    //indexToRemove is the index of the route list
                    //indexCustomer is the 'real' index, or the value inside route list
                    int indexToRemove = routes.get(keyRoutes).getRoute().indexOf(indexCustomer);
                    routes.get(keyRoutes).getRoute().remove(indexToRemove);
                    routes.get(keyRoutes).setCapacityRoute(-graph.getDemand()[indexCustomer]);
                    break;
                }

           }
           random = new ArrayList<Integer>();
           for (int i = 0; i < routes.keySet().size(); i++) {
               random.add(i);
           }
           Collections.shuffle(random);
           for(Integer keyRoutes: random){
               if(routes.get(keyRoutes).getCapacityRoute() +
                       graph.getDemand()[indexCustomer] < graph.getCapacity()) {
                   List<Integer> tmpRoutes = new ArrayList<Integer>();
                   for(Integer client: routes.get(keyRoutes).getRoute()) {
                       if(client != 0) {
                           tmpRoutes.add(client);
                       }

                   }
                   tmpRoutes.add(indexCustomer);
                   tmpRoutes.add(0, 0);
                   tmpRoutes.add(tmpRoutes.size(), 0);
                   routes.get(keyRoutes).assignRoute(tmpRoutes);

                   routes.get(keyRoutes).setCapacityRoute(graph.getDemand()[indexCustomer]);
                   break;

               }

           }

        }
        return routes;

    }

    public HashMap<Integer,Route> ReplaceHighestAverage(HashMap<Integer,Route> routes, Graph graph) {
        List<Integer> indexesToRemove = getMaxiumAverages(graph, routes);
        //remove Customers from their Routes
        for(int c = 0; c<indexesToRemove.size(); c++) {
            int indexCustomer = indexesToRemove.get(c);
            for(Integer keyRoutes: routes.keySet()){
                if(routes.get(keyRoutes).getRoute().contains(indexCustomer)) {
                    //indexToRemove is the index of the route list
                    //indexCustomer is the 'real' index, or the value inside route list
                    int indexToRemove = routes.get(keyRoutes).getRoute().indexOf(indexCustomer);
                    routes.get(keyRoutes).getRoute().remove(indexToRemove);
                    routes.get(keyRoutes).setCapacityRoute(-graph.getDemand()[indexCustomer]);
                    break;
                }

           }
        }
        //Select five random routes
        HashMap<Integer, Route> randomRoutes = new HashMap<Integer, Route>();
        List<Integer> randomNumbers = new ArrayList<Integer>();
        for (int i = 0; i < routes.keySet().size(); i++) {
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);
        randomNumbers = randomNumbers.subList(0, 5);
        for(int i=0; i<5;i++) {
            randomRoutes.put(i, routes.get(randomNumbers.get(i)));
        }

        for(int i=0; i<5;i++) {
            //route to include customer
            int keyRoutes = -1;
            keyRoutes = findMinimumRoute(graph, indexesToRemove.get(i), randomRoutes);
            //find the correspondent key in the real routes
            for (Entry<Integer, Route> maps : routes.entrySet()) {
                if(maps.getValue().equals(randomRoutes.get(keyRoutes))) {
                    keyRoutes = maps.getKey();
                    break;
                }
            }
                List<Integer> tmpRoutes = new ArrayList<Integer>();
                for(Integer client: routes.get(keyRoutes).getRoute()) {
                    if(client != 0) {
                        tmpRoutes.add(client);
                    }

                }

                tmpRoutes.add(indexesToRemove.get(i));
                tmpRoutes.add(0, 0);
                tmpRoutes.add(tmpRoutes.size(), 0);
                routes.get(keyRoutes).assignRoute(tmpRoutes);
                routes.get(keyRoutes).setCapacityRoute(graph.getDemand()[indexesToRemove.get(i)]);


        }

        return routes;


    }

    private int findMinimumRoute(Graph graph, int customer, HashMap<Integer,Route> randomRoutes) {
        //find the minimum distance route for a random customer
        //and then return the route index for that
        int globalMinimumRouteCost = 0;
        int globalMinimumRouteIndex = -1;
        for(Integer keyRoutes: randomRoutes.keySet()) {
            int antpen = randomRoutes.get(keyRoutes).getRoute().get(randomRoutes.get(keyRoutes).getRoute().size()-2);
            int newValue = 0;
            if(customer > antpen) newValue = graph.getAdjacentMatrix()[antpen][customer];
            else if(customer <= antpen) newValue = graph.getAdjacentMatrix()[customer][antpen];
            int currentPathCost = randomRoutes.get(keyRoutes).calculateRouteCost(graph)
                    -graph.getAdjacentMatrix()[0][antpen]
                    +newValue
                    +graph.getAdjacentMatrix()[0][customer];
            int currentRouteIndex = keyRoutes;
            if(globalMinimumRouteCost == 0 && (randomRoutes.get(keyRoutes).getCapacityRoute()+graph.getDemand()[customer]) <= graph.getCapacity()) {
                globalMinimumRouteCost = currentPathCost;
                globalMinimumRouteIndex = currentRouteIndex;
            }
            else if(globalMinimumRouteCost > currentPathCost && (randomRoutes.get(keyRoutes).getCapacityRoute()+graph.getDemand()[customer]) <= graph.getCapacity()) {
                globalMinimumRouteCost = currentPathCost;
                globalMinimumRouteIndex = currentRouteIndex;
            }
        }
        return globalMinimumRouteIndex;
    }

    private List<Node> MinimumDistance(Graph graph) {

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
    private List<Integer> getMaxiumAverages(Graph graph, HashMap<Integer,Route> routes) {
        List<Integer> customerIndexes = new ArrayList<Integer>();
        double[] maximumAverages = new double[5];
        for(Integer keyRoute: routes.keySet()) {
            List<Integer> currentRoute = routes.get(keyRoute).getRoute();
            for(int i = 1; i< currentRoute.size()-1; i++) {
                int current = currentRoute.get(i);
                int previous = currentRoute.get(i-1);
                int next = currentRoute.get(i+1);
                double distAnt = 0;
                double distNxt = 0;
                if(previous>current) {
                    distAnt = graph.getAdjacentMatrix()[current][previous];
                }
                else if(previous<=current) {
                    distAnt = graph.getAdjacentMatrix()[previous][current];
                }
                if(next>current) {
                    distNxt = graph.getAdjacentMatrix()[current][next];
                }
                else if(next<=current) {
                    distNxt = graph.getAdjacentMatrix()[next][current];
                }
                double distAvg = (distAnt+distNxt)/2;
                for(int j = 0; j<maximumAverages.length; j++) {
                    if(distAvg > maximumAverages[j]) {
                        maximumAverages[j] = distAvg;
                        if(j>customerIndexes.size() || customerIndexes.size() < maximumAverages.length) customerIndexes.add(j, current);
                        else customerIndexes.set(j, current);
                        break;

                    }
                }

            }
        }
        return customerIndexes;

    }


    public int costFunction(HashMap<Integer,Route> routes, Graph g) {
        int totalCost = 0;
        for(Integer keyRoute: routes.keySet()) {
            totalCost += routes.get(keyRoute).calculateRouteCost(g);
        }
        return totalCost;
    }





    public HashMap<Integer,Route> getRoutes() {
        return routes;
    }





    public void setRoutes(HashMap<Integer,Route> routes) {
        this.routes = routes;
    }

}

