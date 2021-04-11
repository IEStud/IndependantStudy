package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class ServerConnectionHandler implements Runnable {
	List<Integer> SecondList = new ArrayList<Integer>();
	int processID;
	String inputString;
	String dateString;
	String leaderMessage;
	Socket clientSocket = null;
	boolean running = true;
	boolean reboot = false;
	
	public ServerConnectionHandler(Socket soc) {
		clientSocket = soc;
	}

	@Override
	public void run() {

		try {
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			
			while (running) {
				
            	if (dataIn.available() != 0) {
            		inputString = "";
            		inputString = dataIn.readUTF();
            		inputString = inputString.toUpperCase();
            		
            		if (inputString.startsWith("CONNECT")) {
            			
            			String [] tempinputString = inputString.split(":", inputString.length());
            			String tempPortString = tempinputString[1];
            			int tempPort = Integer.parseInt(tempPortString);          			
            			ConnectionManager.PeerList.add(tempPort);
            			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////            			
            			
            			tempPortString = GetCurrentPortList();
            			
            			dataOut.writeUTF(tempPortString);
            			dataOut.flush();
            			//ConnectionManager.Reboot();
            			
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            			
            		}
            		
            		//This 'If' statement responds to the Heartbeat request
            		if (inputString.startsWith("HEARTBEAT")) {  

            			//Gets the current date and time
//            			Date date = new Date();
//            			dateString = date.toString();
            			String temp = GetCurrentPortList();
            			
            			//Passes date and time back to requesting node as proof it is still online
            			dataOut.writeUTF(temp + dateString);
            			dataOut.flush();
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
		
		System.out.println("SCH: Sending this string to Server Reader " + portString);
		
		return portString;
		
	}
}
