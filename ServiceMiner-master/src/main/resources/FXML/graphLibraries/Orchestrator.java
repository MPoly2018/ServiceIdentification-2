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
import tmp.uqam.stage.graph.structure.ServiceTypes;
import tmp.uqam.stage.graph.structure.SpanningTreeGraph;
import tmp.uqam.stage.graph.visualization.VisualizationController;
import tmp.uqam.stage.metamodel.Link;
import tmp.uqam.stage.metamodel.LinkMap;
import tmp.uqam.stage.metamodel.LinkType;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.metamodel.MetaModelParser;
import tmp.uqam.stage.metamodel.jsonparser.MetaModelEnhancer;
import tmp.uqam.stage.metamodel.jsonserializer.Relation;
import tmp.uqam.stage.metamodel.jsonserializer.RelationType;
import tmp.uqam.stage.metamodel.kdmparser.KDMMetaModelParser;
import tmp.uqam.stage.metamodel.kdmparser.Metrics;
import tmp.uqam.stage.slicing.WSSlicing;
import tmp.uqam.stage.MinSpanningTree.Component;
import tmp.uqam.stage.MinSpanningTree.MinimumSpanningTreeClustering;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
					"{xmi filename in resource folder} {kdm model name} ");
			System.exit(1); 
		}





		//Parsing phase

		KDMMetaModelParser kdmparser= new KDMMetaModelParser(parameters.get(0), parameters.get(1), false);
		MetaModelParser parser = kdmparser;
		// MetaModelParser parser = new KDMMetaModelParser("/Users/manel/Documents/Phd/Comparison_tools/ServiceIdentification_copie/project/src/main/resources/petstore-all-result_kdm.xmi", "petstore-all", false);
		MetaModel model = parser.extractMetaModel(); 


		
		

		// Graph building phase
		graph = new DependencyGraph();
		model.getClasses().forEach(graph::addVertex);
		model.getLinkWeights().forEach((link, weight) -> graph.addEdge(link.getFrom(), link.getTo(), weight));

		model.getLinkWeights().forEach((link, weight) -> System.out.println(link.getFrom() + "\t"+ link.getTo() + "\t" +weight));




		//graph.removeOrphans(2);

		// Preliminary clustering
		Set<Component> computedComponents = new HashSet<Component>();
		MinimumSpanningTreeClustering MinumumSpanningTree = new  MinimumSpanningTreeClustering();
		//computedComponents= MinumumSpanningTree.PrintDecompositionWithLargeComponentSplit(model,15, 60);
		computedComponents= MinumumSpanningTree.PrintDecompositionWithLargeComponentSplit(model,10, 100000);
		
		
		
		Map<String,Metrics> Class_Metrics2 = new HashMap<>();

		kdmparser.Class_Metrics.forEach((classs, metricss) -> {
			Class_Metrics2.put(classs.substring(classs.lastIndexOf(".")+1), metricss); 

			

		});
		
		kdmparser.Fill_McCabe_metrics_from_file("McCabe_compiere.csv", Class_Metrics2);
		
		Class_Metrics2.forEach((key, value) -> System.out.println("class;" + key +";fanin;" + value.fanin + ";fanout;" + value.fanout+ ";if;" + value.number_if + ";try;" + value.number_try+ ";SQL queries;" + value.number_transactions+ "McCabe complexity;"+value.McCabe));//Added by Manel



		
		// Update service metric 
		computedComponents.forEach((service) -> {
			service.updateSomeMetrics(Class_Metrics2);

		});
		// Update service metric 
		computedComponents.forEach((service) -> {

			kdmparser.Update_fanin_fanout_of_a_cluster(service);

		});


		//NormalizeComputedComponents(computedComponents);

		//Normalize_Mean_ComputedComponents(computedComponents);




		ServiceTypes ServiceTypesDetection = new ServiceTypes();


		ServiceTypesDetection.calculateQuantiles(computedComponents);

		
		ServiceTypesDetection.DetectionOfUtilityServices(computedComponents, model, Class_Metrics2, kdmparser);

		ServiceTypesDetection.serviceTypesDetection(computedComponents, model, Class_Metrics2, kdmparser);
		
		

		//print the generated clusters with their remaining metrics
		
		computedComponents.forEach((key) -> {if (key.Utility) System.out.println("Utility ;" +key.id + key.toString() +"[ Fanin = " + key.FanIn + " , " +  
				" Fanout = " + key.Fanout + " , " +  
				" TotalIf = " + key.TotalIf + " , " +
				" TotalSQL = " + key.TotalSQL + " , " + 
				" TotalTry = " + key.TotalTry + " , " + 
				" Total McCabe = " + key.TotalMcCabe + " , " + 
				" Mean McCabe = " + key.MeanMcCabe  +
				" Utility= " +key.Utility + " , " +
				" Entity= " + key.Entity + " , " + 
				" Application= " +key.Application +"\n "
				);});
		
		
		computedComponents.forEach((key) -> {if (key.Entity) System.out.println("Entity ;" +key.id + key.toString() +"[ Fanin = " + key.FanIn + " , " +  
				" Fanout = " + key.Fanout + " , " +  
				" TotalIf = " + key.TotalIf + " , " +
				" TotalSQL = " + key.TotalSQL + " , " + 
				" TotalTry = " + key.TotalTry + " , " + 
				" Total McCabe = " + key.TotalMcCabe + " , " + 
				" Mean McCabe = " + key.MeanMcCabe  +
				" Utility= " +key.Utility + " , " +
				" Entity= " + key.Entity + " , " + 
				" Application= " +key.Application +"\n "
				);});
		
		
		computedComponents.forEach((key) -> {if (key.Application) System.out.println("Application ;" +key.id + key.toString() +"[ Fanin = " + key.FanIn + " , " +  
				" Fanout = " + key.Fanout + " , " +  
				" TotalIf = " + key.TotalIf + " , " +
				" TotalSQL = " + key.TotalSQL + " , " + 
				" TotalTry = " + key.TotalTry + " , " + 
				" Total McCabe = " + key.TotalMcCabe + " , " + 
				" Mean McCabe = " + key.MeanMcCabe  +
				" Utility= " +key.Utility + " , " +
				" Entity= " + key.Entity + " , " + 
				" Application= " +key.Application +"\n "
				);});
		
		
		computedComponents.forEach((key) -> {if (!key.Application & !key.Utility & !key.Entity) System.out.println("Unknown ;" +key.id + key.toString() +"[ Fanin = " + key.FanIn + " , " +  
				" Fanout = " + key.Fanout + " , " +  
				" TotalIf = " + key.TotalIf + " , " +
				" TotalSQL = " + key.TotalSQL + " , " + 
				" TotalTry = " + key.TotalTry + " , " + 
				" Total McCabe = " + key.TotalMcCabe + " , " + 
				" Mean McCabe = " + key.MeanMcCabe  +
				" Utility= " +key.Utility + " , " +
				" Entity= " + key.Entity + " , " + 
				" Application= " +key.Application +"\n "
				);});
		
		
		
		System.out.println("[");
		computedComponents.forEach((key) -> {if (key.Application ) System.out.println(key.toStringJson("Application"));}
		);
		
		computedComponents.forEach((key) -> {if (key.Utility ) System.out.println(key.toStringJson("Utility"));}
	
				);
		
		computedComponents.forEach((key) -> {if (key.Entity) System.out.println(key.toStringJson("Entity"));}
				);
		System.out.println("]");

		
		
		
				
		printAllLinks(kdmparser.linkMap); 
		
		
		int numberOfClassifiedClasses=0; 
		int numberOfUnkomwnClasses=0; 
		int totalClassesIntheGraph=0;
		
		
		StatsClasses(computedComponents, kdmparser,numberOfClassifiedClasses, numberOfUnkomwnClasses,totalClassesIntheGraph );
			
		
		//SpanningTreeGraph spanningTree = graph.getMaximumSpanningTreeAsGraph();

		// Visualization start phase
		//loadAndInitVisualization(primaryStage, spanningTree);
		
		

	}
	
	
	
	
