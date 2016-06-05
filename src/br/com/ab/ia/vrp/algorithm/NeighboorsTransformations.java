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

public class NeighboorsTransformations {

    public HashMap<Integer, ArrayList<Integer>> InitialSolution(Graph graph, int numberOfTrucks) {
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
        Node[] node = graph.getNodes();
        ArrayList<Integer> route = new ArrayList<Integer>();
        ArrayList<Integer> distances = new ArrayList<Integer>();
        ArrayList<Integer> demand = new ArrayList<Integer>();
        for (int i = 0; i < numberOfTrucks; i++) {
            // data type set as the article
            if(demand.size() == 0 || route.get(route.size()-1) == 0) demand.add(0);
            for (int j = 1; j < graph.getAdjacentMatrix().length; j++) {
                if (node[j].visited == false
                        && (demand.get(i) + graph.getDemand()[j]) <= graph.getCapacity()) {
                    demand.set(i,demand.get(i) + graph.getDemand()[j]);
                    if(route.size()==0)  distances.add(graph.getAdjacentMatrix()[0][j]);
                    else if (route.get(route.size()-1) == 0) distances.add(graph.getAdjacentMatrix()[0][j]);
                    else{
                       distances.set(i, distances.get(i)+graph.getAdjacentMatrix()[route.get(route.size()-1)][j]);
                    }
                    route.add(j);
                    node[j].visited = true;
                }
            }
            distances.set(i, distances.get(i)+graph.getAdjacentMatrix()[route.get(route.size()-1)][0]);
            route.add(0);


        }
        //hashmap- the first one is the route, the second is the distance used by each route
        //third is the demand used by each route
        result.put(0, route);
        result.put(1, distances);
        result.put(2, demand);
        for(Node nodeSingle: node) {
           nodeSingle.visited = false;
        }

        return result;
    }

