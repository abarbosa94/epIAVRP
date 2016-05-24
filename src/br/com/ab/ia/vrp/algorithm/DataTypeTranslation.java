package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Route;


public class DataTypeTranslation {
    public HashMap<Integer,int[]> TranslateDataType(SimulatedAnnealing sa, Graph graphT) {
        HashMap<Integer, Route> firstSolution = sa.getRoutes();
        ArrayList<Integer> solutionTranslated = new ArrayList<>();
        ArrayList<Integer> costTranslated = new ArrayList<>();
        ArrayList<Integer> demandTranslated = new ArrayList<>();
        for(int i=0; i< firstSolution.keySet().size();i++) {
            for(int j = 1; j<firstSolution.get(i).getRoute().size()-1; j++) {
                solutionTranslated.add(firstSolution.get(i).getRoute().get(j));
            }
            costTranslated.add(firstSolution.get(i).calculateRouteCost(graphT));
            demandTranslated.add(firstSolution.get(i).getCapacityRoute(graphT));
            if(i<firstSolution.keySet().size() -1) {
                solutionTranslated.add(0);
            }


        }

        int[] translatedRoute = new int[solutionTranslated.size()];
        int[] translatedCost = new int[costTranslated.size()];
        int[] translatedDemand = new int[demandTranslated.size()];
        for (int i=0; i < translatedRoute.length; i++) {
            translatedRoute[i] = solutionTranslated.get(i).intValue();
        }
        for (int i=0; i < translatedCost.length; i++) {
            translatedCost[i] = costTranslated.get(i).intValue();
            translatedDemand[i] = demandTranslated.get(i).intValue();
        }

        HashMap<Integer, int[]> translated = new HashMap<Integer, int[]>();
        //key 0 = routes
        //key 1 = cost, or distance, for that route
        //key 2 = demand used for the current truck
        translated.put(0, translatedRoute);
        translated.put(1, translatedCost);
        translated.put(2, translatedDemand);
        return translated;

    }


}
