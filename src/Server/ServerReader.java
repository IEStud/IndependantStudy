package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ServerReader implements Runnable {
	
	Socket swSocket = null;
	static boolean reading = true;
	
	public ServerReader (Socket inputSoc){
		swSocket = inputSoc;
	}
	
	
	public void run() {
		
		try {
			
			DataInputStream dataIn = new DataInputStream(swSocket.getInputStream());			
			String dataFromServer;
			
			if (ConnectionManager.amLeader) {
				
				reading = false;
				
			}
						
			while (reading) {
				//Checks to see if there is a leader election in progress
				if (!ConnectionManager.leaderFlag) {
					
					if (dataIn.available() > 0) {
						
						dataFromServer = dataIn.readUTF();
						
						//This prints out the heart beat check
						if(dataFromServer.startsWith("HEARTBEAT")) {
							String [] stringArray = dataFromServer.split(":");
	            			Date date = new Date();
	            			String dateString = date.toString();
							int add;
							int result;
	            			
							for (String entry: stringArray) {
								
								if (entry.contains("5000")) {
									
									add = Integer.parseInt(entry);								
									result = CheckNode(add);
								
									if (result == 1) {
										ConnectionManager.PeerList.add(add);
									}
								}
							}							
							System.out.println("HEARTBEAT received on " + dateString);
						}
						if (dataFromServer.startsWith("REBOOT" )) {
							reading = false;
						}
					}			
				} 
				if (ConnectionManager.leaderFlag) {
									
					if (dataIn.available() > 0) {											
						
						dataFromServer = dataIn.readUTF();
						
						if(dataFromServer.startsWith("EXIT")) {
							ServerWriter.electionRunning = false;
							ConnectionManager.leaderFlag = false;
							reading = false;
							System.out.println("Retiring from election");
						}	
					}
				}
			}
		} catch (Exception except) {			
			System.out.println("Error in Server Reader" + except);			
		}
	}
	
	private int CheckNode (int add) {
		
		List<Integer> LocalList = new ArrayList<Integer>();		
		LocalList = ConnectionManager.PeerList.stream().distinct().collect(Collectors.toList());
		int returnValue = 1;
		
		for (int element: LocalList) {
			
			if (element == add) {
				returnValue = 0;
			}			
		}
		
		return returnValue;
	}
}
