
public class process {
    int id;
    LinkedQueue<Integer>  CPU_burst;
    LinkedQueue<Integer>  memory_req;
    LinkedQueue<Integer>  io_burst;
	int AT;
	String state;
	int preempted;
	int finishedmem;
	int finishedcpu;
	int finishedio;
	int firstreq;
	int currentmem;
	int addmem;
	int timeTerOrKilled;
	int loadReadyTime;
	int nbOfTimeInCpu;
	int nbOfTimeIO;
	int nbOfTimeWaitMem;
	
	
	public process(){
		
	      CPU_burst  =new	LinkedQueue<Integer>();
	     memory_req  =new	LinkedQueue<Integer>();;
	     io_burst    =new    LinkedQueue<Integer>();;
	}
	
}
