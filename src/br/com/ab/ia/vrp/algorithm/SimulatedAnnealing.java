package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;

/*
 Algorithm SA implementation
 */
public class SimulatedAnnealing {
    private HashMap<Integer, Route> routes;
    private Graph graph;
    private Node[] node;
    private int numberOfTrucks;

    public SimulatedAnnealing(Graph g, Node[] n, int numberOfTrucks) {
        this.graph = g;
        this.node = n;
        this.numberOfTrucks = numberOfTrucks;
        this.setRoutes(InitialSolution(this.graph, this.node,
                this.numberOfTrucks));
    }

    public void AnnealingCVRP(double alfa, double beta, double MZero, double T,
            double maxTime) {
        HashMap<Integer, Route> bestSolution = new HashMap<Integer, Route>();
        for (int i = 0; i < this.routes.size(); i++) {
            Route r = new Route();
            r.assignRoute(this.routes.get(i).getRoute());
            r.setCapacityRoute(this.routes.get(i).getCapacityRoute(graph));
            bestSolution.put(i, r);
        }
        HashMap<Integer, Route> currentS = new HashMap<Integer, Route>();
        for (int i = 0; i < this.routes.size(); i++) {
            Route r = new Route();
            r.assignRoute(this.routes.get(i).getRoute());
            r.setCapacityRoute(this.routes.get(i).getCapacityRoute(graph));
            currentS.put(i, r);
        }
        int currentCost = costFunction(currentS, this.graph);
        int bestCost = costFunction(bestSolution, this.graph);
        double time = 0;
        do {
            System.out.println(T);
            double M = MZero;
            do {

                HashMap<Integer, Route> newS = new HashMap<Integer, Route>();
                for (int i = 0; i < currentS.size(); i++) {
                    Route r = new Route();
                    r.assignRoute(currentS.get(i).getRoute());
                    r.setCapacityRoute(currentS.get(i).getCapacityRoute(graph));
                    newS.put(i, r);
                }
                HashMap<Integer, Route> solutionTest = new HashMap<Integer, Route>();
                 if(Math.random()<0.8) {
                     solutionTest = MoveTransformation(newS,this.graph);
                     if(solutionTest!=null) newS = solutionTest;
                }
                solutionTest = ReplaceHighestAverage(newS, this.graph);
                if(solutionTest!=null) newS = solutionTest;
                int newCost = costFunction(newS, this.graph);
                int deltaCost = newCost - currentCost;
                if (deltaCost < 0) {
                    currentS = newS;
                    currentCost = costFunction(currentS, this.graph);
                    if (newCost < bestCost) {
                        bestSolution = newS;
                        bestCost = costFunction(bestSolution, this.graph);
                    }

                } else if (Math.random() < Math.pow(Math.E,
                        (-(double) deltaCost / T))) {
                    currentS = newS;
                    currentCost = costFunction(currentS, this.graph);
                }

                M = M - 1;
            } while (M >= 0);
            time = time + MZero;
            T = alfa * T;
            MZero = beta * MZero;
        } while (time < maxTime && T > 0.001);

        this.setRoutes(bestSolution);
    }

