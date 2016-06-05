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

public class TSP {
  // number of cities
  private int n;
  // city locations
  // cost matrix
  private Graph graph;
  private List<Integer> currentNodes;
  private Node[] nodes;
  private Route routesGlobal;


  public TSP(Graph graph, List<Integer> currentNodes, Node[] nodes) {
      this.graph = graph;
      this.currentNodes = currentNodes;
      this.nodes = nodes;
      Route begin = this.InitialSolution(graph, currentNodes, nodes);
      this.setRoutes(begin);


  }

  public ArrayList<Integer> simmulatedAnnealing() {
      double alfa = 0.99;
      double beta = 1.05;
      double MZero = 5;
      double T = 1000;
      double maxTime = 5000;
      Route bestSolution = new Route();
      bestSolution.assignRoute(this.routesGlobal.getRoute());
      bestSolution.setCapacityRoute(this.routesGlobal.getCapacityRoute(graph));
      Route currentS = new Route();
      currentS.assignRoute(this.routesGlobal.getRoute());
      currentS.setCapacityRoute(this.routesGlobal.getCapacityRoute(graph));
      int currentCost = costFunction(currentS, this.graph);
      int bestCost = costFunction(bestSolution, this.graph);
      double time = 0;
      do {
          double M = MZero;
          do {
              Route newS = new Route();
              newS.assignRoute(currentS.getRoute());
              newS.assignNewCapacity(currentS.getCapacityRoute(graph));
              Route solutionTest = new Route();
             newS.getRoute().add(0,0);
             newS.getRoute().add(0);
             solutionTest.assignRoute(OneToOneExch(newS.getRoute(), this.graph));
             if(solutionTest!=null){
                 newS.getRoute().remove(0);
                 newS.getRoute().remove(newS.getRoute().size()-1);
                 solutionTest.assignNewCapacity(solutionTest.getCapacityRoute(this.graph));
                 newS = solutionTest;

             }
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
          }while (M >= 0);
          time = time + MZero;
          T = alfa * T;
          MZero = beta * MZero;
          //System.out.println(time);
      } while (time < maxTime && T > 0.001);
     ArrayList<Integer> result = new ArrayList();
    for(int i: bestSolution.getRoute()) {
        result.add(i);
    }
    return result;

  }

  public List<Integer> OneToOneExch(List<Integer> original, Graph graph) {
      ArrayList<Integer> route = new ArrayList<>();
      for(int j: original) {
          route.add(j);
      }
      int[] idxToChange = pickNRandom(route, 2);
      int tmp = route.get(idxToChange[0]);
      route.set(idxToChange[0], route.get(idxToChange[1]));
      route.set(idxToChange[1], tmp);
      NeighboorsTransformations n = new NeighboorsTransformations();
      ArrayList<Integer> newDemands = n.calculateDemand(route, graph);
      if(newDemands == null) return null;
      List<Integer> result = new ArrayList<>(route);
      return result;

  }

  private Route InitialSolution(Graph graph, List<Integer> currentNodes, Node[] node) {
      Route route = new Route();
      for(Node n: node){
          n.visited = false;
      }
      // the first node is the depot node
      for (int j = 1; j < graph.getAdjacentMatrix().length; j++) {
          if(!currentNodes.contains(j)) continue;
          if (node[j].visited == false
                  && (route.getCapacityRoute(graph) + graph
                          .getDemand()[j]) <= graph.getCapacity()) {
              route.setCapacityRoute(graph.getDemand()[j]);
              route.setRoute(j);
              node[j].visited = true;
          }
      }
      route.setRoute(0);
      for(Node n: node){
          n.visited = false;
      }
      return route;
  }

