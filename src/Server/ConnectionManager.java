package Server;
import java.net.*;

public class ConnectionManager {

	int portNumber = 50000;	
	String serverIP = "localhost"; 
	int finalPort;
	boolean amLeader;
	boolean keepRunning = true;

	@SuppressWarnings("resource")
	public void StartUp() {
		
		try{   	
			
			GetFreePort port = new GetFreePort();			
			finalPort = port.GetPort(portNumber);
			
			amLeader = AmLeader();
			
			if(amLeader) {
				
	            //Setup the socket for communication 
				ServerSocket serverSoc = new ServerSocket(portNumber);
				
				System.out.println("Leader started");
				
				while (keepRunning){    
					
	                //accept incoming communication
	                Socket soc = serverSoc.accept();
	
	                //create a new thread for the connection and start it
	                ServerConnectionHandler sch = new ServerConnectionHandler(soc);
	                Thread schThread = new Thread(sch);
	                schThread.start();
	                Thread.sleep(250);
	
	            }
				
			} else {
				
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
			        
			        System.out.println("Server client started");
			        
			    } catch (Exception except){
			        //Exception thrown (except) when something went wrong, pushing message to the console
			        System.out.println("Error in main --> " + except.getMessage());
			    }
				
			}
            
        } catch (Exception except){
            //Exception thrown (except) when something goes wrong, pushing message to the console
            System.out.println("Error in Server Connection Manager--> " + except.getMessage());
        }
		
	}
	
	private Boolean AmLeader( ) {
		
		if (finalPort == 50000) {
			
			return true;
			
		}
		else {
		
			return false;
			
		}
	}

}