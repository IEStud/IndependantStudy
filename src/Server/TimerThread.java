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
		int band0Upper = 60000;
		int band1Upper = 120000;
		int band2Upper = 240000;
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
				runningTime = rand.nextInt(band0Upper) + band0Upper;
				break;
			case 2:
				runningTime = rand.nextInt(band1Upper) + band1Upper;
				break;
			case 3:
				runningTime = rand.nextInt(band2Upper) + band1Upper;
				break;
		}
		
		System.out.println("Running time is " + runningTime + " and I am band " + bandDecider + ":" + decider);
		
		while(true) {
			
			uptime = (int) rb.getUptime();
			
			if (uptime >= runningTime) {
				
				if (ConnectionManager.leaderFlag || ServerConnectionHandler.justFinished || ConnectionManager.electionComplete) {
					
					try {
						System.out.println("Waiting to kill thread...");
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						System.out.println("Error in TimerThread ->" + e.getMessage());
					}
					
				} else {
				
					System.out.println("Node crashing...");
					System.exit(0);
				}
			}
		}
	}
}
