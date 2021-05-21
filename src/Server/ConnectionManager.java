package Server;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ConnectionManager {

	public static ArrayList<Integer> PeerList = new ArrayList<Integer>();
	public static ArrayList<Integer> TempList = new ArrayList<Integer>();
	static List<Integer> SecondList = new ArrayList<Integer>();
	static int portNumber = 50000;	
	static int processID;
	static int numberOfFlushes = 0;
	static int oldPortNumber;
	static String serverIP = "localhost"; 
	public static int finalPort;
	static boolean amLeader;
	static boolean leaderFlag = false;
	static boolean electionComplete = false;
	static boolean already = false;
	static boolean election = false;
	static boolean running = true;
	static boolean timerLoop = true;
	

	public static void StartUp() throws InterruptedException {  
		
		TimerThread timerThread = new TimerThread();
        Thread timerThread1 = new Thread(timerThread);
        timerThread1.start();
		
		ServerBoot();
	}
	
	public static void ServerBoot () throws InterruptedException {
		
		//GetFreePort tests ports from 50000 and up to find out which one is available
		GetFreePort port = new GetFreePort();
		PeerList.add(portNumber);
		finalPort = port.GetPort(portNumber);		
		oldPortNumber = finalPort;
		amLeader = AmLeader();
				
    	if (amLeader) {
    		//If the node is the leader, it will only start up its server side
    		ServerStart(); 
    		
    		if (electionComplete) {
    			
    			election = false;
    			
    			for (int newPort: SecondList) {
    			
    				ReaderWriter(newPort);
    				
    			}   			
    		}
    				
    	} else {    		
    		//If the node is not the leader, it will start its server and client threads		
    		ServerStart();
    		ReaderWriter(portNumber);
    	}
    	
    	if (amLeader) {
    		
	    	while (running) {
	    		
	    		if (!leaderFlag) {
	    			
	    			Thread.sleep(12000);
	    			UpdatePortList();
	    			
	    		} else if (leaderFlag) {
	    			
	    			running = false;
	    		}
	    	}
    	}
	}
	
	
	public static void ConnectToPeer () {
		SecondList.clear();
		SecondList = ConnectionManager.PeerList.stream().distinct().collect(Collectors.toList()); //Copies the peer list over to a secondary list		
		SecondList.remove(new Integer(ConnectionManager.finalPort)); //Removes the leader port number "50000"
		SecondList.remove(new Integer(portNumber)); //Removes it's own portnumber
		LeaderElection.Run(); //Generates it's own process ID
		election = true;
		
		for (int port: SecondList) {
			ReaderWriter(port);
		}	
		System.out.println("Connected to peers " + SecondList);
	}
	
	public static void Reboot () {
		ServerReader.reading = true;
		LeaderElection.firstRun = true;
		election = false;
		leaderFlag = false;
		ServerConnectionHandler.justFinished = false;
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
	        
	        System.out.println("Connected to server on port " + port + "; and I am " + finalPort);
	        
		} catch (Exception except) {

			System.out.println("Error in ReaderWriter -> " + except);
			
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
	
	public static void UpdatePortList () {
		PeerList.clear();
		PeerList = (ArrayList<Integer>) TempList.stream().distinct().collect(Collectors.toList());	
		System.out.println("List updated" + PeerList);
		
		TempList.clear();
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