package br.com.ab.ia.vrp.model;

import java.util.ArrayList;
import java.util.List;


public class Route {
    private List<Integer> routes = new ArrayList<Integer>();
    private int capacityRoute;
    public List<Integer> getRoute() {
        return routes;
    }
    public void printRoute() {
        for(Integer route: routes){
            System.out.print(route+" ");
        }
        System.out.println();
    }
    public void setRoute(int route) {
        if(routes.size()==0) {
            this.routes.add(0,0);
        }
        this.routes.add(route);
    }
    public int getCapacityRoute(Graph g) {
        int totalCost =0;
        for(Integer i: routes) {
            totalCost += g.getDemand()[i];
        }
        return totalCost;
    }
    public void setCapacityRoute(int capacityRoute) {
        this.capacityRoute = this.capacityRoute + capacityRoute;
    }
    public void assignNewCapacity(int capacityRoute) {
        this.capacityRoute = capacityRoute;
    }
    public int calculateRouteCost(Graph g) {
        int totalCost = 0;
        for(int i = 1; i<routes.size();i++) {
            int current = i;
            int previous = i-1;
            int costPath = 0;
            costPath = g.getAdjacentMatrix()[previous][current];
            totalCost += costPath;
        }
        return totalCost;
    }

    public void assignRoute(List<Integer> route) {
        for(int i = 0; i< route.size(); i++) {
            if(this.routes.size()<route.size()) {
                this.routes.add(i, route.get(i));
            }
            else{
                this.routes.set(i, route.get(i));
            }
        }



    }

}
