package kp.backend.dataService.model;

public class Triplet<A, B, C> {
    public A from;
    public B with;
    public C to;

    public Triplet(A from, B with, C to) {
        this.from = from;
        this.with = with;
        this.to = to;
    }

    @Override
    public String toString() {
        return "{ "+ from + " " + with + " " + to + " }";
    }
}
