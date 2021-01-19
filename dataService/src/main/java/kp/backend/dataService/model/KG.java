package kp.backend.dataService.model;

import java.util.List;
import java.util.Set;

public class KG {
    public Set<Node> nodes;
    public List<Triplet<Node, Relation, Node>> relations;

    public KG(Set<Node> nodes, List<Triplet<Node, Relation, Node>> relations) {
        this.nodes = nodes;
        this.relations = relations;
    }
}