    public HashMap<Integer, ArrayList<Integer>> OneToOneExch(HashMap<Integer, ArrayList<Integer>> original, Graph graph) {
        HashMap<Integer, ArrayList<Integer>> routes = clone(original);
        int[] idxToChange = pickNRandom(routes.get(0), 2);
        int tmp = routes.get(0).get(idxToChange[0]);
        routes.get(0).set(idxToChange[0], routes.get(0).get(idxToChange[1]));
        routes.get(0).set(idxToChange[1], tmp);
        ArrayList<Integer> newDemands = calculateDemand(routes.get(0), graph);
        if(newDemands == null) return null;
        ArrayList<Integer> newDistances = calculateDistances(routes.get(0), graph);
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(1).set(i, newDistances.get(i));
        }
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(2).set(i, newDemands.get(i));
        }
        return routes;

    }

    public HashMap<Integer, ArrayList<Integer>> RemoveAndInsert(HashMap<Integer, ArrayList<Integer>> original, Graph graph) {
        HashMap<Integer, ArrayList<Integer>> routes = clone(original);
        int[] idxToChange = pickNRandom(routes.get(0), 2);
        int tmp = routes.get(0).get(idxToChange[0]);
        routes.get(0).remove(idxToChange[0]);
        routes.get(0).add(idxToChange[1], tmp);
        ArrayList<Integer> newDemands = calculateDemand(routes.get(0), graph);
        if(newDemands == null) return null;
        ArrayList<Integer> newDistances = calculateDistances(routes.get(0), graph);
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(1).set(i, newDistances.get(i));
        }
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(2).set(i, newDemands.get(i));
        }
        return routes;

    }

    public HashMap<Integer, ArrayList<Integer>> PartialReversal(HashMap<Integer, ArrayList<Integer>> original, Graph graph) {
        HashMap<Integer, ArrayList<Integer>> routes = clone(original);
        int[] idxToChange = pickNRandom(routes.get(0), 2);
        idxToChange = sort(idxToChange);
        int temp = 0;
        int count = 0;
        for (int i = 0; i <= (idxToChange[1]-idxToChange[0])/2; i++) {
             temp = routes.get(0).get(idxToChange[0]+ i);
             routes.get(0).set(idxToChange[0]+ i,routes.get(0).get(idxToChange[1]-count));
             routes.get(0).set(idxToChange[1]-count, temp);
             count++;
        }

        ArrayList<Integer> newDemands = calculateDemand(routes.get(0), graph);
        if(newDemands == null) return null;
        ArrayList<Integer> newDistances = calculateDistances(routes.get(0), graph);
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(1).set(i, newDistances.get(i));
        }
        for(int i=0; i<routes.get(1).size(); i++) {
            routes.get(2).set(i, newDemands.get(i));
        }
        return routes;

    }

    private int[] sort(int[] idxToChange) {
        for(int i = 0; i< idxToChange.length-1; i++) {
            if(idxToChange[i] > idxToChange[i+1]) {
                int tmp = idxToChange[i];
                idxToChange[i] = idxToChange[i+1];
                idxToChange[i+1] = tmp;
            }
        }
        return idxToChange;
    }

    private HashMap<Integer, ArrayList<Integer>> clone(HashMap<Integer, ArrayList<Integer>> original) {
        HashMap<Integer, ArrayList<Integer>> routes = new HashMap<Integer, ArrayList<Integer>>();
        for (int i = 0; i < original.size(); i++) {
            ArrayList<Integer> current = new ArrayList<Integer>();
            for(int value: original.get(i)) {
                current.add(value);
            }
            routes.put(i, current);
        }
        return routes;
    }
    public ArrayList<Integer> calculateDemand(ArrayList<Integer> routes, Graph graph) {
        ArrayList<Integer> demands = new ArrayList<Integer>();
        int[] allDemands = graph.getDemand();
        int currentDemand = 0;
        //int currentRoute = 0;
        for(int i = 0; i< routes.size(); i++) {
            if(routes.get(i) == 0) {
                demands.add(currentDemand);
                currentDemand = 0;
            }
            currentDemand = currentDemand + allDemands[routes.get(i)];
            if(currentDemand>graph.getCapacity()) {
                return null;
            }
        }
        return demands;


    }

    private int[] pickNRandom(ArrayList<Integer> array, int n) {

        List<Integer> list = new ArrayList<Integer>(array.size());
        for (int i : array) {
            list.add(i);
        }
        Collections.shuffle(list);

        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            answer[i] = list.get(i);
        }

        int[] result = new int[n];
        for (int i = 0; i < answer.length; i++) {
            for(int j = 0; j < array.size(); j++) {
                if(answer[i] == array.get(j)) {
                    result[i] = j;
                    break;
                }
            }
        }
        //return the indices of the values that will be exchanged
        return result;

    }

    public ArrayList<Integer> calculateDistances(ArrayList<Integer> routes, Graph graph) {
        int[][] adjacentMatrix = graph.getAdjacentMatrix();
        ArrayList<Integer> newDistances = new ArrayList<Integer>();
        int currentDistance = 0;
        for(int i = 0; i < routes.size(); i++) {
            if(routes.get(i) == 0) {
                newDistances.add(currentDistance);
                currentDistance = 0;
                if(i == routes.size()-1) break;
            }
            if(i==0) {
                currentDistance = currentDistance + adjacentMatrix[0][routes.get(i)];
            }
            currentDistance = currentDistance + adjacentMatrix[routes.get(i)][routes.get(i+1)];
        }
        return newDistances;
    }




    /*
    public HashMap<Integer, ArrayList<Integer>> MoveTransformation(HashMap<Integer, ArrayList<Integer>> original, Graph graph, int numberOfTrucks) {
        int[][] adjacentMatrix = graph.getAdjacentMatrix();
        //include the depot and five neigh
        HashMap<Integer, Integer> forbiddenNeighboors = new HashMap<Integer, Integer>();
        //int[] forbiddenDistances = new int[5];
        int j = 0;
        for(int i = 0; i< original.get(0).size(); i++) {
            int leftNeigh = -1;
            int rightNeigh = -1;
            int distance = -1;
            if(original.get(0).get(i) == 0) continue;
            if(i == original.get(0).size()-1) {
                leftNeigh = original.get(0).get(i);
                rightNeigh = 0;

            }
            else {
                //its possible to ignore when the left neigh is the depot
                leftNeigh = original.get(0).get(i);
                rightNeigh = original.get(0).get(i+1);
            }
            distance = adjacentMatrix[leftNeigh][rightNeigh];
            int firstPosition = 0;
            if (forbiddenNeighboors.keySet().size() != 0) {
                firstPosition = forbiddenNeighboors.get(forbiddenNeighboors.keySet().toArray()[0]);
            }
            if (forbiddenNeighboors.keySet().size() < 5) {
                forbiddenNeighboors.put(leftNeigh, distance);
                forbiddenNeighboors = (HashMap<Integer, Integer>) sortByValueReverse(forbiddenNeighboors);
            } else if (distance < firstPosition) {
                forbiddenNeighboors.put(leftNeigh, distance);
                forbiddenNeighboors = (HashMap<Integer, Integer>) sortByValueReverse(forbiddenNeighboors);
                if (forbiddenNeighboors.size() == 6) {
                    forbiddenNeighboors.remove(forbiddenNeighboors.keySet()
                            .toArray()[0]);
                }
            }

        }
        List<Integer> forbiddenNeighboorsIndex = new ArrayList<Integer>();
        for(int i: forbiddenNeighboors.keySet()) {
            forbiddenNeighboorsIndex.add(i);
        }
        int[] randomNeighIndex = pickNRandom(original.get(0), 5, forbiddenNeighboorsIndex);
        int[] randomNeighValues = new int[5];
        int[] leftNeigh = new int[5];
        int[] rightNeigh = new int[5];
        for(int i = 0; i<5; i++) {
            randomNeighValues[i] = original.get(0).get(randomNeighIndex[i]);
        }
        for(int i = 0; i<5; i++) {

            int currentTruck = 0;
            for(int k = 0; k<original.get(0).size()-1;k++) {
                if(original.get(0).get(k) == 0) currentTruck++;
                if(randomNeighValues[i] == original.get(0).get(k)){
                    if(k==0) {
                        leftNeigh[i] = 0;
                        rightNeigh[i] = original.get(0).get(k+1);
                    }
                    else {
                        leftNeigh[i] = original.get(0).get(k-1);
                        rightNeigh[i] = original.get(0).get(k+1);
                    }
                    break;
                }
            }
            int idx = original.get(0).indexOf(randomNeighValues[i]);
            original.get(0).remove(idx);
            original.get(2).set(currentTruck, original.get(2).get(currentTruck) -
                    graph.getDemand()[randomNeighValues[i]]);
            original.get(1).set(currentTruck, (original.get(1).get(currentTruck) -
                    (adjacentMatrix[leftNeigh[i]][randomNeighValues[i]] +
                    adjacentMatrix[randomNeighValues[i]][rightNeigh[i]]))+ adjacentMatrix[leftNeigh[i]][rightNeigh[i]]);
        }
        //random routes
        List<Integer> randomRoute = new ArrayList<Integer>();
        for (int i = 0; i < numberOfTrucks; i++) {
            randomRoute.add(i);
        }
        Collections.shuffle(randomRoute);
        for(int currentNeigh: randomNeighValues) {
            int minimumIndex = -1;
            int minimumDistance = -1;
            for(int k = 0; k< randomRoute.size(); k++) {
                if ((original.get(2).get(randomRoute.get(k)) + graph
                        .getDemand()[currentNeigh]) <= graph.getCapacity()) {
                    int countZeros = 0;
                    int routeBeginIdx = 0;
                    for(int i=0; i< original.get(0).size(); i++) {
                        if(original.get(0).get(i) == 0) countZeros++;
                        if(countZeros!=randomRoute.get(k)) continue;
                        if(countZeros==0) routeBeginIdx = i;
                        else routeBeginIdx = i+1;
                        break;
                    }
                    for(int i=routeBeginIdx; i< original.get(0).size(); i++) {
                        if(original.get(0).get(i) == 0) break;
                        int currentCustomer = original.get(0).get(i);
                        int distance = graph.getAdjacentMatrix()[currentCustomer][currentNeigh];
                        if (minimumDistance == -1) {
                            minimumDistance = distance;
                            minimumIndex = i;
                        } else if (minimumDistance > distance) {
                            minimumDistance = distance;
                            minimumIndex = i;
                        }
                    }
                    original.get(0).add(minimumIndex+1, currentNeigh);
                    if(countZeros == 5 || minimumIndex == -1) {
                        System.out.println("Here");
                    }
                    original.get(1).set(countZeros, original.get(1).get(countZeros) +
                            (adjacentMatrix[original.get(0).get(minimumIndex)][original.get(0).get(minimumIndex+1)] +
                                    adjacentMatrix[original.get(0).get(minimumIndex+1)][original.get(0).get(minimumIndex+2)] -
                                    adjacentMatrix[original.get(0).get(minimumIndex)][original.get(0).get(minimumIndex+2)]
                                     ));
                    original.get(2).set(countZeros, original.get(2).get(countZeros) + graph.getDemand()[original.get(0).get(minimumIndex+1)]);
                    break;



                }
            }
            if(minimumIndex == -1) return null;
        }


        return original;

    }


    public HashMap<Integer, ArrayList<Integer>> ReplaceHighestAverage(HashMap<Integer, ArrayList<Integer>> original, Graph graph, int numberOfTrucks) {
        int[][] adjacentMatrix = graph.getAdjacentMatrix();
        List<Integer> indexesToRemove = getMaxiumAverages(graph, original.get(0));
        int indexesToRemoveSize = indexesToRemove.size();
        for(int c = 0; c<indexesToRemoveSize; c++) {
            int customer = indexesToRemove.get(c);
            int indexTrucker = locateTruckerIndex(customer, original.get(0));
            int idx = original.get(0).indexOf(customer);

            original.get(2).set(indexTrucker, original.get(2).get(indexTrucker) -
                    graph.getDemand()[customer]);
            if(idx==0) {
                original.get(1).set(indexTrucker, original.get(1).get(indexTrucker) -
                        (adjacentMatrix[0][customer] + adjacentMatrix[customer][original.get(0).get(idx+1)]) +
                        adjacentMatrix[0][original.get(0).get(idx+1)]);
            }
            else {
                original.get(1).set(indexTrucker, (original.get(1).get(indexTrucker) -
                        (adjacentMatrix[original.get(0).get(idx-1)][customer] + adjacentMatrix[customer][original.get(0).get(idx+1)]))
                        + adjacentMatrix[original.get(0).get(idx-1)][original.get(0).get(idx+1)]);
            }
            original.get(0).remove(idx);



           }

        List<Integer> randomRoute = new ArrayList<Integer>();
        for (int i = 0; i < numberOfTrucks; i++) {
            randomRoute.add(i);
        }

        Collections.shuffle(randomRoute);
        if(randomRoute.size()<5) randomRoute = randomRoute.subList(0, numberOfTrucks);
        else randomRoute = randomRoute.subList(0, 5);
        for(int newCustomer: indexesToRemove) {
            int minimumIndex = -1;
            int minimumRoute = -1;
            double minimumAvg = Double.MAX_VALUE;
            for(int i=0; i<randomRoute.size();i++) {
                if((original.get(2).get(randomRoute.get(i)) + graph
                        .getDemand()[newCustomer]) <= graph.getCapacity()) {

                    int countZeros = 0;
                    int routeBeginIdx = 0;
                    for(int k=0; k< original.get(0).size(); k++) {
                        if(original.get(0).get(k) == 0) countZeros++;
                        if(countZeros!=randomRoute.get(i)) continue;
                        if(countZeros==0) routeBeginIdx = k;
                        else routeBeginIdx = k+1;
                        break;
                    }
                    for(int j=routeBeginIdx; j< original.get(0).size(); j++) {
                        if(original.get(0).get(j) == 0) break;
                        int currentCustomer = original.get(0).get(j);
                        double avg = graph.getAdjacentMatrix()[newCustomer][currentCustomer] + graph.getAdjacentMatrix()[newCustomer][original.get(0).get(j+1)];
                        avg = avg/2;
                        if(avg<minimumAvg) {
                            minimumAvg = avg;
                            minimumRoute = randomRoute.get(i);
                            minimumIndex = j;

                        }
                    }
                }

            }
            if(minimumIndex==-1) {
                return null;

            }
            original.get(0).add(minimumIndex+1, newCustomer);
            original.get(1).set(minimumRoute, original.get(1).get(minimumRoute) +
                    (adjacentMatrix[original.get(0).get(minimumIndex)][original.get(0).get(minimumIndex+1)] +
                            adjacentMatrix[original.get(0).get(minimumIndex+1)][original.get(0).get(minimumIndex+2)] -
                            adjacentMatrix[original.get(0).get(minimumIndex)][original.get(0).get(minimumIndex+2)]
                             ));
            original.get(2).set(minimumRoute, original.get(2).get(minimumRoute) + graph.getDemand()[original.get(0).get(minimumIndex+1)]);

        }
        return original;
        }


    private int locateTruckerIndex(int customer, ArrayList<Integer> array) {
        int routeBeginIdx = 0;
        for(int i=0; i< array.size(); i++) {
            if(array.get(i) == 0) {
                routeBeginIdx = i;
            }
            if(customer==array.get(i)) {
                return routeBeginIdx;
            }
        }
        return 0;
    }
    */

    /*
    private List<Integer> getMaxiumAverages(Graph graph,
            ArrayList<Integer> routes) {
        HashMap<Integer, Double> customerIndexes = new HashMap<Integer, Double>();
        for(int i = 0;i < routes.size() -1; i++) {
            int current = -1;
            int previous = -1;
            int next = -1;
            current = routes.get(i);
            next = routes.get(i + 1);
            if(current==0) break;
            if(i==0) {
                previous = 0;

            }
            else {
                previous = routes.get(i - 1);
            }

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
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(customerIndexes.keySet());
        return result;

    }
    */

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
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


    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(
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


}
