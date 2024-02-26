package tmp.uqam.stage.graph.visualization;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tmp.uqam.stage.genetic.configuration.GeneticConfiguration;
import tmp.uqam.stage.genetic.criteria.ManagerialGoal;
import tmp.uqam.stage.main.Orchestrator;
import tmp.uqam.stage.slicing.WSSlicing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Controller class to handle all the visualization and fxml logic for the main window of the program
 */
public class VisualizationController {

    public WebView browser;

    public TextField popSize;
    public TextField crossoverRate;
    public TextField eliteNumber;
    public TextField tournamentSize;
    public TextField mutationRate;

    public Slider cohesion;
    public Slider coupling;
    public Slider complexity;
    public Slider compCount;
    public Slider compSize;

    public Button step1;
    public Button step5;
    public Button step10;


    public StackPane infoControls;
    public Label nbWS;

    public ProgressBar progressBar;

    public Button lessServices;
    public Button moreServices;
    public Button validateSlicing;

    public LineChart chart;
    public Text fittestInfo;

    public HBox lessMoreContainer;
    public StackPane validateContainer;

    private Orchestrator parent;
    private int currentProposalId;
    private WSSlicing currentElite;
    private List<WSSlicing> proposals;
    private WebEngine webEngine;
    private Map<ManagerialGoal, Double> weights;
    private GeneticConfiguration config;
    private XYChart.Series series;

    /**
     * Initialize basic components of the visualization and binds it to the orchestrator
     */
    public void initialize(SerializedGraph graph, List<WSSlicing> sliceProposals, Orchestrator parent) {
        this.parent = parent;
        this.currentProposalId = 0;
        this.proposals = sliceProposals;
        nbWS.setText("Number of WebServices : " + proposals.get(currentProposalId).getNbWS());
        config = GeneticConfiguration.getConfig();
        webEngine = browser.getEngine();
        webEngine.load((getClass().getClassLoader().getResource("FXML/baseGraph.html")).toString());
        popSize.setText(Integer.toString(config.getPopSize()));
        crossoverRate.setText(Double.toString(config.getCrossoverRate()));
        mutationRate.setText(Double.toString(config.getMutationRate()));
        eliteNumber.setText(Integer.toString(config.getEliteNumber()));
        tournamentSize.setText(Integer.toString(config.getTournamentSize()));
        step1.setDisable(true);
        step5.setDisable(true);
        step10.setDisable(true);
        popSize.textProperty().addListener((obs, oldValue, newValue) -> config.setPopSize(Integer.parseInt(newValue)));
        crossoverRate.textProperty().addListener((obs, oldValue, newValue) -> config.setCrossoverRate(Double.parseDouble(newValue)));
        mutationRate.textProperty().addListener((obs, oldValue, newValue) -> config.setMutationRate(Double.parseDouble(newValue)));
        eliteNumber.textProperty().addListener((obs, oldValue, newValue) -> config.setEliteNumber(Integer.parseInt(newValue)));
        tournamentSize.textProperty().addListener((obs, oldValue, newValue) -> config.setTournamentSize(Integer.parseInt(newValue)));
        series = new XYChart.Series();
        series.setName("Generations");
        chart.setCreateSymbols(false);
        initManagerialGoals();
        initGraph(graph);
    }

