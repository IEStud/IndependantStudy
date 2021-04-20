package Server;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectionManager {

	public static ArrayList<Integer> PeerList = new ArrayList<Integer>();
	public static ArrayList<Integer> ElectionList = new ArrayList<Integer>();
	static List<Integer> SecondList = new ArrayList<Integer>();
	static int portNumber = 50000;	
	static int processID;
	static int numberOfFlushes = 0;
	static String serverIP = "localhost"; 
	public static int finalPort;
	static boolean amLeader;
	static boolean leaderFlag = false;
	static boolean electionComplete = false;

	public static void StartUp() throws InterruptedException {  	
		
		//GetFreePort tests ports from 50000 and up to find out which one is available
		GetFreePort port = new GetFreePort();
		PeerList.add(portNumber);
		finalPort = port.GetPort(portNumber);		
		
		amLeader = AmLeader();
		
    	if (amLeader) {
    		//If the node is the leader, it will only start up its server side
    		ServerStart(); 
    		
    		if (electionComplete) {
    			
    			for (int newPort: SecondList) {
    			
    				ReaderWriter(newPort);
    				
    			}
    		}
    				
    	} else {    		
    		//If the node is not the leader, it will start its server and client threads		
    		ServerStart();
    		ReaderWriter(portNumber);
    	}
	}
	
	
	public static void ConnectToPeer () {
		
		SecondList = ConnectionManager.PeerList.stream().distinct().collect(Collectors.toList());		
		SecondList.remove(new Integer(ConnectionManager.finalPort));
		SecondList.remove(new Integer(portNumber));
		LeaderElection.Run();

		for (int port: SecondList) {
			
			ReaderWriter(port);
			
		}	
	}
	
	public static void Reboot () {
		ServerReader.reading = true;
		ReaderWriter(portNumber);
		
	}
	
	private static void ReaderWriter (int port) {
		try {
			
	        //Create a new socket for communication
	        Socket soc = new Socket(serverIP,port);
	        
	        //create new instance of the server writer thread, initialise it and start it running
	        ServerReader serverRead = new ServerReader(soc);
	        Thread serverReadThread = new Thread(serverRead);
	        serverReadThread.start();
	        
	        // create new instance of the server writer thread, initialise it and start it running
	        ServerWriter serverWrite = new ServerWriter(soc);
	        Thread serverWriteThread = new Thread(serverWrite);
	        serverWriteThread.start();
	        
	        //System.out.println("Connected to server on port " + port + "; and I am " + finalPort);
	        
		} catch (Exception except) {
			
			System.out.println("Error in ReaderWriter --> " + except);
			
		}
	}
	
	
	private static void ServerStart () {
		
		try {
			//Create server socket using the free port found in GetFreePort class
			ServerSocket serverSoc = new ServerSocket(finalPort);
			    
			/*This creates a separate thread for the server side of the peer, 
			 * and allows it to run independently of the ConnectionManager Class**/
	        ServerThread serverThread = new ServerThread(serverSoc);
	        Thread servThread = new Thread(serverThread);
	        servThread.start();
			
		} catch (Exception except) {
			
			System.out.println("Exception in ServerStart --> " + except);
			
		}		
	}

	//This class runs a simple check to see if this node is the leader in the P2P network
	private static Boolean AmLeader( ) {		
		
		if (finalPort == 50000) {
			
			return true;
			
		} else {
		
			return false;
			
		}
	}
}