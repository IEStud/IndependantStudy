package Server;

import java.net.ServerSocket;

public class GetFreePort {

	
	//int portNumber = 50001;
	
	////////////////////////////////////////////////////
	
	public int GetPort (int localPort) {
		
		boolean keepGoing = true;
		boolean portSuccess = false;
		
        while (keepGoing) {
        	
        	portSuccess = isPortInUse(localPort);
        	
	        if (!portSuccess) {
	        	portSuccess = isPortInUse(localPort);
	        	localPort = localPort + 1;
	        }
	        else {
	        	
	        	keepGoing = false;
	        }
        }
        
        System.out.println("Port number is " + localPort);
		return localPort;
	}
	
	
////////////////////////////////////////////////////////
	
	private boolean isPortInUse(int portNumber) {
        
		boolean result;

        try {

            ServerSocket s = new ServerSocket(portNumber);
            s.close();
            result = true;

        }
        catch(Exception e) {
            result = false;
        }

        return(result);
	}
}
