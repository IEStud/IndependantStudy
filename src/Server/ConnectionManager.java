package Server;
import java.net.*;
import java.util.ArrayList;

public class ConnectionManager {

	int portNumber = 50000;	
	boolean firstRun = true;

	public void StartUp() {
	
		try{   	
			
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