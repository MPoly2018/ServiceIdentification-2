package tmp.uqam.stage.metamodel.jsonserializer;

import java.util.List;

public class SCMetaModel {

    private String name;
    private List<Entity> entities;
    private List<Relation> relations;

    public SCMetaModel(String name, List<Entity> entities, List<Relation> relations) {
        this.name = name;
        this.entities = entities;
        this.relations = relations;
    }
}
