package Server;

import java.io.*;
import java.net.Socket;

public class ServerWriter implements Runnable {

    Socket swSocket = null; 
    boolean running = true;
    boolean swFirstRun = true;
    
    public ServerWriter (Socket outputSoc){
        swSocket = outputSoc;
    }
    
    
    public void run() {
    	
		try {
			
			DataOutputStream dataOut = new DataOutputStream(swSocket.getOutputStream());
			
			String heartbeat = "HEARTBEAT";
			String connect = "CONNECT:";

			while (running) {
				
				if (swFirstRun) {
					String temp = connect + ConnectionManager.finalPort;
					dataOut.writeUTF(temp);
					dataOut.flush();
					swFirstRun = false;
					//running = false;
				} else {
					//Sends a heart beat check to the current leader every 10 seconds	
					
					if (swSocket.getPort() == 50000) {
						Thread.sleep(10000);
						dataOut.writeUTF(heartbeat);
						dataOut.flush();
						System.out.println("SW: Heartbeat sent");
					}
				}
			}
			
			
		} catch (Exception except) {
		
			System.out.println("Error in Server Writer" + except);
			
		}
	}	 
}
