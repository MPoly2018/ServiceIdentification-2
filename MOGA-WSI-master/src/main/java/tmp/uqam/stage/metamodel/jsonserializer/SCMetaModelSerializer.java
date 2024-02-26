package tmp.uqam.stage.metamodel.jsonserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tmp.uqam.stage.metamodel.LinkMap;
import tmp.uqam.stage.slicing.ClassVertex;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SCMetaModelSerializer {

    public String serialize(LinkMap linkMap, Set<ClassVertex> classes, String name) {
        List<Entity> entities = new ArrayList<>();
        for (ClassVertex classVertex : classes) {
            entities.add(new Entity(classVertex.getName()));
        }
        List<Relation> relations = linkMap.toSCRelations();
        SCMetaModel metaModel = new SCMetaModel(name, entities, relations);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(metaModel);
    }
}
