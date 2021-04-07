package Server;
import java.util.*;

public class LeaderElection {
	
	public int Run() {
		
		Random rand = new Random();
		int upperBound = 1000;
		
		int processID = rand.nextInt(upperBound);
		
		return processID;
		
	}
	
}
