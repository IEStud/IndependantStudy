package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

class ServerConnectionHandler implements Runnable {
	List<Integer> SecondList = new ArrayList<Integer>();
	int processID;
	int numberSent = 0;
	long startTime;
	long currentTime;
	long difference;
	String inputString;
	String dateString;
	String leaderMessage;
	Socket clientSocket = null;
	static boolean running = true;
	boolean reboot = false;
	static boolean justFinished = false;
	boolean flushSent = false;
	boolean started = false;
	boolean already = false;
	
	public ServerConnectionHandler(Socket soc) {
		clientSocket = soc;
	}

	@Override
	public void run() {

		try {
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			
			while (running) {
				
				if(ConnectionManager.leaderFlag) {
		
					if (justFinished) {
						//System.out.println("In just finished loop");
						ConnectionManager.leaderFlag = false;
						
					} else {
						
						if (flushSent) {
							currentTime = System.currentTimeMillis();
							difference = currentTime - startTime;
							
							if (difference > 10000) {
								currentTime = 0;
								startTime = 0;
								difference = 0;
								ConnectionManager.leaderFlag = false;
    							ConnectionManager.electionComplete = true;
    							ConnectionManager.StartUp();
							}
						}
						
						if (dataIn.available() != 0) { 
							inputString = "";
		            		inputString = dataIn.readUTF();
		            		inputString = inputString.toUpperCase();
		            		
		            		System.out.println("LEADER ELECTION: " + inputString);
		            		
		            		if(inputString.startsWith("ELECTION")) {
		            			String[] stringArray = inputString.split(":");
		            			
		            			for (String port: stringArray) {
		            				
		            				if (port.contains("5000")) {
		            					int add = Integer.parseInt(stringArray[2]);         					
		            					int result = 0; 
		            					result = LeaderElection.Check(add);
		            					
		            					if (result == 1) {	            						
		            						dataOut.writeUTF("EXIT");
		            						dataOut.flush();
		            						System.out.println("Exit sent");
		            						
		            						startTime = System.currentTimeMillis();
		            						System.out.println("The current time is " + startTime);
		            						flushSent = true;
		            						
		            						if (ConnectionManager.numberOfFlushes == ConnectionManager.SecondList.size()) {
		            							ConnectionManager.numberOfFlushes = 0;
		            							ConnectionManager.leaderFlag = false;
		            							ConnectionManager.electionComplete = true;
		            							ConnectionManager.ServerBoot();
		            						}
		            					} else {
		            						
		            						System.out.println("Retiring from election in Server with process ID " + ConnectionManager.processID);
		            						ConnectionManager.numberOfFlushes = 0;
		        							ConnectionManager.leaderFlag = false;
		        							ServerReader.reading = false;
		        							justFinished = true;
		            					}
		            				}
		            			}	            			 
		            		}	
		            		if (inputString.startsWith("COMPLETE")) {
		            			ConnectionManager.numberOfFlushes = 0;
    							ConnectionManager.leaderFlag = false;
    							ServerReader.reading = false;
    							justFinished = true;
    							ConnectionManager.Reboot();	
		            		}
	            		}	
					}
				} else {
					
	            	if (dataIn.available() != 0) {
	            		inputString = "";
	            		inputString = dataIn.readUTF();
	            		inputString = inputString.toUpperCase();
	            		
	            		System.out.println("Server: " + inputString);
	            		
	            		//This first statement parses the port number presented after the CONNECT and stores it
	            		if (inputString.startsWith("CONNECT")) {            			
	            			String [] tempinputString = inputString.split(":", inputString.length());
	            			String tempPortString = tempinputString[1];
	            			int tempPort = Integer.parseInt(tempPortString);          			
	            			ConnectionManager.PeerList.add(tempPort);          			            			
	            			tempPortString = GetCurrentPortList();
	            			
	            			dataOut.writeUTF(tempPortString);
	            			dataOut.flush();           			
	            		}            		
	            		//This 'If' statement responds to the Heartbeat request
	            		if (inputString.startsWith("HEARTBEAT")) {  
	            			//This first section of code splits of the clients port number and adds it to the list of clients
	            			String [] tempinputString = inputString.split(":", inputString.length());
	            			String tempPortString = tempinputString[1];
	            			int tempPort = Integer.parseInt(tempPortString);  
	            			ConnectionManager.TempList.add(tempPort);
	            			
	            			//This gets the list of clients the leader is aware of and send then back to the node
	            			String temp = GetCurrentPortList();
	
	            			dataOut.writeUTF(temp + dateString);
	            			dataOut.flush();	            			
	            		}  
	            		if (inputString.startsWith("ELECTION")) {
	            			dataOut.writeUTF("ELECTION");
	            			dataOut.flush();
	            			LeaderElection.Run();
	            			ConnectionManager.leaderFlag = true;
	            		}
	            		if (inputString.startsWith("COMPLETE")) {
	            			System.out.println("Beginning reboot..." + justFinished);
	            			ConnectionManager.Reboot();	            			
	            		}
	            	}
				}	
			}
		} catch(Exception except) {
			
			System.out.println("Error in Server Connection Handler -->" + except);
						
		}		
	}
	
	private String GetCurrentPortList () {
		SecondList.clear();
		SecondList = ConnectionManager.PeerList.stream().distinct().collect(Collectors.toList());		
		SecondList.remove(new Integer(ConnectionManager.finalPort));
		String portString = "HEARTBEAT";
		
		for(int port: SecondList) {
			portString = portString + ":" + Integer.toString(port);
		}
		
		portString = portString + ":EOF";
		
		return portString;
		
	}
}
