package kp.backend.dataService.model;

import java.util.Arrays;
import java.util.List;

public class Relation {
    public int id;
    public String representation;

    public Relation(int id, String representation) {
        this.id = id;
        this.representation = representation;
    }

    public static List<Relation> relations = Arrays.asList(
            new Relation(0, "contains_as_actor"),
            new Relation(1, "directed_by"),
            new Relation(2, "created_in_country"),
            new Relation(3, "producted_by"),
            new Relation(4, "rated_by"),
            new Relation(5, "has_genre"),
            new Relation(6, "stared_by"),
            new Relation(7, "written_by"),
            new Relation(8, "create_with_lang"),
            new Relation(9, "has_sequel"),
            new Relation(10, "created_in_year"),
            new Relation(11, "has_duration")
    );

    @Override
    public String toString() {
        return representation;
    }
}
