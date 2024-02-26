package tmp.uqam.stage.MinSpanningTree;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import tmp.uqam.stage.metamodel.kdmparser.Metrics;





public class Component {


	public Long id;


	public List<ClassNode> nodes;


	public double FanIn; 
	public double Fanout;
	public double TotalIf;
	public double TotalSQL;
	public double TotalTry;
	public double TotalMcCabe;
	public double MeanMcCabe;
	
	public double cohesion;
	public double coupling;

	public boolean Entity; 
	public boolean Utility; 
	public boolean Application; 
	public boolean Business;

	private boolean visited;

	public Component(){
		id = new Random().nextLong() % System.currentTimeMillis();
		id= Math.abs(id);
		nodes = new ArrayList<>();
		visited = false;
		Entity = false; 
		Utility = false;
		Application =  false;
		Business =  false;
	}


	public Component(String Name, Map<String,Metrics> Class_Metrics2){

		Metrics aMetric = new Metrics(); 

		aMetric= Class_Metrics2.get(Name);

		FanIn= aMetric.fanin; 
		Fanout= aMetric.fanout;
		TotalIf= aMetric.number_if;
		TotalSQL= aMetric.number_transactions;
		TotalTry= aMetric.number_try;
		TotalMcCabe = aMetric.McCabe;
		MeanMcCabe = aMetric.McCabe;
		//cohesion=??;
		//coupling=??;

	

		id = new Random().nextLong() % System.currentTimeMillis();
		id= Math.abs(id);
		nodes = new ArrayList<>();
		
		ClassNode aClassNode = new ClassNode(Name);
		
		nodes.add(aClassNode);
		/*
		 * 
		 * TODO mettre à jours les neighbors du classNode
		 * 
		 */
		
		visited = false;
		Entity = false; 
		Utility = false;
		Application =  false;
		Business =  false;
	}
	
	public void addClassNodeToComponent(String Name, Map<String,Metrics> Class_Metrics2) {
		
		Metrics aMetric = new Metrics(); 

		aMetric= Class_Metrics2.get(Name);

		FanIn= FanIn+ aMetric.fanin; 
		Fanout= Fanout+ aMetric.fanout;
		TotalIf= TotalIf+ aMetric.number_if;
		TotalSQL= TotalSQL+ aMetric.number_transactions;
		TotalTry= TotalTry+ aMetric.number_try;
		TotalMcCabe = TotalMcCabe+ aMetric.McCabe;
		MeanMcCabe = MeanMcCabe+ aMetric.McCabe;
		//cohesion=??;
		//coupling=??;

	

		
		
		
		ClassNode aClassNode = new ClassNode(Name);
		
		nodes.add(aClassNode);
		
	}


	public double Min_Max_normalize(double min, double max, double x) {


		return (x-min)/(max-min);
	}

	public void normalizeAllMetrics_min_max( double min_fanin, double max_fanin, double min_fanout, double max_fanout, double min_SQL, double max_sql, double min_IF, double max_IF, double min_Try, double max_Try) {


		FanIn=Min_Max_normalize(min_fanin, max_fanin , FanIn);
		Fanout=Min_Max_normalize(min_fanout, max_fanout , Fanout);
		TotalIf=Min_Max_normalize(min_IF, max_IF , TotalIf);
		TotalSQL=Min_Max_normalize(min_SQL, max_sql , TotalSQL);
		TotalTry=Min_Max_normalize(min_Try, max_Try , TotalTry);

	}  


	public Boolean Service_contain_class_name(String ClassName) {

		Boolean ClassInCurrentService = false; 

		for (ClassNode node: nodes) {

			if( node.getClassName().equals(ClassName)) {
				ClassInCurrentService = true;

			}
		}

		return ClassInCurrentService;
	}
	public void normalizeAllMetrics_mean(double m_fanin, double m_fanout, double m_TotalIf, double m_TotalSQL, double m_TotalTry) {


		FanIn= FanIn/m_fanin *100 ;
		Fanout= Fanout/m_fanout*100 ;
		TotalIf=TotalIf/m_TotalIf*100 ;
		TotalSQL=TotalSQL/ m_TotalSQL  *100 ;
		TotalTry= TotalTry/  m_TotalTry *100 ;


	}