    private HashMap<Integer, Route> InitialSolution(Graph graph, Node[] node,
            int numberOfTrucks) {
        HashMap<Integer, Route> routes = new HashMap<Integer, Route>();
        for (int i = 0; i < numberOfTrucks; i++) {
            Route r = new Route();
            r.setCapacityRoute(0);
            routes.put(i, r);
            // the first node is the depot node
            for (int j = 1; j < graph.getAdjacentMatrix().length; j++) {
                if (node[j].visited == false
                        && (routes.get(i).getCapacityRoute(graph) + graph
                                .getDemand()[j]) <= graph.getCapacity()) {
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

    public HashMap<Integer, Route> MoveTransformation(HashMap<Integer, Route> original, Graph graph) {
        HashMap<Integer,Route> routes = new HashMap<Integer,Route>();
        for(int keySet = 0; keySet<original.keySet().size(); keySet++) {
            Route r = new Route();
            r.assignRoute(original.get(keySet).getRoute());
            r.setCapacityRoute(original.get(keySet).getCapacityRoute(graph));
            routes.put(keySet, r);
        }
        List<Integer> minimumValues = MinimumDistance(graph, routes);
        List<Integer> random = new ArrayList<Integer>();
        for (int i = 1; i < graph.getAdjacentMatrix().length; i++) {
            if (!minimumValues.contains(i)) {
                random.add(i);
            }

        }
        Collections.shuffle(random);
        random = new ArrayList<Integer>(random.subList(0, 5));
        int routeSize = random.size();
        for (int c = 0; c < routeSize; c++) {
            int indexCustomer = random.get(c);
            for (Integer keyRoutes : routes.keySet()) {
                if (routes.get(keyRoutes).getRoute().contains(indexCustomer)) {
                    // indexToRemove is the index of the route list
                    // indexCustomer is the 'real' index, or the value inside
                    // route list
                    int indexToRemove = routes.get(keyRoutes).getRoute()
                            .indexOf(indexCustomer);
                    routes.get(keyRoutes).getRoute().remove(indexToRemove);
                    routes.get(keyRoutes).setCapacityRoute(
                            -graph.getDemand()[indexCustomer]);
                    break;
                }

            }
        }
        List<Integer> randomRoute = new ArrayList<Integer>();
        for (int i = 0; i < routes.keySet().size(); i++) {
            randomRoute.add(i);
        }
        Collections.shuffle(randomRoute);
        for (Integer customerToInsert : random) {
            int minimumIndex = -1;
            int minimumDistance = -1;
            for (Integer keyRoutes : randomRoute) {
                if ((routes.get(keyRoutes).getCapacityRoute(graph) + graph
                        .getDemand()[customerToInsert]) <= graph.getCapacity()) {
                    for (int i = 0; i < routes.get(keyRoutes).getRoute().size()-1; i++) {
                        int currentCustomer = routes.get(keyRoutes).getRoute()
                                .get(i);
                        int distance = graph.getAdjacentMatrix()[currentCustomer][customerToInsert];
                        if (minimumDistance == -1) {
                            minimumDistance = distance;
                            minimumIndex = i;
                        } else if (minimumDistance > distance) {
                            minimumDistance = distance;
                            minimumIndex = i;
                        }
                    }
                    routes.get(keyRoutes).getRoute()
                            .add(minimumIndex + 1, customerToInsert);
                    int newCapacity = routes.get(keyRoutes).getCapacityRoute(
                            graph);
                    routes.get(keyRoutes).assignNewCapacity(newCapacity);
                    break;
                }

            }
            if(minimumIndex == -1) return null;
        }

        return routes;

    }

    public HashMap<Integer,Route> ReplaceHighestAverage(HashMap<Integer,Route> original, Graph graph) {
        HashMap<Integer,Route> routes = new HashMap<Integer,Route>();
        for(int keySet = 0; keySet<original.keySet().size(); keySet++) {
            Route r = new Route();
            r.assignRoute(original.get(keySet).getRoute());
            r.setCapacityRoute(original.get(keySet).getCapacityRoute(graph));
            routes.put(keySet, r);
        }
        List<Integer> indexesToRemove = getMaxiumAverages(graph, routes);
        //remove Customers from their Routes
        int indexesToRemoveSize = indexesToRemove.size();
        for(int c = 0; c<indexesToRemoveSize; c++) {
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

        List<Integer> randomNumbers = new ArrayList<Integer>();
        for (int i = 0; i < routes.keySet().size(); i++) {
            randomNumbers.add(i);
        }

        Collections.shuffle(randomNumbers);
        if(randomNumbers.size()<5) randomNumbers = randomNumbers.subList(0, numberOfTrucks);
        else randomNumbers = randomNumbers.subList(0, 5);
        //customers for
        for(int k = 0; k< indexesToRemove.size(); k++) {
            int minimumIndex = -1;
            int minimumRoute = -1;
            double minimumAvg = Double.MAX_VALUE;
            for(int i=0; i<randomNumbers.size();i++) {
                int currentRandomRoute = randomNumbers.get(i);

                if((routes.get(currentRandomRoute).getCapacityRoute(graph) + graph.getDemand()[indexesToRemove.get(k)]) <= graph.getCapacity()) {
                    for(int j = 0; j<routes.get(currentRandomRoute).getRoute().size(); j++) {
                        double avg = graph.getAdjacentMatrix()[indexesToRemove.get(k)][i] + graph.getAdjacentMatrix()[indexesToRemove.get(k)][i+1];
                        avg = avg/2;
                        if(avg<minimumAvg) {
                            minimumAvg = avg;
                            minimumRoute = randomNumbers.get(i);
                            minimumIndex = j;

                        }
                    }
                }
            }
            if(minimumIndex==-1) {
                return null;

            }
            routes.get(minimumRoute).getRoute().add(minimumIndex+1, indexesToRemove.get(k));
            int newCapacity = routes.get(minimumRoute).getCapacityRoute(graph);
            routes.get(minimumRoute).assignNewCapacity(newCapacity);
        }

        return routes;


    }

    private List<Integer> MinimumDistance(Graph graph, HashMap<Integer, Route> routes) {

        int[][] adjacentMatrix = graph.getAdjacentMatrix();
        HashMap<Integer, Integer> forbiddenCustomers = new HashMap<Integer, Integer>();
        /*
         * for(int i = 0; i<adjacentMatrix.length; i++) { for(int j = i+1;
         * j<adjacentMatrix.length; j++) { if(i>j) { break; }
         * if(minimumValues.size()<5){ Node node = new Node();
         * node.setIndex(adjacentMatrix[i][j]); node.setX(i); node.setY(j);
         * minimumValues.add(node); } else { Collections.sort(minimumValues);
         * for(Node nodeIndex: minimumValues) { if(adjacentMatrix[i][j]<
         * nodeIndex.getIndex()){ nodeIndex.setIndex(adjacentMatrix[i][j]);
         * nodeIndex.setX(i); nodeIndex.setY(j); break; } } } }
         */
        for (Integer key : routes.keySet()) {
            for (int i = 0; i < routes.get(key).getRoute().size() - 1; i++) {
                int leftNeigh = routes.get(key).getRoute().get(i);
                int rightNeigh = routes.get(key).getRoute().get(i + 1);
                int currentDistance = adjacentMatrix[leftNeigh][rightNeigh];
                int firstPosition = 0;
                if (forbiddenCustomers.keySet().size() != 0) {
                    firstPosition = forbiddenCustomers.get(forbiddenCustomers.keySet().toArray()[0]);
                }
                if (forbiddenCustomers.keySet().size() < 5) {
                    forbiddenCustomers.put(leftNeigh, currentDistance);
                    forbiddenCustomers = (HashMap<Integer, Integer>) sortByValueReverse(forbiddenCustomers);
                } else if (currentDistance < firstPosition) {
                    forbiddenCustomers.put(leftNeigh, currentDistance);
                    forbiddenCustomers = (HashMap<Integer, Integer>) sortByValueReverse(forbiddenCustomers);
                    if (forbiddenCustomers.size() == 6) {
                        forbiddenCustomers.remove(forbiddenCustomers.keySet()
                                .toArray()[0]);
                    }
                }

            }
        }
        List<Integer> result = new ArrayList<Integer>(forbiddenCustomers.keySet());
        return result;
    }

    private List<Integer> getMaxiumAverages(Graph graph,
            HashMap<Integer, Route> routes) {
        HashMap<Integer, Double> customerIndexes = new HashMap<Integer, Double>();
        for (Integer keyRoute : routes.keySet()) {
            List<Integer> currentRoute = new ArrayList<Integer>(routes.get(
                    keyRoute).getRoute());
            for (int i = 1; i < currentRoute.size() - 1; i++) {
                int current = currentRoute.get(i);
                int previous = currentRoute.get(i - 1);
                int next = currentRoute.get(i + 1);
                double distAnt = 0;
                double distNxt = 0;
                distAnt = graph.getAdjacentMatrix()[current][previous];
                distNxt = graph.getAdjacentMatrix()[current][next];
                double distAvg = (distAnt + distNxt) / 2;
                if (customerIndexes.size() < 5
                        || distAvg > customerIndexes.get(customerIndexes
                                .keySet().toArray()[0])) {
                    customerIndexes.put(current, distAvg);
                    customerIndexes = (HashMap<Integer, Double>) sortByValue(customerIndexes);
                    if (customerIndexes.size() == 6) {
                        customerIndexes.remove(customerIndexes.keySet()
                                .toArray()[0]);
                    }
                }

            }
        }
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(customerIndexes.keySet());
        return result;

    }

    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
            Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(
            Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(
                map.entrySet());

        Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                return e2.getValue().compareTo(e1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : sortedEntries) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public int costFunction(HashMap<Integer, Route> routesT, Graph g) {
        int totalCost = 0;
        for (Integer keyRoute : routesT.keySet()) {
            totalCost += routesT.get(keyRoute).calculateRouteCost(g);
        }
        return totalCost;
    }

    public HashMap<Integer, Route> getRoutes() {
        return routes;
    }

    public void setRoutes(HashMap<Integer, Route> routes) {
        this.routes = routes;
    }

}
