package Server;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

public class LeaderElection {
	static boolean firstRun = true;
	
	public static void Run() {
		
		//This if loops stops more than one Process ID being generated
		if (firstRun) {
			
			int uptime;
			RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
			uptime = (int) rb.getUptime();
						
//			Random rand = new Random();
//			int upperBound = 1000;			
//			uptime = rand.nextInt(upperBound);
			
			ConnectionManager.processID = uptime;
			firstRun = false;
		}
	}	
	
	public static int Check(int num) {
		
		System.out.println("Checking number " + num);
		
		if (ConnectionManager.processID > num) {
			ConnectionManager.numberOfFlushes++;
			return 1;
			
		} else {

			return 0;
		}
	}
}
