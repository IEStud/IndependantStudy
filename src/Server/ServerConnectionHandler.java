package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class ServerConnectionHandler implements Runnable {

	Socket clientSocket = null;
	
	public ServerConnectionHandler(Socket soc) {
		clientSocket = soc;
	}

	@Override
	public void run() {
		
		try {
			
			GetFreePort port = new GetFreePort();
			ConnectionManager connMan = new ConnectionManager();
			DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
			
			
			
			
		} catch(Exception except) {
			
			System.out.println("Error in Server Connection Handler -->" + except);
						
		}
		
	}

}
