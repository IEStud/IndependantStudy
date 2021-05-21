package Server;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

public class TimerThread implements Runnable {

	@Override
	public void run() {
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		Random rand = new Random();
		int decider = 3;
		int band1Upper = 5000;
		int band2Upper = 10000;
		int runningTime = 0;
		int uptime;
		int bandDecider;
		boolean running = true;
		
		do {
			bandDecider = rand.nextInt((decider)  + 1 );
			
			if (bandDecider != 0) {
				running = false;
			}
		} while(running);
		
		
		switch (bandDecider) {		
			case 1:
				runningTime = rand.nextInt(band1Upper);
				break;
			case 2:
				runningTime = rand.nextInt(band1Upper) + band1Upper;
				break;
			case 3:
				runningTime = rand.nextInt(band1Upper) + band2Upper;
				break;
		}
		
		System.out.println("Running time is " + runningTime + " and I am band " + bandDecider + ":" + decider);
		
		while(true) {
			
			uptime = (int) rb.getUptime();
			
			if (uptime >= runningTime) {
				
				System.out.println("Node crashing...");
				System.exit(0);
				
			}
		}
	}
}
