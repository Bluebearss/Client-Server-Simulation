import java.io.IOException;
import java.net.InetAddress; //Import InetAddress Class that represents an IP Address
import java.net.ServerSocket; //Importing the ServerSocket Class to use the ServerSocket object
import java.net.Socket; //Importing the Socket Class so client's socket can be used
import java.util.HashMap; //Importing HashMap to implement the "database"

public class Server {
	public ServerSocket serverSocket; //Server has a ServerSocket object that represents server's socket
	private boolean serverStop; //Server has a boolean variable that tells if server has stopped
	private HashMap<String, String> database; //Server has a database that maps an email to its user's full name
	
	public Server(int port, HashMap<String, String> database) throws IOException //IOException when server can't be made
	{
		serverSocket = new ServerSocket(port); //Creates a new ServerSocket with the given well known port #
		serverStop = false; //Set serverStop to false
		this.database = database; //Link given database to the server
	}
	
	public void simulate() throws IOException //simulate the server to accept clients
	{
		System.out.println("You are on Port " + serverSocket.getLocalPort());
		System.out.println("Your IP Address is " + InetAddress.getLocalHost().getHostAddress());
		//Concatenation of Welcome Message; it says port # and LocalHost IP address
		
		while(!serverStop) //Loops while server is not stopped; usually an infinite loop
		{
			Socket clientSocket = null; //Takes in the clientSocket
			
			try 
			{
				clientSocket = serverSocket.accept(); //Accepts the connection with the clientSocket
				System.out.println();
				System.out.println("Client has been accepted.");
			} 
			catch(IOException e)
			{
				System.out.println("Error connecting to the Client"); //In case the client can't be connected to
				e.printStackTrace();
			}
			
			ThreadNewRunnable clientRunnable = new ThreadNewRunnable(clientSocket, database); //Creates new Runnable for the client
			Thread clientThread = new Thread(clientRunnable); //Create new thread for client passing in their socket and the database
			clientThread.start(); //Starts the connection with the client on the thread		
			
		}
		
		//Supposedly it never reaches this point. The server is not suppose to stop.
		System.out.println("Server has stopped."); //If breaks out of while loop, server has stopped
		serverSocket.close(); //Close the server's socket
	}
	
	public static void main(String[] args) throws IOException {
		
		int portNum = 5000; //An arbitrary port number for the server, also used by the client
		HashMap<String, String> testDatabase = new HashMap<String, String>(); //Create a "database" for the server
		
		//This the database. If you want to add more email. simply using the .put() method to put one in the database
		testDatabase.put("csiscool@gmail.com", "Robert Leung");
		testDatabase.put("cs310isfun@yahoo.com", "Bobby Li");
		testDatabase.put("brian.zhu@stonybrook.edu", "Brian Zhu");
		testDatabase.put("brianzhu0512@gmail.com", "Brian Zhu");
		testDatabase.put("iliketospendmoney@aol.com", "Steven McDonald");
		testDatabase.put("ktm@cs.stonybrook.edu", "Kevin McDonnell");
		testDatabase.put("sbuwolfie@hotmail.com", "The Wolfie");
		testDatabase.put("ilikeskype@gmail.com", "Sea Wolf");
		testDatabase.put("hpisacoolcompany@yahoo.com", "Ben Ten");
		testDatabase.put("socketsareamazing@outlook.com", "Haroon Akhtar");
		
		Server testServer = new Server(portNum, testDatabase); //Create an instance of the server with the portNum and "database"
		testServer.simulate(); //Simulate the server
	}

}
