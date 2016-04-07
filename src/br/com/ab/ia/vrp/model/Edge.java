package br.com.ab.ia.vrp.model;

public class Edge implements Comparable<Edge> {
    private Node n1;
    private Node n2;
    private int val;

    private Edge next;

    public Edge(Node ln1,Node ln2){
        this.setN1(ln1);
        this.setN2(ln2);
        this.setVal(ln1, ln2);
    }

    public Node getN1() {
        return n1;
    }

    public void setN1(Node n1) {
        this.n1 = n1;
    }

    public Node getN2() {
        return n2;
    }

    public void setN2(Node n2) {
        this.n2 = n2;
    }

    public Edge getNext() {
        return next;
    }

    public void setNext(Edge next) {
        this.next = next;
    }

    public int getVal() {
        return val;
    }

    public void setVal(Node n1, Node n2) {
        double n1X = this.n1.getX();
        double n2X = this.n2.getX();
        double n1Y = this.n1.getY();
        double n2Y = this.n2.getY();

        double xd = n2X - n1X;
        double yd = n2Y - n1Y;

        this.val = (int)Math.round(Math.sqrt((xd*xd + yd*yd)));
    }

    public void reverse(){
        Node swap = this.n2;
        this.n2 = n1;
        this.n1 = swap;
    }

    public void connect(Edge e1){
        setNext(e1);
    }

    @Override
    public int compareTo(Edge o) {
        if(this.val<o.val)
            return -1;
        else if(o.val == this.val)
            return 0;
        else
            return 1;
    }

}
