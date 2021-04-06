package Server;

import java.io.*;
import java.net.Socket;

public class ServerReader implements Runnable {

	Socket swSocket = null;
	boolean restartClient = false;
	boolean transfer = false;
	byte[] array;
	String filePath;
	ServerWriter cw = new ServerWriter(null);
	
	public ServerReader (Socket inputSoc){
		swSocket = inputSoc;
	}
	
	
	public void run() {
		
		try {
			
			DataInputStream dataIn = new DataInputStream(swSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(swSocket.getOutputStream());
			Boolean reading = true;
			String dataFromServer;
			
			while (reading) {
				
				if (dataIn.available() > 0) {
					
					dataFromServer = dataIn.readUTF();
					
					System.out.println("Heartbeat check: " + dataFromServer);
					
//					if (dataFromServer.startsWith("HEARTBEAT")) {
//						System.out.println("Heartbeat check: " + dataFromServer);
//					}
				}
			
			}
		} catch (Exception except) {
			
			System.out.println("Error in Server Reader" + except);
			
		}
		
	}
}
