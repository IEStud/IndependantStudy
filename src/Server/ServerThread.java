package Server;

import java.net.*;

public class ServerThread implements Runnable {
	
	ServerSocket servSoc;
	boolean keepRunning = true;
	
	public ServerThread(ServerSocket serverSoc) {
		
		servSoc = serverSoc;
	}

	@Override
	public void run() {
		try {
			
			while (keepRunning){  
				
		        //accept incoming communication
		        Socket soc = servSoc.accept();
	
		        //create a new thread for the connection and start it
		        ServerConnectionHandler sch = new ServerConnectionHandler(soc);
		        Thread schThread = new Thread(sch);
		        schThread.start();
		        Thread.sleep(250);       
		    }
			
		} catch (Exception except) {
			
			System.out.println("Error in ServerThread --> " + except);
			
		}		
	}	
}
