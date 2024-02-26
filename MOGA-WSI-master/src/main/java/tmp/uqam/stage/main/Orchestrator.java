package tmp.uqam.stage.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import tmp.uqam.stage.genetic.EvolutionEngine;
import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.subjects.Individual;
import tmp.uqam.stage.graph.structure.DependencyGraph;
import tmp.uqam.stage.graph.structure.SpanningTreeGraph;
import tmp.uqam.stage.graph.visualization.VisualizationController;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.metamodel.MetaModelParser;
import tmp.uqam.stage.metamodel.jsonparser.MetaModelEnhancer;
import tmp.uqam.stage.metamodel.kdmparser.KDMMetaModelParser;
import tmp.uqam.stage.slicing.WSSlicing;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to handle the highest level logic of the program
 */
public class Orchestrator extends Application {

    private int currentStep;
    private VisualizationController child;
    private WSSlicing currentSlicing;
    private EvolutionEngine evolutionEngine;
    private DependencyGraph graph;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start the simulation by parsing the files, creating the graph and starting the visualization
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getUnnamed();
        if (parameters.size() < 2) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Not enough parameters, needs at least two\nUsage :\n" +
                    "{xmi filename in resource folder} {kdm model name} [json dynamic model filename in resource folder(optional)");
            System.exit(1);
        }
        //Parsing phase
        MetaModelParser parser = new KDMMetaModelParser(parameters.get(0), parameters.get(1), false);
        MetaModel model = parser.extractMetaModel();

        if (parameters.size() >= 3) {
            MetaModelEnhancer enhancer = new MetaModelEnhancer(parameters.get(2), false);
            enhancer.enhanceMetaModel(model);
        }

        // Graph building phase
        graph = new DependencyGraph();
        model.getClasses().forEach(graph::addVertex);
        model.getLinkWeights().forEach((link, weight) -> graph.addEdge(link.getFrom(), link.getTo(), weight));
        graph.removeOrphans(2);
        SpanningTreeGraph spanningTree = graph.getMaximumSpanningTreeAsGraph();

        // Visualization start phase
        loadAndInitVisualization(primaryStage, spanningTree);
    }

    /**
     * Setup the child stage for the visualization
     */
    private void loadAndInitVisualization(Stage stage, SpanningTreeGraph graph) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("FXML/main.fxml"));
            Parent root = loader.load();
            child = loader.getController();
            child.initialize(graph.serializeHierarchic(), graph.getSliceProposals(), this);
            Scene scene = new Scene(root, 1400, 1000);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start the evolution engine by setting its data according to the chosen slicing
     */
    public void startEvolutionEngine(WSSlicing proposal) {
        GeneticConfiguration.getConfig().setGraph(graph);
        currentSlicing = proposal;
        currentSlicing.initConfig();
        evolutionEngine = new EvolutionEngine(currentSlicing);
        currentStep = 0;
        child.updateInfos(currentStep, "Waiting for data", 0.0, new WSSlicing());
        child.setGraph(graph.serialize(), currentSlicing.getNbWS());
        System.out.println(currentSlicing);
    }

    /**
     * Execute a given number of steps with the evolution engine
     */
    public void stepSimulation(int numberStep, ProgressBar progressBar) {
        currentStep += numberStep;
        Platform.runLater(() -> progressBar.setProgress(0));
        Individual fittest = evolutionEngine.step(numberStep, progressBar);
        currentSlicing = fittest.getPhenotypeAsWSSlicing();
        Platform.runLater(() -> {
            child.setGraph(currentSlicing.serialize(), currentSlicing.getNbWS());
            child.updateInfos(currentStep, fittest.getDetailedFitnessInfo(), fittest.getFitness(), fittest.getPhenotypeAsWSSlicing());
        });
    }
}
