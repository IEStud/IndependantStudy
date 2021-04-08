package Server;
import java.net.*;
import java.util.*;

public class ConnectionManager {

	public static ArrayList<String> PeerList = new ArrayList<String>();
	int portNumber = 50000;	
	String serverIP = "localhost"; 
	int finalPort;
	boolean amLeader;
	boolean leaderElection = false;
	boolean keepRunning = true;

	public void StartUp() {  	
			
		GetFreePort port = new GetFreePort();			
		finalPort = port.GetPort(portNumber);
		
		amLeader = AmLeader();

    	if (amLeader) {
    		
    		ServerStart();
    		
    	} else {
    		
    		ServerStart();
    		ReaderWriter();
    		
    	}
	}
	
	
	private void ReaderWriter () {
		try {
			
	        //Create a new socket for communication
	        Socket soc = new Socket(serverIP,portNumber);
	        
	        //create new instance of the server writer thread, initialise it and start it running
	        ServerReader serverRead = new ServerReader(soc);
	        Thread serverReadThread = new Thread(serverRead);
	        serverReadThread.start();
	        
	        // create new instance of the server writer thread, initialise it and start it running
	        ServerWriter serverWrite = new ServerWriter(soc);
	        Thread serverWriteThread = new Thread(serverWrite);
	        serverWriteThread.start();	      
	        
		} catch (Exception except) {
			
			System.out.println("Error in ReaderWriter --> " + except);
			
		}
	}
	
	
	private void ServerStart () {
		
		try {
			
			ServerSocket serverSoc = new ServerSocket(finalPort);
			    
	        ServerThread serverThread = new ServerThread(serverSoc);
	        Thread servThread = new Thread(serverThread);
	        servThread.start();
			
		} catch (Exception except) {
			
			System.out.println("Exception in ServerStart --> " + except);
			
		}		
	}
	
	
	private Boolean AmLeader( ) {
		
		if (finalPort == 50000) {
			
			return true;
			
		} else {
		
			return false;
			
		}
	}
}