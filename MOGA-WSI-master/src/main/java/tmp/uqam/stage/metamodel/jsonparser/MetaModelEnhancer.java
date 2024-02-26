package tmp.uqam.stage.metamodel.jsonparser;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import tmp.uqam.stage.metamodel.Link;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.metamodel.ModelReader;
import tmp.uqam.stage.slicing.ClassVertex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class to read a dynamic model in json and add the new links to the already existing metamodel
 */
public class MetaModelEnhancer extends ModelReader {

    private List<UseCase> useCases;
    private static final double DYNAMIC_WEIGHT = 0.2;

    /**
     * Parse the Use cases from the json file with the name provided
     */
    public MetaModelEnhancer(String fileName, boolean resource) {
        super(fileName, resource);
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            useCases = Arrays.asList(gson.fromJson(reader, UseCase[].class));
            useCases = useCases.stream().filter(uc -> {
                if (uc.getMessages() == null) return false;
                for (Message message : uc.getMessages()) {
                    if (message.getFrom() == null || message.getTo() == null || message.getCount() == 0) {
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Json file not found in resources directory");
        }
    }

    /**
     * Enhance the given metamodel with the links obtained from the json file
     */
    public void enhanceMetaModel(MetaModel metaModel) {
        Map<String, ClassVertex> classMap = new HashMap<>();
        for (ClassVertex classVertex : metaModel.getClasses()) {
            classMap.put(classVertex.getName(), classVertex);
        }
        Map<Link, Double> linkWeights = metaModel.getLinkWeights();
        for (UseCase useCase : useCases) {
            for (Message message : useCase.getMessages()) {
                boolean valid = true;
                ClassVertex from = classMap.get(message.getFrom());
                ClassVertex to = classMap.get(message.getTo());
                if (from == null) {
                    logClassVertexNotFound(message.getFrom());
                    valid = false;
                }
                if (to == null) {
                    logClassVertexNotFound(message.getTo());
                    valid = false;
                }
                if (valid) {
                    linkWeights.merge(new Link(from, to), message.getCount() * DYNAMIC_WEIGHT, (a, b) -> a + b);
                }
            }
        }
    }

    /**
     * Utility method to print a warning when the json is maldormed
     */
    private void logClassVertexNotFound(String className) {
        Logger.getLogger(getClass().getName()).log(Level.WARNING, "ClassVertex " + className + " in json was not found in original model");
    }
}
