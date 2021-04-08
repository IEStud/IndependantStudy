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
				
				Thread.sleep(10000);
				dataOut.writeUTF(heartbeat);
				dataOut.flush();
				
			}
			
			
		} catch (Exception except) {
			
			System.out.println("Beginning leader election");
			ServerReader serverReader = new ServerReader(swSocket);
			ServerConnectionHandler sch = new ServerConnectionHandler(swSocket);
			ConnectionManager connMan = new ConnectionManager();
			
			serverReader.reading = false;
			sch.running = false;
			connMan.leaderElection = true;
			connMan.StartUp();
			//System.out.println("Error in Server Writer" + except);
			
		}	
    }	
}
