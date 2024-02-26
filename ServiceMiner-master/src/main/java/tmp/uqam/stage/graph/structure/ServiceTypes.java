package tmp.uqam.stage.graph.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tmp.uqam.stage.MinSpanningTree.BaseCoupling;
import tmp.uqam.stage.MinSpanningTree.ClassNode;
import tmp.uqam.stage.MinSpanningTree.Component;
import tmp.uqam.stage.metamodel.Link;
import tmp.uqam.stage.metamodel.LinkType;
import tmp.uqam.stage.metamodel.MetaModel;
import tmp.uqam.stage.metamodel.kdmparser.KDMMetaModelParser;
import tmp.uqam.stage.metamodel.kdmparser.Metrics;

import com.google.common.math.Quantiles;
import tmp.uqam.stage.MinSpanningTree.NodeWeightPair;

public class ServiceTypes {


	public ServiceTypes() {
		super();
		// TODO Auto-generated constructor stub
	}


	double Q1_fanin; 
	double Q2_fanin; 
	double Q3_fanin; 


	double Q1_fanout; 
	double Q2_fanout; 
	double Q3_fanout; 

	double Q1_try; 



	double Q2_try; 
	double Q3_try; 


	double Q1_if; 
	double Q2_if; 
	double Q3_if;

	double Q1_SQL; 
	double Q2_SQL; 
	double Q3_SQL;






	public double percentile(int num_percentile, int[]values) {

		return Quantiles.percentiles().index(num_percentile).compute(values);


	}

	public void calculateQuantiles(Set<Component> computedComponents) {

		int [] fanins = new int[computedComponents.size()]; 
		int [] fanouts= new int[computedComponents.size()]; 

		int [] trys= new int[computedComponents.size()]; 

		int [] ifs= new int[computedComponents.size()]; 

		int [] sqls= new int[computedComponents.size()]; 

		int i=0;

		for(Component service: computedComponents) {

			fanins[i]= (int)service.FanIn;
			fanouts[i]= (int)service.Fanout;
			trys[i]= (int)service.TotalTry;
			ifs[i]= (int)service.TotalIf;
			sqls[i]=(int) service.TotalSQL;
			i++;

		}

		Q1_fanin=percentile(25, fanins); 
		Q2_fanin=percentile(50, fanins); 
		Q3_fanin= percentile(75, fanins);

		Q1_fanout=percentile(25, fanouts); 
		Q2_fanout=percentile(50, fanouts); 
		Q3_fanout=percentile(75, fanouts); 

		Q1_try=percentile(25, trys); 
		Q2_try=percentile(50, trys);
		Q3_try=percentile(75, trys);


		Q1_if=percentile(25, ifs); 
		Q2_if=percentile(50, ifs); 
		Q3_if=percentile(75, ifs); 

		Q1_SQL=percentile(25, sqls); 
		Q2_SQL=percentile(50, sqls);
		Q3_SQL=percentile(75, sqls);

		System.out.println("=========Q1 Q2 Q3 Fanout= "+ Q1_fanout +"  " + Q2_fanout +"  " + Q3_fanout   );
		System.out.println("=========Q1 Q2 Q3 Fanin= "+ Q1_fanin +"  " + Q2_fanin +"  " + Q3_fanin   );
		System.out.println("=========Q1 Q2 Q3 SQL= "+ Q1_SQL +"  " + Q2_SQL +"  " + Q3_SQL   );
		System.out.println("=========Q1 Q2 Q3 if= "+ Q1_if +"  " + Q2_if +"  " + Q3_if   );
		System.out.println("=========Q1 Q2 Q3 Try "+ Q1_try +"  " + Q2_try +"  " + Q3_try   );

	}



