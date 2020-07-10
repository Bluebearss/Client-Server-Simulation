import java.io.IOException;
import java.io.InputStream; //This helps read the final response for the client
import java.io.OutputStream; //This helps write the request for the server
import java.net.InetAddress; //This is to get the LocalHost
import java.net.Socket; //Sockets are used to communicate with the server
import java.net.UnknownHostException;

public class Client {
	private Socket clientSocket; //Socket for the client that to connect to the server
	private OutputStream clientRequest; //This request is for the client to send to the server
	private InputStream clientRead; //This is to read back the response from the server
	
	public Client(InetAddress address, int port) throws IOException //IOException is when socket can't be created
	{
		clientSocket = new Socket(address, port); //Creates a new clientSocket with the given server socket
		clientRequest = clientSocket.getOutputStream(); //Client's request written here
		clientRead = clientSocket.getInputStream(); //Client's response read here
	}

	public void simulateRequest(String email) throws IOException //This method simulates the request to the server for a name given an email
	{
		System.out.println("Hello Client");
		System.out.println();
		System.out.println("Welcome to the Addressbook Service");
		System.out.println();
		System.out.println("You are connected.");
		System.out.println();
		System.out.println("The Client's Email Request: " + email);
		
		if (email.length() >= 255) //If the email's length is >= 255 characters (255 bytes), return and print too long
		{
			System.out.println("Your email is too long");
			return;
		}
		
		byte[] requestArray = new byte[email.length() + 2]; //requestArray is array filled with request to server
		char request = 'Q';
		byte requestInBytes = (byte)request;
		requestArray[0] = requestInBytes; //First element in array is char 'Q' for request
		byte length = (byte)email.length();
		requestArray[1] = length; //Second element in array is email's length
		byte[] emailToBytes = email.getBytes(); //Converts String email to a byte[]
		int counter = 0;
		
		for (int i = 2; i < requestArray.length; i++) //Copies (byte[]) email into the requestArray
		{
			requestArray[i] = emailToBytes[counter];
			counter++;
		}
		
		try 
		{
			clientRequest.write(requestArray); //Write the requestArray into the OutputStream
		}
		catch (IOException e) 
		{
			System.out.println("Invalid request"); //Prints if the request is invalid
			e.printStackTrace();
		}
		
		System.out.println();
		
		int typeResponse = 0; //int representing first byte of the response message, the type
		int responseLength = 0; //int representing second byte of the response message, the name length
		byte[] name; //byte[] for the name result
		
		try //Read the first byte of the InputStream response from the server thread
		{
			typeResponse = clientRead.read();
		} 
		catch (IOException e)
		{
			System.out.println("Client's Response can not be read"); //Print if response can not be read
			e.printStackTrace();
		}
		char convertedType = (char)typeResponse; //Convert type to a char
		
		if (convertedType != 'R') //If the char is NOT a R for response, print invalid
		{
			System.out.println("Invalid response");
			return;
		}
		System.out.println("This is a type " + convertedType + " Message: Response");
		
		try //Read next byte for name length
		{
			responseLength = clientRead.read();
		} 
		catch (IOException e)
		{
			System.out.println("Client's Response can not be read");
			e.printStackTrace();
		}
		System.out.println("Your full name's length: " + responseLength);
		
		name = new byte[responseLength]; //set name byte[] to length of name
		try 
		{
			clientRead.read(name, 0, responseLength); //Read from the InputStream name.length times
		} 
		catch (IOException e) 
		{
			System.out.println("Client's Response can not be read");
			e.printStackTrace();
		}
		
		String nameString = new String(name); //Convert the byte[] to a String
		System.out.println();
		System.out.println("Your desired full name: " + nameString); //Print out final name
		
		try //Close the clientSocket
		{
			clientSocket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("There has been an error closing your socket");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Client testClient = null; //Creates an instance of a client
		try
		{
			testClient = new Client(InetAddress.getLocalHost(), 5000); //Create new client to connect to the server, using their
																	   //LocalHost and their port number
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		testClient.simulateRequest("cs310isfun@yahoo.com");
	}

}