public void StatsClasses(Set<Component> computedComponents, KDMMetaModelParser kdmparser,int numberOfClassifiedClasses, int numberOfUnkomwnClasses,int totalClassesIntheGraph ) {
		
		for (Component key : computedComponents) {
			
			if ((key.Application || key.Utility || key.Entity)) {numberOfClassifiedClasses= numberOfClassifiedClasses+key.nodes.size();
			
			}else {
				
				numberOfUnkomwnClasses= numberOfUnkomwnClasses+key.nodes.size();
				
			}
		}
		totalClassesIntheGraph= kdmparser.Class_Metrics.size();
		
		
		
		System.out.println("=======>numberOfClassifiedClasses= "+numberOfClassifiedClasses + "\n"+ "numberOfUnkomwnClasses =" + numberOfUnkomwnClasses + " \n"+ "totalClassesIntheGraph= "+ totalClassesIntheGraph);
		
		int k = totalClassesIntheGraph - numberOfClassifiedClasses;
		
		System.out.println("=======> number of unclassified classes= "+k);
		
		
		//System.out.println("=======> percentage of unknownclassification1= "+numberOfUnkomwnClasses/totalClassesIntheGraph  *100);
		
		
		
		//System.out.println("=======> percentage of reuse= "+numberOfClassifiedClasses/totalClassesIntheGraph *100);
		
		
	}

	
	
	
	
	
	public void printAllLinks(LinkMap linkMap) {

		linkMap.getMap().forEach((link, map)->


		{
			Set<LinkType> linkTypes = map.keySet();

			if (linkTypes.contains(LinkType.EXTENDS) || linkTypes.contains(LinkType.IMPLEMENTS)) {
				System.out.println(link.getFrom().getName()+ "  TO=====  "+ link.getTo().getName()  );
			}
		});
	}


	public void NormalizeComputedComponents(Set<Component> computedComponents) {
		double  min_fanin=0;
		double  max_fanin=0; 
		double  min_fanout=0;
		double  max_fanout=0;
		double  min_TotalIf=0;
		double  max_TotalIf=0; 
		double  min_TotalSQL=0;
		double  max_TotalSQL=0;
		double  min_TotalTry=0;
		double  max_TotalTry=0;


		for (Component service :computedComponents) {
			if (min_fanin>= service.FanIn) min_fanin= service.FanIn;
			if (max_fanin<= service.FanIn) max_fanin= service.FanIn;

			if (min_fanout>= service.FanIn) min_fanout= service.Fanout;
			if (max_fanout<= service.FanIn) max_fanout= service.Fanout;	


			if (min_TotalIf>= service.FanIn) min_TotalIf= service.TotalIf;
			if (max_TotalIf<= service.FanIn) max_TotalIf= service.TotalIf;

			if (min_TotalSQL>= service.FanIn) min_TotalSQL= service.TotalSQL;
			if (max_TotalSQL<= service.FanIn) max_TotalSQL= service.TotalSQL;

			if (min_TotalTry>= service.FanIn) min_TotalTry= service.TotalTry;
			if (max_TotalTry<= service.FanIn) max_TotalTry= service.TotalTry;

		};


		for (Component service :computedComponents) {

			service.normalizeAllMetrics_min_max(min_fanin, max_fanin, min_fanout, max_fanout,min_TotalSQL, max_TotalSQL, min_TotalIf, max_TotalIf, min_TotalTry, max_TotalTry);
		}

	}


	public void Normalize_Mean_ComputedComponents(Set<Component> computedComponents) {
		double  m_fanin=0;
		double  m_fanout=0;
		double  min_TotalIf=0;
		double  min_TotalSQL=0;
		double  min_TotalTry=0;

		for (Component service :computedComponents) {
			m_fanin=m_fanin+ service.FanIn;
			m_fanout=m_fanout+  service.Fanout;
			min_TotalIf = min_TotalIf+ service.TotalIf;
			min_TotalSQL= min_TotalSQL+service.TotalSQL;
			min_TotalTry= min_TotalTry+ service.TotalTry;
		};

		for (Component service :computedComponents) {

			service.normalizeAllMetrics_mean(m_fanin, m_fanout, min_TotalIf,min_TotalSQL, min_TotalTry);
		}
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
