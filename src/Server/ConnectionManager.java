package Server;
import java.net.*;

public class ConnectionManager {

	int portNumber = 50000;	
	boolean firstRun = true;

	@SuppressWarnings("resource")
	public void StartUp() {
		
		try{   	
			
			GetFreePort port = new GetFreePort();			
			portNumber = port.GetPort(portNumber);
			
            //Setup the socket for communication 
			ServerSocket serverSoc = new ServerSocket(portNumber);

			while (true){    
                //accept incoming communication
                Socket soc = serverSoc.accept();
                
                

                //create a new thread for the connection and start it
                ServerConnectionHandler sch = new ServerConnectionHandler(soc);
                Thread schThread = new Thread(sch);
                schThread.start();
                Thread.sleep(250);
                

            }
            
        } catch (Exception except){
            //Exception thrown (except) when something goes wrong, pushing message to the console
            System.out.println("Error in Server Connection Manager--> " + except.getMessage());
        }
		
	}

}