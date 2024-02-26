package tmp.uqam.stage.MinSpanningTree;
//import ch.uzh.ifi.seal.monolith2microservices.graph.MSTGraphClusterer;
//import ch.uzh.ifi.seal.monolith2microservices.models.couplings.BaseCoupling;
//import ch.uzh.ifi.seal.monolith2microservices.models.graph.ClassNode;
//
//import ch.uzh.ifi.seal.monolith2microservices.models.graph.Component;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.slicing.ClassVertex;


public class MinimumSpanningTreeClustering {


	public Set<Component> DecompositionWithLargeComponentSplit(String Filepath, int splitTreshold, int numberOfServices){

		List<BaseCoupling> originalGraph = generateGraph(Filepath);

		// Compute result
		Set<Component> computedComponents = MSTGraphClusterer.clusterWithSplit(originalGraph,splitTreshold, numberOfServices);

		computedComponents.forEach((key) -> System.out.println("class;" + key.toString() ));//Added by Manel

		return computedComponents;
	}


	public  void PrintDecompositionWithLargeComponentSplit(String Filepath, int splitTreshold, int numberOfServices){

		List<BaseCoupling> originalGraph = generateGraph(Filepath);

		// Compute result
		Set<Component> computedComponents = MSTGraphClusterer.clusterWithSplit(originalGraph,splitTreshold, numberOfServices);

		computedComponents.forEach((key) -> System.out.println("class;" + key.toString() ));//Added by Manel

		// return computedComponents;
	}

	public Set<Component> PrintDecompositionWithLargeComponentSplit(MetaModel model, int splitTreshold, int numberOfServices){

		List<BaseCoupling> originalGraph = generateGraph(model);

		// Compute result
		Set<Component> computedComponents = MSTGraphClusterer.clusterWithSplit(originalGraph,splitTreshold, numberOfServices);
/*
		computedComponents.forEach((key) -> System.out.println("Cluster;" + key.toString() +"[ Fanin = " + key.FanIn + " , " +  
				" Fanout = " + key.Fanout + " , " +  
				" TotalIf = " + key.TotalIf + " , " +
				" TotalSQL = " + key.TotalSQL + " , " + 
				" TotalTry = " + key.TotalTry + " , " + 
				" cohesion = " + key.cohesion + " , " + 
				" coupling = " + key.coupling + ']'  ));

*/
		//computedComponents.forEach((key) -> System.out.println("cluster fan_in;" + key.FanIn ));//Added by Manel

		return computedComponents;
	}

	private List<BaseCoupling> generateGraph(MetaModel model){



		List<BaseCoupling> graph = new ArrayList<>();


		System.out.println("==============> MinSpanningTree");


		List<List<String>> records = new ArrayList<>();
		model.getLinkWeights().forEach((link, weight) -> {
			if (weight!=0 && !link.getFrom().getName().equals(link.getTo().getName()) )
			{  
				graph.add(new BaseCoupling(link.getFrom().getName(),link.getTo().getName(),weight));
			}

		});


		return graph;
	}


	private List<BaseCoupling> generateGraph(String FileName){



		List<BaseCoupling> graph = new ArrayList<>();


		System.out.println("==============> MinSpanningTree");


		
		try (BufferedReader br = new BufferedReader(new FileReader(FileName))) {
			String line;
			Integer i=0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				i++;

				if (!values[2].equals("") && !values[0].equals(values[1])) {
					System.out.println("Line :"+ i+  "Coupling="+ values[2]);
					Double coupling = Double.parseDouble(values[2]);
					graph.add(new BaseCoupling(values[0],values[1],coupling));
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		return graph;
	}
}
