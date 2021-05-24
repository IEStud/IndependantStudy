package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
	            			int add;
							int result;
	            			ConnectionManager.PeerList.clear();
							for (String entry: stringArray) {
								
								if (entry.contains("5000")) {
									
									add = Integer.parseInt(entry);								
									result = CheckNode(add);
								
									if (result == 1) {
										ConnectionManager.PeerList.add(add);
									}
								}
								if (entry.startsWith("9")) {
									int temp = Integer.parseInt(entry);
									temp = temp - 90;
									ConnectionManager.electionCount = temp;
									//System.out.println("The current election count is " + ConnectionManager.electionCount);
								}
							}							
							//System.out.println("HEARTBEAT : The Leader Flag is " + ConnectionManager.leaderFlag + " : And the Election boolean is " + ConnectionManager.electionComplete);
						}
						if (dataFromServer.startsWith("ELECTION")) {
							ConnectionManager.leaderFlag = true;
						}
					}			
				} 
				if (ConnectionManager.leaderFlag) {
					
					if (dataIn.available() > 0) {											
						
						dataFromServer = dataIn.readUTF();
						
						if(dataFromServer.startsWith("EXIT")) {
							System.out.println("Retiring from election in Reader");
							ConnectionManager.leaderFlag = false;
							reading = false;
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
