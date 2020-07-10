import java.io.IOException;
import java.io.InputStream; //Carries InputStream to read client's request
import java.io.OutputStream; //Carries OutputStream to write response to the client
import java.net.Socket; //Socket class for the clientSocket
import java.util.HashMap; //Passes in the database from the server
import java.util.Iterator; //Used to check the database
import java.util.Map; //Used to check the database

/*This class is meant to implement what happens when the thread runs for the given client. It will do the work
for the server so multiple clients can be taken at once by the server. The server thread will run ThreadNewRunnable*/
public class ThreadNewRunnable implements Runnable{

	private Socket clientSocket; //This is the client's socket that will connect to this thread
	private InputStream clientInput; //The client's request
	private OutputStream clientOutput; //The response for the client
	private HashMap<String, String> serverDatabase; //The server's database
	
	public ThreadNewRunnable(Socket clientSocket, HashMap<String, String> serverDatabase) throws IOException
	{
		this.clientSocket = clientSocket;
		this.serverDatabase = serverDatabase;
		clientInput = clientSocket.getInputStream();
		clientOutput = clientSocket.getOutputStream();
	}
	
	public void run() //When the thread starts, it will do this run method
	{
		int typeRequest = 0; //Type Request for 'Q'
		int requestLength = 0; //Email's length
		byte[] email; //byte[] for email
		String fullName = ""; //fullName string when retrieved fullName
		
		try //Read first byte of request
		{
			typeRequest = clientInput.read();
		} 
		catch (IOException e)
		{
			System.out.println("Client's Request can not be read");
			e.printStackTrace();
		}
		char convertedType = (char)typeRequest; //Convert first byte to char, has to be 'Q'
		
		if (convertedType != 'Q') //If the convertedType is not 'Q' print invalid request
		{
			System.out.println("Invalid request");
			return;
		}
		System.out.println("This is a type " + convertedType + " Message: Request");
		
		try //Read second byte of the request
		{
			requestLength = clientInput.read();
		} 
		catch (IOException e)
		{
			System.out.println("Client's Request can not be read");
			e.printStackTrace();
		}
		System.out.println("Request's email length: " + requestLength); //Print the email's length
		
		email = new byte[requestLength]; //email byte[] is size of email's length
		try 
		{
			clientInput.read(email, 0, requestLength); //read from InputStream email.length() times
		} 
		catch (IOException e) 
		{
			System.out.println("Client's Request can not be read");
			e.printStackTrace();
		}
		
		String emailString = new String(email); //Convert email byte[] to a String
		System.out.println("Client's email: " + emailString); //Print out the client's email
		
		//THIS IS USED TO CHECK IF THE DATABASE HAS ANY ENTRIES WITH NAMES THAT ARE LENGTH >= 255
		Iterator<Map.Entry<String, String>> checker = serverDatabase.entrySet().iterator(); //This iterator is used to check database
		while(checker.hasNext()) //While there is an entry in the HashMap
		{
			Map.Entry<String, String> instance = checker.next(); //Get the current instance of the HashMap
			String databaseName = instance.getValue(); //Get its name
			
			if(databaseName.length() >= 255) //If the name corresponding to the email is >= 255, remove that entry from database
			{
				checker.remove();
			}
		}
		
		if (serverDatabase.containsKey(emailString)) //Search in the database if it contains the given email
		{
			fullName = serverDatabase.get(emailString); //If it DOES contain it, get the corresponding name and assign to fullName
		}
		else
		{
			fullName = "Your email is NOT in the Addressbook Service"; //If it DOES NOT contain given email, set fullName to 
													   //"Your email is NOT in the Addressbook Service"
		}
		
		byte[] clientResponse = new byte[fullName.length() + 2]; //Set clientResponse array to name.length + 2
		char response = 'R'; //'R' for response type
		byte responseInBytes = (byte)response;
		clientResponse[0] = responseInBytes; //Set first byte of response to response type
		byte length = (byte)fullName.length();
		clientResponse[1] = length; //Set second byte of response to name's length
		byte[] nameToBytes = fullName.getBytes(); //Convert name's string to a byte[]
		int counter = 0;
		
		for (int i = 2; i < clientResponse.length; i++) //Copy the name byte[] to the client response
		{
			clientResponse[i] = nameToBytes[counter];
			counter++;
		}
		
		try //Write the response back to the client
		{
			clientOutput.write(clientResponse); 
		}
		catch (IOException e) 
		{
			System.out.println("Invalid response");
			e.printStackTrace();
		}
		
		try //Close the clientSocket
		{
			clientSocket.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Client's socket can not be closed");
			e.printStackTrace();
		}
	}

}
