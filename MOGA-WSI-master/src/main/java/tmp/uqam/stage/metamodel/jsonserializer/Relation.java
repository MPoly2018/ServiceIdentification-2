package tmp.uqam.stage.metamodel.jsonserializer;

public class Relation {

    public Relation(String origin, String destination, RelationType type) {
        this.origin = origin;
        this.destination = destination;
        this.type = type;
    }

    private String origin;
    private String destination;
    private RelationType type;
}