	public void updateSomeMetrics(Map<String,Metrics> Class_Metrics){




		int number_of_ignored_classes=0;
		for (ClassNode Aclass: nodes){

			Metrics ClassMetric= new Metrics();
			ClassMetric= Class_Metrics.get(Aclass.getId());


			if (ClassMetric!=null){
				//FanIn= FanIn + ClassMetric.fanin; 
				//Fanout= Fanout + ClassMetric.fanout;
				TotalIf= TotalIf +  ClassMetric.number_if;
				TotalSQL= TotalSQL +  ClassMetric.number_transactions;
				TotalTry= TotalTry + ClassMetric.number_try;
				TotalMcCabe = TotalMcCabe + ClassMetric.McCabe;
				

			} else{
				number_of_ignored_classes++;

			}
		}
		
		MeanMcCabe = TotalMcCabe /( nodes.size());
	}


	public boolean isEntity() {
		return Entity;
	}

	public void setEntity(boolean entity) {
		Entity = entity;
	}

	public boolean isUtility() {
		return Utility;
	}

	public void setUtility(boolean utility) {
		Utility = utility;
	}

	public boolean isApplication() {
		return Application;
	}

	public void setApplication(boolean application) {
		Application = application;
	}

	public boolean isBusiness() {
		return Business;
	}

	public void setBusiness(boolean business) {
		Business = business;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNodes(List<ClassNode> nodes) {
		this.nodes = nodes;
	}

	public Long getId(){
		return this.id;
	}

	public void addNode(ClassNode node){
		nodes.add(node);
	}

	public void setVisited(boolean value){
		this.visited = value;
	}


	public boolean getVisited(){
		return this.visited;
	}

	public List<ClassNode> getNodes(){
		return this.nodes;
	}


	public int getSize() {
		return this.nodes.size();
	}


	public List<String> getFilePaths(){
		return this.nodes.stream().map(classNode -> classNode.getId()).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Service{" +
				"nodes=" + nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat) + '}';
	}
	
	
	
	public String toStringJson() {
		return "{"+ nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat) + '}';
	}
	
	public String toStringJson(String Type) {
		return "{"+Type+ nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat) + "},";
	}


	public String toString(String Service_type) {
		return Service_type+"{" +
				"nodes=" + nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat) + '}';
	}



	public String toString2() {
		return "Service{" +
				"nodes=" + nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat)  +
				" Fanin = " + FanIn + " , " +  
				" Fanout = " + Fanout + " , " +  
				" TotalIf = " + TotalIf + " , " +
				" TotalSQL = " + TotalSQL + " , " + 
				" TotalTry = " + TotalTry + " , " + 
				" Total McCabe = " + TotalMcCabe + " , " + 
				" Mean McCabe = " + MeanMcCabe + '}' ;



	}

	public String toString2(String Service_type) {
		return Service_type+" {" +
				"nodes=" + nodes.stream().map(n -> " , " + n.getId()).reduce("", String::concat)  +
				" Fanin = " + FanIn + " , " +  
				" Fanout = " + Fanout + " , " +  
				" TotalIf = " + TotalIf + " , " +
				" TotalSQL = " + TotalSQL + " , " + 
				" TotalTry = " + TotalTry + " , " + 
				" Total McCabe = " + TotalMcCabe + " , " + 
				" Mean McCabe = " + MeanMcCabe + '}' ;



	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Component)) return false;

		Component component = (Component) o;

		return nodes.equals(component.nodes);

	}

	@Override
	public int hashCode() {
		return nodes.hashCode();
	}
}
