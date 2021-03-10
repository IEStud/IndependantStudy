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
		// TODO Auto-generated method stub
		
		
		
	}

}
