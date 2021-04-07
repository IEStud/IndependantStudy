package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

class ServerConnectionHandler implements Runnable {
	int processID;
	String inputString;
	String dateString;
	String leaderMessage;
	Socket clientSocket = null;
	Boolean running = true;
	
	public ServerConnectionHandler(Socket soc) {
		clientSocket = soc;
	}

	@Override
	public void run() {

		try {
			LeaderElection leaderElection = new LeaderElection();
			ConnectionManager connMan = new ConnectionManager();
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
			
			System.out.println(connMan.leaderElection);
			
			if(connMan.leaderElection) {
				
				processID = leaderElection.Run();
				
    			leaderMessage = "LEADERELECTION:" + Integer.toString(processID);
    			dataOut.writeUTF(leaderMessage);
				dataOut.flush();
				
				System.out.println("Message sent");
			}
			
			while (running) {
				
            	if (dataIn.available() != 0) {
            		inputString = "";
            		inputString = dataIn.readUTF();
            		inputString = inputString.toUpperCase();
            		
            		if (inputString.startsWith("HEARTBEAT")) {  
            			
            			Date date = new Date();
            			dateString = date.toString();
            			dataOut.writeUTF("HEARTBEAT: " + dateString);
            			
            		} 
           	  		
            	}
				
			}
			
			
		} catch(Exception except) {
			
			System.out.println("Error in Server Connection Handler -->" + except);
						
		}
		
	}

}
