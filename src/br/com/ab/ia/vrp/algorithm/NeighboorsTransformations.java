package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import br.com.ab.ia.vrp.model.Graph;

public class NeighboorsTransformations {
    public HashMap<Integer, int[]> OneToOneExch(HashMap<Integer, int[]> routes, Graph graphDist) {
        for (int i=0; i < routes.get(0).length; i++) {
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

        return routes;

    }

    public static int[] pickNRandom(int[] array, int n) {

        List<Integer> list = new ArrayList<Integer>(array.length);
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
            for(int j = 0; j < array.length; j++) {
                if(answer[i] == array[j]) {
                    result[i] = j;
                    break;
                }
            }
        }
        //return the indices of the values that will be exchanged
        return result;

    }
}