	public ArrayList<Long>  ConnectedComponent(Component service, Set<Component> computedComponents, MetaModel model){




		ArrayList<Long> ConnectedComponentsIDs = new ArrayList<Long>();

		long currentserviceId = service.id;


		model.getLinkWeights().forEach((link, weight) -> {
			if ( !link.getFrom().getName().equals(link.getTo().getName()) & 
					service.Service_contain_class_name(link.getFrom().getName())&
					!service.Service_contain_class_name(link.getTo().getName()) )


			{  
				//graph.add(new BaseCoupling(link.getFrom().getName(),link.getTo().getName(),weight));

				long clusterID= GetClusterIdContainingClassName(link.getTo().getName(), computedComponents    );


				if (clusterID>-1) ConnectedComponentsIDs.add(clusterID);

				System.out.println("****--------The Class: "+ link.getFrom().getName() +"   ----is connected to---- " + link.getTo().getName() +"----in cluster: " +clusterID );


			}

		});


		return ConnectedComponentsIDs; 


	}











	public ArrayList<Long>  ConnectedComponent2(Component service, Set<Component> computedComponents){


		ArrayList<Long> ConnectedComponentsIDs = new ArrayList<Long>();

		long currentserviceId = service.id;


		for (ClassNode anode: service.nodes) {


			List<NodeWeightPair>neighbors= new ArrayList<>();

			neighbors= anode.getNeighbors();

			System.out.println("*******------The Class: "+ anode.getClassName() +"   ----has Neighbor.---- " + neighbors.size());

			for (NodeWeightPair aNeighbor : neighbors) {
				long clusterID=  GetClusterIdContainingNode(aNeighbor.getNode(), computedComponents);

				if ((clusterID>0) & (clusterID!=currentserviceId)) {


					ConnectedComponentsIDs.add(clusterID);
					System.out.println("--------The Class: "+ anode.getClassName() +"   ----is connected to---- " + aNeighbor.getNode() +"----in cluster: " +clusterID );

				}

			}

		}

		return ConnectedComponentsIDs; 


	}


	public long GetClusterIdContainingNode(ClassNode node, Set<Component> computedComponents) {

		for (Component service:computedComponents ) {

			for (ClassNode anode: service.nodes) {


				if (anode.getId().equals(node.getId())) 
				{

					//System.out.println("----Class: " + node.getId()+ " is in cluster :" + service.id); 
					return service.id;
				}


			}

		}
		return -1;

	}


	public long GetClusterIdContainingClassName(String node_Name, Set<Component> computedComponents) {

		for (Component service:computedComponents ) {

			for (ClassNode anode: service.nodes) {


				if (anode.getId().equals(node_Name)) 
				{

					//System.out.println("----Class: " + node.getId()+ " is in cluster :" + service.id); 
					return service.id;
				}


			}

		}
		return -1;

	}

	public Boolean CallsEntity(Component service, Set<Component> computedComponents, MetaModel model) {

		ArrayList<Long> ConnectedComponentsIDs = new ArrayList<Long>();

		ConnectedComponentsIDs= ConnectedComponent(service, computedComponents, model);

		for (Component Aservice :computedComponents) {

			if ( ConnectedComponentsIDs.contains(Aservice.getId()) & Aservice.Entity) return true;


		}


		return false;


	}

	public Boolean CallsEntity2(Component service, Set<Component> computedComponents, MetaModel model) {

		ArrayList<Long> ConnectedComponentsIDs = new ArrayList<Long>();

		ConnectedComponentsIDs= ConnectedComponent(service, computedComponents, model);

		for (Component Aservice :computedComponents) {

			if ( ConnectedComponentsIDs.contains(Aservice.getId()) & Aservice.Entity) return true;


		}


		return false;


	}


