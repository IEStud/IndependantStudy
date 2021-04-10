package Server;

import java.io.*;
import java.net.Socket;

public class ServerReader implements Runnable {
	
	Socket swSocket = null;
	boolean transfer = false;
	Boolean reading = true;
	byte[] array;
	String filePath;
	ServerWriter cw = new ServerWriter(null);
	
	public ServerReader (Socket inputSoc){
		swSocket = inputSoc;
	}
	
	
	public void run() {
		
		try {
			
			DataInputStream dataIn = new DataInputStream(swSocket.getInputStream());			
			String dataFromServer;
			
			
			while (reading) {
				
				if (dataIn.available() > 0) {
					dataFromServer = dataIn.readUTF();
					
					//This prints out the heart beat check
					if(dataFromServer.startsWith("HEARTBEAT")) {
						
						System.out.println("SR: " + dataFromServer + ":" + swSocket.getPort());
						
					}
					if (dataFromServer.startsWith("REBOOT" )) {
						reading = false;
					}
				}			
			}				
		} catch (Exception except) {			
			System.out.println("Error in Server Reader" + except);			
		}
	}
}
