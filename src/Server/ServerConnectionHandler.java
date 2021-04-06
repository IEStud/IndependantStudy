package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class ServerConnectionHandler implements Runnable {

	Socket clientSocket = null;
	Boolean running = true;
	
	public ServerConnectionHandler(Socket soc) {
		clientSocket = soc;
	}

	@Override
	public void run() {
		
		try {
			
			ConnectionManager connMan = new ConnectionManager();
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			
			while (running) {
				
				System.out.println("Here I am!");
				
			}
			
			
		} catch(Exception except) {
			
			System.out.println("Error in Server Connection Handler -->" + except);
						
		}
		
	}

}
