package Server;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectionManager {

	public static ArrayList<Integer> PeerList = new ArrayList<Integer>();
	static List<Integer> SecondList = new ArrayList<Integer>();
	static int portNumber = 50000;	
	static String serverIP = "localhost"; 
	public static int finalPort;
	static boolean amLeader;
	boolean leaderElection = false;
	boolean keepRunning = true;
	static boolean swFirstRun = true;

	public void StartUp() throws InterruptedException {  	
		
		//GetFreePort tests ports from 50000 and up to find out which one is available
		GetFreePort port = new GetFreePort();
		PeerList.add(portNumber);
		finalPort = port.GetPort(portNumber);	
		int count = 0;
		int numberOfPeers =  finalPort - portNumber;
		int finalCount = 0;
		boolean finished = false;
			
		while (!finished) {						
			count++;				
			if (count >= numberOfPeers) {					
				finished = true;				
			} else {				
				//This line of code gets the port number 
				finalCount = portNumber + count;				
				PeerList.add(finalCount);
			}
		}		
		
		amLeader = AmLeader();
    	if (amLeader) {
    		//If the node is the leader, it will only start up its server side
    		ServerStart();   		
    	} else {    		
    		//If the node is not the leader, it will start its server and client threads
    		ServerStart();
    		ConnectToPeer();
    	}
	}
	
	public static void Reboot() throws InterruptedException {
		ConnectToPeer();
	}
	
	private static void ConnectToPeer () throws InterruptedException {
	
		//This section of code is working, the second list carries the correct number of peers
		
		SecondList = PeerList.stream().distinct().collect(Collectors.toList());		
		SecondList.remove(new Integer(finalPort));
		
		for (int portyPort: SecondList) {
			Thread.sleep(500);
			ReaderWriter(portyPort);
		}
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