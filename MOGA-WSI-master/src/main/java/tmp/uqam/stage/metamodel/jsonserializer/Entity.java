package tmp.uqam.stage.metamodel.jsonserializer;

import java.util.Collections;
import java.util.List;

public class Entity {

    public Entity(String name) {
        this.name = name;
        this.nanoentities = Collections.singletonList(name);
    }

    private String name;
    private List<String> nanoentities;
}