	public void serviceTypesDetection(Set<Component> computedComponents, MetaModel model,Map<String,Metrics> Class_Metrics2 , KDMMetaModelParser kdmparser ) {


		// Detection of Entity services



		for (Component service :computedComponents) {



			
			if (  Service_Contain_Persistent(service, kdmparser)& service.getSize()<=6 & !service.Utility ) service.Entity=true;

			
//			if ((int) service.TotalSQL>0 & Service_Contain_Persistent(service, kdmparser)& service.getSize()<=6 & !service.Utility ) service.Entity=true;


			//if ((service.Fanout < Q2_fanout) & (int) service.TotalSQL>0 & service.getSize()<=6 & !service.Utility ) service.Entity=true;


			//if ((service.FanIn>=  Q3_fanin) & (int) service.TotalSQL==0 & service.Fanout < Q1_fanout   ) service.Utility = true;


			//if ((service.FanIn<  Q3_fanin) & (int) service.TotalSQL <=2 & service.Fanout>  Q2_fanout & CallsEntity(service, computedComponents, model) &  !service.Entity  ) service.Application = true;


			//if ( (int) service.TotalSQL <=3  & CallsEntity(service, computedComponents, model) & !service.Utility & !service.Entity ) service.Application = true;

			if ( (CallsEntity(service, computedComponents, model) & !service.Utility & !service.Entity) ||( (int) service.TotalMcCabe > 200 & !service.Utility & !service.Entity)   ) service.Application = true;




		}

	}


	public void DetectionOfUtilityServices(Set<Component> computedComponents, MetaModel model,Map<String,Metrics> Class_Metrics2, KDMMetaModelParser kdmparser) {


		/* Parcourir la liste des classes 
		 * détecter les utility 
		 * 
		 * very low  fanin and very low fanout and notpersistant and no sql
		 * very high fanin and very low fanout and notpersistant and no sql
		 * 
		 * Parcourir les classes utilitaires
		 * extraire les utility classes des clusters
		 * 
		 * si la classe ne se trouve dans aucun cluster la mettre dans un cluster à part 
		 * 
		 * voir comment composer les utility (couplage)
		 * 
		 * 
		 */

		ArrayList<String> Utility_Class_Names = new ArrayList<String>();

		Class_Metrics2.forEach((classs, metric) ->  {



			///	System.out.println(classs+ "   is persistent========"+ isPersistent( classs, kdmparser ) );


			//low fanin low fanout 
			if ((metric.fanin <= 36 ) &  (metric.fanout <= 12 ) & (metric.number_transactions==0) & !isPersistent( classs, kdmparser ) ) Utility_Class_Names.add(classs);

			//high fanin low fanout 
			if ((metric.fanin >= 80 ) &  (metric.fanout <= 12 ) & (metric.number_transactions==0) & !isPersistent( classs, kdmparser ) ) Utility_Class_Names.add(classs);

		}
				);




		//print utility classes

		Utility_Class_Names.forEach(className -> System.out.println("=========Utility  ======" + className   ));

		Utility_Class_Names.forEach(className -> {

			long id = GetClusterIdContainingClassName(className,computedComponents);

			if (id>=0) {

				//delete utility class from Component with id
				computedComponents.forEach(Componentt -> {
					if (Componentt.id==id) {
						//Componentt.nodes.removeIf(ClassNodee -> ClassNodee.getId()==className);
						for (ClassNode ClassNodeee : new ArrayList<>(Componentt.nodes)) {
							if (ClassNodeee.getId().equals(className)) {	
								Componentt.nodes.remove(ClassNodeee);
							}	
						}	
					}
				});

			}
			Component NewUtility = new Component(className,Class_Metrics2);
			NewUtility.Utility= true;
			computedComponents.add(NewUtility);
		});

		
		
		//cluster utility classes 
		
		ClusterUtilityClasses(computedComponents, model,Class_Metrics2, kdmparser);
	

	}


