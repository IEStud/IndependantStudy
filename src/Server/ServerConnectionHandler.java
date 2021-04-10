package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Date;

class ServerConnectionHandler implements Runnable {

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
            			
            			dataOut.writeUTF("REBOOT");
            			dataOut.flush();
            			ConnectionManager.Reboot();
            		}
            		
            		//This 'If' statement responds to the Heartbeat request
            		if (inputString.startsWith("HEARTBEAT")) {  

            			//Gets the current date and time
            			Date date = new Date();
            			dateString = date.toString();
            			
            			//Passes date and time back to requesting node as proof it is still online
            			dataOut.writeUTF("HEARTBEAT: " + dateString);
            			dataOut.flush();
            		}            	  		
            	}				
			}	
			
		} catch(Exception except) {
			
			System.out.println("Error in Server Connection Handler -->" + except);
						
		}		
	}
}
