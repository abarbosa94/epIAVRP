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
    }

    public HashMap<Integer,  ArrayList<Integer>> AnnealingCVRP(double alfa, double beta, double MZero, double T,
            double maxTime) {
        HashMap<Integer,  ArrayList<Integer>> bestSolution = new HashMap<Integer,  ArrayList<Integer>>();
        NeighboorsTransformations solution = new NeighboorsTransformations();
        bestSolution = solution.InitialSolution(graph, numberOfTrucks);
        HashMap<Integer, ArrayList<Integer>> currentS = new HashMap<Integer, ArrayList<Integer>>();
        currentS = solution.InitialSolution(graph, numberOfTrucks);
        int currentCost = costFunction(currentS.get(1));
        int bestCost = costFunction(bestSolution.get(1));
        double time = 0;
        do {

            double M = MZero;
            do {

                HashMap<Integer, ArrayList<Integer>> newS = new HashMap<Integer, ArrayList<Integer>>();
                for (int i = 0; i < currentS.size(); i++) {
                    ArrayList<Integer> current = new ArrayList<Integer>();
                    for(int value: currentS.get(i)) {
                        current.add(value);
                    }
                    newS.put(i, current);
                }
                HashMap<Integer, ArrayList<Integer>> solutionTest;
                solutionTest = solution.OneToOneExch(newS, graph);
                if(solutionTest!=null) newS = solutionTest;
                solutionTest = solution.RemoveAndInsert(newS, graph);
                if(solutionTest!=null) newS = solutionTest;
                solutionTest = solution.PartialReversal(newS, graph);
                if(solutionTest!=null) newS = solutionTest;
                int newCost = costFunction(newS.get(1));
                int deltaCost = newCost - currentCost;
                if (deltaCost < 0) {
                    currentS = newS;
                    currentCost = costFunction(currentS.get(1));
                    if (newCost < bestCost) {
                        bestSolution = newS;
                        bestCost = costFunction(bestSolution.get(1));
                    }

                } else if (Math.random() < Math.pow(Math.E,
                        (-(double) deltaCost / T))) {
                    currentS = newS;
                    currentCost = costFunction(currentS.get(1));
                }

                M = M - 1;
            } while (M >= 0);
            time = time + MZero;
            T = alfa * T;
            MZero = beta * MZero;
        } while (time < maxTime && T > 0.001);

        return bestSolution;
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

    public int costFunction(ArrayList<Integer> routesCost) {
        int totalCost = 0;
        for (Integer currentRoute : routesCost) {
            totalCost += currentRoute;
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
