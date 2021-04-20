package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
	boolean running = true;
	boolean reboot = false;
	boolean justFinished = false;
	boolean flushSent = false;
	
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
					
						ConnectionManager.leaderFlag = false;
						
					} else {
						
						if (flushSent) {
							currentTime = System.currentTimeMillis();
							difference = currentTime - startTime;
							
							if (difference > 10000) {
								System.out.println("the difference is real");
							}
						}
						
						if (dataIn.available() != 0) { 
							inputString = "";
		            		inputString = dataIn.readUTF();
		            		inputString = inputString.toUpperCase();
		            		
		            		if(inputString.startsWith("ELECTION")) {
		            			String[] stringArray = inputString.split(":");
		            			
		            			for (String port: stringArray) {
		            				
		            				if (port.contains("5000")) {
		            					int add = Integer.parseInt(stringArray[2]);         					
		            					int result = 0; 
		            					result = LeaderElection.Check(add);
		            					
		            					//System.out.println("Process ID received is " + add + " my process ID is " + ConnectionManager.processID);
		            					
		            					if (result == 1) {	            						
		            						dataOut.writeUTF("EXIT");
		            						dataOut.flush();
		            						System.out.println("Exit sent");
		            						
		            						startTime = System.currentTimeMillis();
		            						System.out.println("The current time is " + startTime);
		            						flushSent = true;
		            						if (ConnectionManager.numberOfFlushes == ConnectionManager.SecondList.size()) {
		            							ConnectionManager.leaderFlag = false;
		            							ConnectionManager.electionComplete = true;
		            							ConnectionManager.StartUp();
		            						}
		            					} else {
		            						
		            						System.out.println("Retiring from election in Server with process ID " + ConnectionManager.processID);
		        							ConnectionManager.leaderFlag = false;
		        							ServerReader.reading = false;
		        							justFinished = true;
		            					}
		            				}
		            			}	            			 
		            		}			            		
	            		}	
					}
				} else {
				 
	            	if (dataIn.available() != 0) {
	            		inputString = "";
	            		inputString = dataIn.readUTF();
	            		inputString = inputString.toUpperCase();
	            		
	            		System.out.println(inputString);
	            		
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
	
	            			String temp = GetCurrentPortList();
	
	            			dataOut.writeUTF(temp + dateString);
	            			dataOut.flush();
	            		}  
	            		if(inputString.startsWith("REBOOT")) {
	            			dataOut.writeUTF("REBOOT");
	            			dataOut.flush();
	            		}
	            		if (inputString.startsWith("ELECTION")) {
	            			LeaderElection.Run();
	            			ConnectionManager.leaderFlag = true;
	            		}
	            		if (inputString.startsWith("COMPLETE")) {
	            			System.out.println("Beginning reboot...");
	            			justFinished = false;
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
