package br.com.ab.ia.vrp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cluster {

	private List<Integer> nos ;
	private double GCx;
	private double GCy;
	private int capacidade;
	public Cluster(){
		nos = new ArrayList<Integer>();
		capacidade = 0;
	}
	public void printa(){
		Iterator<Integer> it = nos.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	public int getCapacidade() {
		return capacidade;
	}
	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}

	public List<Integer> getNos() {
		return nos;
	}
	public void setNos(List<Integer> nos) {
		this.nos = nos;
	}
	public double getGCx() {
		return GCx;
	}
	public void setGCx(double gCx) {
		GCx = gCx;
	}
	public double getGCy() {
		return GCy;
	}
	public void setGCy(double gCy) {
		GCy = gCy;
	}


}