	public void ClusterUtilityClasses(Set<Component> computedComponents, MetaModel model,Map<String,Metrics> Class_Metrics2, KDMMetaModelParser kdmparser) {


		Set<Component> utilityServices = new HashSet<Component>();
		
		Set<Component> ClusteredutilityServices = new HashSet<Component>();

		computedComponents.forEach(service -> { if (service.Utility) {

			utilityServices.add(service);

		}
		} );
		
		
		 computedComponents.removeAll(utilityServices);
		 
		 
		for(Component utilityClass : utilityServices) {
			
			ArrayList<Long> ConnectedComponentsIDs = new ArrayList<Long>();

			ConnectedComponentsIDs= ConnectedComponent(utilityClass, computedComponents, model);
			
			if (ConnectedComponentsIDs.size()>0) {
				
				
				Component GroupedUtilityService= GroupAllClassesWithID(utilityServices,ConnectedComponentsIDs , Class_Metrics2);

				computedComponents.add(GroupedUtilityService);
				
				
			}else {
				computedComponents.add(utilityClass);
				
				
			}
			
			
		}
	
		
	}

	
	public Component GroupAllClassesWithID(Set<Component> utilityServices, ArrayList<Long> ConnectedComponentsIDs,Map<String,Metrics> Class_Metrics2) {
		
		
		Component GroupedUtilityService = null; 
		
		int i=1;
		for (Component comp : utilityServices ) {
			
			if (i==1) {
				
				GroupedUtilityService = new Component(comp.getNodes().get(0).getId(),Class_Metrics2 );
				GroupedUtilityService.Utility= true;
				
			}else if (ConnectedComponentsIDs.contains(comp.id)) {
				
				GroupedUtilityService.addClassNodeToComponent(comp.getNodes().get(0).getId(),Class_Metrics2 );
				
				//utilityServices.remove(comp);
				
			}
			
			i++;
				
		}
		
		
		return GroupedUtilityService;
	}
	
	

	public boolean isPersistent(String Aclass,KDMMetaModelParser kdmparser ) {


		boolean Persistent= false;
		ArrayList<String> parentsList = new ArrayList<String>();
		parentsList.add(Aclass);

		for (int i=0; i< parentsList.size(); i++) {

			//for (Map.Entry<String, Boolean> aparent : parentsList.entrySet()) {



			for (Map.Entry<Link, Map<LinkType, Integer>> entry : kdmparser.linkMap.getMap().entrySet()) {

				//System.out.println(entry.getKey() + "/" + entry.getValue());

				if (entry.getKey().getFrom().getName().equals(parentsList.get(i))) {

					Set<LinkType> linkTypes = entry.getValue().keySet();

					if (linkTypes.contains(LinkType.EXTENDS) || linkTypes.contains(LinkType.IMPLEMENTS)) {

						if (entry.getKey().getTo().getName().equals("PO")) {

							System.out.println(entry.getKey() + "/" + entry.getValue());
							System.out.println("------this class is persistent: " +Aclass);
							return true; 

						}else if (!parentsList.contains(entry.getKey().getTo().getName())) { 
							parentsList.add(entry.getKey().getTo().getName()) ;
						}
					}
				}
			}
		}


		System.out.println("---->Number Parents of: " + Aclass + "=" + parentsList.size());

		parentsList.forEach((Parent)-> System.out.println(Parent));

		return false;
	}




	public boolean isPersistent_copy(String Aclass,KDMMetaModelParser kdmparser ) {


		boolean Persistent= false;
		Map<String, Boolean> parentsList = new HashMap<String, Boolean>();
		parentsList.put(Aclass, false);



		for (Map.Entry<String, Boolean> aparent : parentsList.entrySet()) {

			if (!aparent.getValue()){

				//parent visité
				parentsList.put(aparent.getKey(), true);

				for (Map.Entry<Link, Map<LinkType, Integer>> entry : kdmparser.linkMap.getMap().entrySet()) {

					//System.out.println(entry.getKey() + "/" + entry.getValue());

					if (entry.getKey().getFrom().getName().equals(Aclass)) {

						Set<LinkType> linkTypes = entry.getValue().keySet();

						if (linkTypes.contains(LinkType.EXTENDS) || linkTypes.contains(LinkType.IMPLEMENTS)) {

							if (entry.getKey().getTo().getName().equals("PO")) {

								System.out.println(entry.getKey() + "/" + entry.getValue());
								return true; 

							}else {

								parentsList.put(entry.getKey().getTo().getName(), false);
							}
						}
					}
				}
			}
		}

		System.out.println("---->Number Parents of: " + Aclass + "=" + parentsList.size());

		parentsList.forEach((Parent,bool)-> System.out.println(Parent));

		return false;
	}



	public boolean Service_Contain_Persistent(Component Service, KDMMetaModelParser kdmparser ) {


		for (ClassNode Aclass : Service.nodes) {

			if (isPersistent(Aclass.getId(), kdmparser)) return true; 


		}

		return false;


	}


}





