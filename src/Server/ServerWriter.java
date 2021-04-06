package Server;

import java.io.*;
import java.net.Socket;

public class ServerWriter implements Runnable {

    Socket swSocket = null;
    public boolean activeSession = true;
    
    public ServerWriter (Socket outputSoc){
        swSocket = outputSoc;
    }
    
    
    public void run() {
    	
		try {
			
			DataOutputStream dataOut = new DataOutputStream(swSocket.getOutputStream());
			Boolean running = true;
			String heartbeat = "HEARTBEAT";
			
			System.out.println("Starting loop");
			
			while (running) {
				
				Thread.sleep(5000);
				dataOut.writeUTF(heartbeat);
				dataOut.flush();
				
			}
			
			
		} catch (Exception except) {
			
			System.out.println("Error in Server Writer" + except);
			
		}
    	
    }
    
    private void PerformHeartBeat() {
    	
    	
    }
	
}