  private Route ReplaceHighestAverage(Route original, Graph graph) {
      Route route = new Route();
      route.assignRoute(original.getRoute());
      route.setCapacityRoute(original.getCapacityRoute(graph));
      List<Integer> indexesToRemove = getMaxiumAverages(graph, route);
      int indexesToRemoveSize = indexesToRemove.size();
      for(int c = 0; c<indexesToRemoveSize; c++) {
          int indexCustomer = indexesToRemove.get(c);
          if(route.getRoute().contains(indexCustomer)) {
              //indexToRemove is the index of the route list
              //indexCustomer is the 'real' index, or the value inside route list
              int indexToRemove = route.getRoute().indexOf(indexCustomer);
              route.getRoute().remove(indexToRemove);
              route.setCapacityRoute(-graph.getDemand()[indexCustomer]);
              break;
          }
      }
      for(int k = 0; k< indexesToRemove.size(); k++) {
          int minimumIndex = -1;
          double minimumAvg = Double.MAX_VALUE;
          if((route.getCapacityRoute(graph) + graph.getDemand()[indexesToRemove.get(k)]) <= graph.getCapacity()) {
              for(int j = 0; j<route.getRoute().size()-1; j++) {
                  double avg = graph.getAdjacentMatrix()[indexesToRemove.get(k)][j] + graph.getAdjacentMatrix()[indexesToRemove.get(k)][j+1];
                  avg = avg/2;
                  if(avg<minimumAvg) {
                      minimumAvg = avg;
                      minimumIndex = j;

                  }
              }
          }
          if(minimumIndex==-1) {
              return null;

          }
          route.getRoute().add(minimumIndex+1, indexesToRemove.get(k));
          int newCapacity = route.getCapacityRoute(graph);
          route.assignNewCapacity(newCapacity);
      }


      return route;
}

private Route MoveTransformation(Route original, Graph graph) {
    Route route = new Route();
    route.assignRoute(original.getRoute());
    route.setCapacityRoute(original.getCapacityRoute(graph));
    List<Integer> minimumValues = MinimumDistance(graph, route);
    List<Integer> random = new ArrayList<Integer>();
    for (int i = 0; i < this.currentNodes.size(); i++) {
        if (!minimumValues.contains(this.currentNodes.get(i))) {
            random.add(this.currentNodes.get(i));
        }
    }
    Collections.shuffle(random);
    if(random.size()>5) {
        random = new ArrayList<Integer>(random.subList(0, 5));
    }
    int routeSize = random.size();
    for (int c = 0; c < routeSize; c++) {
        int indexCustomer = random.get(c);
        if (route.getRoute().contains(indexCustomer)) {
            // indexToRemove is the index of the route list
            // indexCustomer is the 'real' index, or the value inside
            // route list
            int indexToRemove = route.getRoute()
                    .indexOf(indexCustomer);
            route.getRoute().remove(indexToRemove);
            route.setCapacityRoute(
                    -graph.getDemand()[indexCustomer]);
            break;
        }
    }

    for (Integer customerToInsert : random) {
        int minimumIndex = -1;
        int minimumDistance = -1;
        if ((route.getCapacityRoute(graph) + graph
                .getDemand()[customerToInsert]) <= graph.getCapacity()) {
            for (int i = 0; i < route.getRoute().size()-1; i++) {
                int currentCustomer = route.getRoute()
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
            route.getRoute()
                    .add(minimumIndex + 1, customerToInsert);
            int newCapacity = route.getCapacityRoute(graph);
            route.assignNewCapacity(newCapacity);
            break;
        }
        if(minimumIndex == -1) return null;
    }

    return route;
}

private List<Integer> getMaxiumAverages(Graph graph,
        Route route) {
    HashMap<Integer, Double> customerIndexes = new HashMap<Integer, Double>();
    List<Integer> currentRoute = new ArrayList<Integer>(route.getRoute());
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
    List<Integer> result = new ArrayList<Integer>();
    result.addAll(customerIndexes.keySet());
    return result;

}


private List<Integer> MinimumDistance(Graph graph, Route route) {

    int[][] adjacentMatrix = graph.getAdjacentMatrix();
    HashMap<Integer, Integer> forbiddenCustomers = new HashMap<Integer, Integer>();
    for (int i = 0; i < route.getRoute().size() - 1; i++) {
        int leftNeigh = route.getRoute().get(i);
        int rightNeigh = route.getRoute().get(i + 1);
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
    List<Integer> result = new ArrayList<Integer>(forbiddenCustomers.keySet());
    return result;
}

private void setRoutes(Route routes) {
    this.routesGlobal = routes;
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

private ArrayList<Integer> clone(ArrayList<Integer> original) {
    ArrayList<Integer> route = new ArrayList<Integer>();
    for (int customer: original) {
        route.add(customer);
    }
    return route;
}
public int costFunction(Route routesT, Graph g) {
      int totalCost = 0;
      totalCost = routesT.calculateRouteCost(g);
      return totalCost;
  }

  }