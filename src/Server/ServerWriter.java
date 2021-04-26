package Server;

import java.io.*;
import java.net.Socket;

public class ServerWriter implements Runnable {

    Socket swSocket = null; 
    boolean running = true;
    static boolean swFirstRun = true;
    int processID;
    
    
    public ServerWriter (Socket outputSoc){
        swSocket = outputSoc;
    }
    
    
    public void run() {
    	
		try {
			
			DataOutputStream dataOut = new DataOutputStream(swSocket.getOutputStream());
			
			String heartbeat = "HEARTBEAT:";
			String connect = "CONNECT:";
			String election = "ELECTION";
			String complete = "COMPLETE";
			
//			if (ConnectionManager.election) {
//				ConnectionManager.leaderFlag = true;
//				System.out.println("The leader flag is " + ConnectionManager.leaderFlag);
//			}
			
			if (ConnectionManager.electionComplete) {
				System.out.println("Waiting for complete message send");
				//Clear the peer list below to get a fresh list of online nodes
				ConnectionManager.PeerList.clear();
				Thread.sleep(10000);
				dataOut.writeUTF(complete);
				dataOut.flush();
				ConnectionManager.electionComplete = false;
				System.out.println("Complete message sent");
				
			}  
			if (ConnectionManager.leaderFlag) {
				
				dataOut.writeUTF(election);
				Thread.sleep(5000);
				dataOut.writeUTF(election + ":" + ConnectionManager.finalPort + ":" + ConnectionManager.processID);
				dataOut.flush();

			} else {
			
				while (running) {
					//Checks to see if there is an election in progress
					if (!ConnectionManager.leaderFlag) { 
						if (swFirstRun) {
							String temp = connect + ConnectionManager.finalPort;
							dataOut.writeUTF(temp);
							dataOut.flush();
							swFirstRun = false;
						} else {
							//Sends a heart beat check to the current leader every 10 seconds		
							
							if (swSocket.getPort() == 50000) {
								Thread.sleep(10000);
								dataOut.writeUTF(heartbeat + ConnectionManager.finalPort);
								dataOut.flush();
								ConnectionManager.electionComplete = false;
								
							}
						}
					} else if (ConnectionManager.leaderFlag){						
						running = false;
						ConnectionManager.ConnectToPeer();
					}
				}
			}
		} catch (Exception except) {
			
			if (swSocket.getPort() == 50000) {
				System.out.println("Starting leader election...");
				ConnectionManager.leaderFlag = true;
				System.out.println("Leader flag is " + ConnectionManager.leaderFlag);
				ConnectionManager.ConnectToPeer();
				
			} else {
				
				System.out.println("Error in server writer -> " + except);
				
			}
		}
	}	 
}
