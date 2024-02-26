package tmp.uqam.stage.metamodel.kdmparser;

public class Metrics {
	
	
	 
	public int fanin; 
	public int fanout;
	public int number_try; 
	public int number_if;
	public int number_transactions;
	
	public int McCabe;
	 
	public Metrics(int fanin, int fanout) {
			super();
			this.fanin = fanin;
			this.fanout = fanout;
		}

	public Metrics() {
		// TODO Auto-generated constructor stub
		this.fanin=0;
		this.fanout=0;
		this.number_try=0;
		this.number_transactions=0;
		this.number_if=0;
		this.McCabe=0;
		
	}
	  public void increment_NUM_SQL_Queries(int x){
		  
		  this.number_transactions=this.number_transactions+1;
		  
		  
	  }

	public Metrics(int fanin, int fanout, int number_if, int number_try, int number_transactions) {
		super();
		this.fanin = fanin;
		this.fanout = fanout;
		this.number_try = number_try;
		this.number_if = number_if;
		this.number_transactions = number_transactions;
		this.McCabe=0;
	}
	
}
