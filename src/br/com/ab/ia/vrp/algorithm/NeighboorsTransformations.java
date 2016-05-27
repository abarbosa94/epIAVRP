package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;
import br.com.ab.ia.vrp.model.Route;

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

        return result;
    }

    public HashMap<Integer, ArrayList<Integer>> MoveTransformation(HashMap<Integer, ArrayList<Integer>> original, Graph graph) {
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
            for(int k = 0; k<original.get(0).size();k++) {
                if(original.get(0).get(k) == 0) currentTruck++;
                if(randomNeighValues[i] == original.get(0).get(k)){
                    leftNeigh[i] = original.get(0).get(k-1);
                    rightNeigh[i] = original.get(0).get(k+1);
                    break;
                }
            }
            original.get(0).remove(randomNeighIndex[i]);
            original.get(2).set(currentTruck, original.get(2).get(currentTruck) -
                    graph.getDemand()[randomNeighValues[i]]);
            original.get(1).set(currentTruck, original.get(1).get(currentTruck) -
                    (adjacentMatrix[leftNeigh[i]][randomNeighValues[i]] +
                    adjacentMatrix[randomNeighValues[i]][rightNeigh[i]]));


        }

        return original;

    }


    public HashMap<Integer, Route> OneToOneExch(HashMap<Integer, Route> routes, Graph graphT) {
       /* for (int i=0; i < routes.get(0).length; i++) {
            System.out.print(routes.get(0)[i]+" ");
        }
        System.out.println();
        for (int i=0; i < routes.get(1).length; i++) {
            System.out.print(routes.get(1)[i]+" ");
        }
        System.out.println();
        for (int i=0; i < routes.get(2).length; i++) {
            System.out.print(routes.get(2)[i]+" ");
        }

        int[] valuesToChange = pickNRandom(routes.get(0), 2);
        */

        return routes;

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

    private int[] pickNRandom(ArrayList<Integer> array, int n, List<Integer> forbidden) {

        List<Integer> list = new ArrayList<Integer>(array.size());
        for (int i : array) {
            if(i != 0 && !forbidden.contains(i))
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
}