    /**
     * Utility method to set the weights of the managerials goals on the sliders and bind them
     */
    private void initManagerialGoals() {
        Map<ManagerialGoal, Slider> managerialGoals = new EnumMap<>(ManagerialGoal.class);
        managerialGoals.put(ManagerialGoal.COHESION, cohesion);
        managerialGoals.put(ManagerialGoal.COUPLING, coupling);
        managerialGoals.put(ManagerialGoal.COMPLEXITY, complexity);
        managerialGoals.put(ManagerialGoal.COMPONENT_COUNT, compCount);
        managerialGoals.put(ManagerialGoal.COMPONENT_SIZE, compSize);
        weights = config.getWeights();
        for (Map.Entry<ManagerialGoal, Slider> entry : managerialGoals.entrySet()) {
            entry.getValue().setValue((weights.get(entry.getKey())) * 10);
        }
        for (Map.Entry<ManagerialGoal, Slider> entry : managerialGoals.entrySet()) {
            entry.getValue().valueProperty().addListener((obs, oldValue, newValue) -> {
                weights.put(entry.getKey(), newValue.doubleValue());
                config.setWeights(
                        weights.get(ManagerialGoal.COHESION),
                        weights.get(ManagerialGoal.COUPLING), weights.get(ManagerialGoal.COMPLEXITY),
                        weights.get(ManagerialGoal.COMPONENT_COUNT),
                        weights.get(ManagerialGoal.COMPONENT_SIZE));
            });
        }
    }

    /**
     * Set the data for the graph the first time
     */
    private void initGraph(SerializedGraph graph) {
        String script = "setTheData(" + graph.getVertices() + "," + graph.getEdges() + ")";
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript(script);
            }
        });
    }

    /**
     * Change the graph data
     */
    public void setGraph(SerializedGraph graph, int nbServices) {
        String script = "setTheDataProposal(" + graph.getVertices() + "," + graph.getEdges() + "," + nbServices + ")";
        if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            webEngine.executeScript(script);
        }
    }

    public void updateInfos(int gen, String fitnessInfo, double maxFitness, WSSlicing eliteSlicing) {
        nbWS.setText("Generation n°" + gen +
                "\nNumber of webservices : " + config.getServiceNumber() +
                ", Number of classes : " + config.getClassNumber());
        fittestInfo.setText(fitnessInfo);
        series.getData().add(new XYChart.Data(gen, maxFitness));
        if (gen == 0) {
            chart.getData().add(series);
        }
        this.currentElite = eliteSlicing;
    }

    /**
     * Step simulation once
     */
    public void stepSimulation() {
        stepSimulationN(1);
    }

    /**
     * Step simulation five times
     */
    public void stepSimulation5() {
        stepSimulationN(5);
    }

    /**
     * Step simulation ten times
     */
    public void stepSimulation10() {
        stepSimulationN(10);
    }

    /**
     * Step the simulation for a given number of time out of the main thread
     */
    private void stepSimulationN(int times) {
        final Task task = new Task<Void>() {
            @Override
            protected Void call() {
                parent.stepSimulation(times, progressBar);
                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Reduce the number of webservices for the proposal
     */
    public void reduceNbWS() {
        if (currentProposalId > 0) {
            currentProposalId--;
        }
        nbWS.setText("Number of WebServices : " + proposals.get(currentProposalId).getNbWS());
    }

    /**
     * Increase the number of webservices for the proposal
     */
    public void increaseNbWS() {
        if (currentProposalId < (proposals.size() - 1)) {
            currentProposalId++;
        }
        nbWS.setText("Number of WebServices : " + proposals.get(currentProposalId).getNbWS());
    }

    /**
     * Validate the number of webservices and setup the simulation
     */
    public void validateNbWS() {
        step1.setDisable(false);
        step5.setDisable(false);
        step10.setDisable(false);
        lessServices.setDisable(true);
        moreServices.setDisable(true);
        lessMoreContainer.getChildren().clear();
        validateContainer.getChildren().clear();
        Button saveButton = new Button();
        saveButton.setText("Save Fittest to File");
        saveButton.setOnMouseClicked(e -> saveResults());
        validateContainer.getChildren().add(saveButton);
        parent.startEvolutionEngine(proposals.get(currentProposalId));
    }

    private void saveResults() {
        String rootPath = System.getProperty("user.home");
        File dir = new File(rootPath + File.separator + "bench");
        if (!dir.exists())
            dir.mkdirs();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(System.getProperty("user.home") + File.separator + "bench" + File.separator + "res-genetic.txt", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        currentElite.forEach(slicing -> sj.add(slicing.toString()));
        writer.print(sj.toString());
        writer.close();
    }
}