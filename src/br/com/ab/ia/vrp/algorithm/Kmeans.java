package br.com.ab.ia.vrp.algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.ab.ia.vrp.model.Cluster;
import br.com.ab.ia.vrp.model.Graph;
import br.com.ab.ia.vrp.model.Node;

public class Kmeans {
	ArrayList<Cluster> solucao = new ArrayList<Cluster>();
	ArrayList<Integer> visitados = new ArrayList<Integer>();
	Graph grafo;
	boolean adjustmentHasChanged = false;


	public Kmeans(Graph grafo) {
		this.grafo = grafo;
	}

	public ArrayList<Integer> NN(Cluster c){
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> visited = new ArrayList<Integer>();
		int atual = 0;
		int next = 0;
		visited.add(0);
		while(visited.size()!=c.getNos().size()+1){
			int index = 0;
			double dist = Double.MAX_VALUE;
			for(int i = 0;i<c.getNos().size();i++){
				next = c.getNos().get(i);
				if(grafo.getAdjacentMatrix()[atual][next]<dist && !visited.contains(next)){
					dist = grafo.getAdjacentMatrix()[atual][next];
					index = next;
				}
			}
			visited.add(index);
			result.add(index);
			atual = index;
		}
		return result;
	}


	public ArrayList<Cluster> clusterConstruction(int trucks) {
	    initialSolution(trucks);
	    ArrayList<Cluster> finalSolution = new ArrayList<Cluster>();
	    int fim = 0;
	    for(Cluster c: solucao) {
	        ArrayList<Integer> test = NN(c);
	        test.add(0,0);
            test.add(0);
            NeighboorsTransformations n = new NeighboorsTransformations();
            ArrayList<Integer> b = n.calculateDistances(test, grafo);
            fim += b.get(1);
	    }
	    for(Cluster c: solucao) {
	        Cluster a = new Cluster();
	        a.setCapacidade(c.getCapacidade());
	        a.setGCx(c.getGCx());
	        a.setGCy(c.getGCy());
	        List<Integer> nosA = c.getNos();
	        a.setNos(nosA);
	        finalSolution.add(a);
	    }

        do {
            clusterAdjustment();
            if(adjustmentHasChanged == false) {
                break;
            }
            else {
                int tmp = 0;
                for(Cluster c: solucao) {
                    ArrayList<Integer> test = NN(c);
                    test.add(0,0);
                    test.add(0);
                    NeighboorsTransformations n = new NeighboorsTransformations();
                    ArrayList<Integer> b = n.calculateDistances(test, grafo);
                    tmp += b.get(1);
                }
                adjustmentHasChanged = false;
                if(tmp<fim) {
                    finalSolution.clear();
                    for(Cluster c: solucao) {
                        Cluster a = new Cluster();
                        a.setCapacidade(c.getCapacidade());
                        a.setGCx(c.getGCx());
                        a.setGCy(c.getGCy());

                        List<Integer> nosA = c.getNos();
                        a.setNos(nosA);
                        finalSolution.add(a);
                    }
                    fim = tmp;
                }
            }
        }while(true);

        return finalSolution;

	}

	public void clusterAdjustment(){
		for(int i = 0; i<solucao.size();i++){
			Cluster c1 = solucao.get(i);
			for(int k = 0;k<c1.getNos().size();k++){

				int no = c1.getNos().get(k);
				Node atual = pegaNo(no);
				double x = atual.getX();
				double y = atual.getY();
				for(int j = 0;j<solucao.size();j++){
					Cluster c2 = solucao.get(j);
					if(i!=j && dist(x,y,c2.getGCx(), c2.getGCy())< dist(x,y,c1.getGCx(),c1.getGCy())){
						if(grafo.getDemand()[no]<= grafo.getCapacity()-c2.getCapacidade()){
						    adjustmentHasChanged = true;
							c1.getNos().remove(k);
							c2.getNos().add(no);
							c2.setCapacidade(c2.getCapacidade() + grafo.getDemand()[no]);
							c1.setCapacidade(c1.getCapacidade() - grafo.getDemand()[no]);
							recalculateGC(c1);
							recalculateGC(c2);
							break;
						}
					}
				}
			}
		}

	}

	public double dist(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow((x1-x2),2)+ Math.pow((y1-y2), 2));
	}

	public void initialSolution(int trucks){
		int [][] distancias = grafo.getAdjacentMatrix();
		for(int i = 0;i<trucks;i++){
			Cluster c1 = new Cluster();
			int centro = geometricCenter(distancias);
			setCentro(centro, c1);
			c1.getNos().add(centro);
			c1.setCapacidade(grafo.getDemand()[centro]);


			while(visitados.size()<=distancias.length-2){
			    //check if its max or min
				double min = Double.MAX_VALUE;
				int customer = -1;
				for(int j = 1;j<distancias.length;j++){
				    double tmp = dist(c1.getGCx(), c1.getGCy(), grafo.getNodes()[j].getX(), grafo.getNodes()[j].getY());
					if(min>=tmp && !visitados.contains(j)){
						min = tmp;
						customer = j;
					}
				}

				if(customer != -1 ){
					if(c1.getCapacidade() + grafo.getDemand()[customer] > grafo.getCapacity()) break;
					c1.getNos().add(customer);
					c1.setCapacidade(c1.getCapacidade() + grafo.getDemand()[customer]);
					recalculateGC(c1);
					visitados.add(customer);
				}

			}
			solucao.add(c1);
		}
	}

	public void setCentro(int centro, Cluster c){
		double x = grafo.getNodes()[centro].getX();
		double y = grafo.getNodes()[centro].getY();
		c.setGCx(x);
		c.setGCy(y);
	}

	private int geometricCenter(int[][] distancias) {
		double GC = 0;
		int visited = -1;
		for(int i = 1;i<distancias.length;i++){
			double max = distancias[0][i];
			if(max>GC && !visitados.contains(i)) {
				GC = max;
				visited = i;
			}
		}
		visitados.add(visited);
		return visited;

	}

	private Node pegaNo(int index){
		Node n = grafo.getNodes()[index];
		return n;
	}

	private void recalculateGC(Cluster c){
		double x=0;
		double y=0;
		Iterator<Integer> it = c.getNos().iterator();
		while(it.hasNext()){
			int atual = it.next();
			Node n = grafo.getNodes()[atual];
			x+=n.getX();
			y+=n.getY();
		}
		c.setGCx(x/c.getNos().size());
		c.setGCy(y/c.getNos().size());
	}

	public void printaSolucao(ArrayList<Cluster> solucao) {
		for(Cluster c: solucao){
			System.out.print("Caminho: ");
			Iterator<Integer> it = c.getNos().iterator();
			double dist = custoCaminho(c.getNos());
			while(it.hasNext()){
				int next =  it.next();
				System.out.print(next + " ");
			}
			System.out.println();
			System.out.println("Custo " + dist);
		}

	}

	private double custoCaminho(List<Integer> nos) {
		int result=0;
		int next;
		int previous;
		previous = 0;
		for(int i = 1;i<nos.size();i++){
			next = nos.get(i);
			result+= grafo.getAdjacentMatrix()[next][previous];
			previous = next;
		}
		result+= grafo.getAdjacentMatrix()[previous][0];
		return result;
	}

}
