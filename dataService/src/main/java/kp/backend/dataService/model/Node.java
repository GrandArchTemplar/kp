package kp.backend.dataService.model;

import java.util.Objects;

public class Node {
    public int id;
    public String representation;
    public boolean isFilm;

    public Node(int id, String representation, boolean isFilm) {
        this.id = id;
        this.representation = representation;
        this.isFilm = isFilm;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return Objects.equals(representation, node.representation);
    }

    @Override
    public int hashCode() {
        return representation != null ? representation.hashCode() : 0;
    }
}
