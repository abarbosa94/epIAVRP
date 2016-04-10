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
    public int getCapacityRoute() {
        return capacityRoute;
    }
    public void setCapacityRoute(int capacityRoute) {
        this.capacityRoute = this.capacityRoute + capacityRoute;
    }
    public int calculateRouteCost(Graph g) {
        int totalCost = 0;
        for(int i = 1; i<routes.size();i++) {
            int current = i;
            int previous = i-1;
            int costPath = 0;
            if(current > previous) costPath = g.getAdjacentMatrix()[previous][current];
            else if(current <= previous) costPath = g.getAdjacentMatrix()[current][previous];
            totalCost += costPath;
        }
        return totalCost;
    }

    public void assignRoute(List<Integer> route) {
        this.routes = route;
    }

}
