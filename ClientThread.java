import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {

	public ObjectInputStream streamInput; //declare lng muna
	public ObjectOutputStream streamOutput; //declare lng muna
	public Socket socket;
	private String username;
	private String message;
	private String ipAddress;
	private int id;

	public ClientThread(Socket socket) { // constructor for the client's thread
		this.socket = socket; // socket contains information sent by the client class
		this.id = ChatServer.getNewId();
		
		System.out.println("Creating I/O streams for client threads...");
		try {
			System.out.println("Client with ip: " + socket.getRemoteSocketAddress().toString() + " has connected to the server");
			System.out.println("Initializing new client..."); //note: nabasa ko sa net dpat daw mauuna ang outputstream pag sa server. so wag pag papalitin
			streamOutput = new ObjectOutputStream(socket.getOutputStream());  //initilized the client's outputStream (sending)
			streamInput = new ObjectInputStream(socket.getInputStream());     //initilized the client's getInputStream (receiving)
			System.out.println("Streams created!");
			username = (String) streamInput.readObject(); // remember,socket contains info sent by the client, ung unang send ng client ay username lng rembmer? so since
			ipAddress = socket.getRemoteSocketAddress().toString();
			System.out.println(username + " has entered the chat room!"); //cont: nsatin na ung socket.getInputStream, pwede n ntn makuha ung laman nun wc is ung user name
		} catch (Exception e) {
			System.out.println("Error creating streams!\n"+e);
			return;
		}
	}
	
	private void close() {
		// try to close the connection to prevent misuse
		try {
			if(streamOutput != null) streamOutput.close();
			if(streamInput != null) streamInput.close();
			if(socket != null) socket.close();
		}
		catch(Exception e) {}
	}
	
	public boolean writeMessage(String message, String username) {
		if(!socket.isConnected()){ // if the socket is not connected anymore
			close();
			return false; //will result to removing the client
		}
		try { 
			streamOutput.writeObject(username + " : "  + message); //else we broadcast. since we are looping through a list of clients, each client will receive the same StreamOutput
		} catch (IOException e) {			//cont: meaning lahat sila mkktanggap ng msg na nasend ng bawat isa sakanila INCLUDING the client n nag send ng msg
			System.out.println("Error sending message to "+username); //cont: which is mali pa XD dpat ma filter na ung sender ay d dpat sendan ng msg na un
		}
		//TODO something fishy here. what if an error was thrown and caught by CATCH block, still return true? oshit
		return true;
	}

	public String getUsername() { //getters
		return this.username;
	}

	public int getConnectionId() { //getters
		return this.id; 
	}

	public String getIPAddress() { //getters
		return this.ipAddress; 
	}

	public void run(){ // result of client.start();
		boolean keepRunning = true;
		while(keepRunning){ // will run infinitely
			try {
				message = (String) streamInput.readObject(); //read the msg coming from this client (we have a list of clietns)
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				break;
			} catch (IOException e) {
				System.out.println("Server: " + username + " has disconnected/Error on reading streams!");
				break;
			}
			//now, broadcast that msg to other clients
			if(message.equalsIgnoreCase("/logout"))break; //mali pa to, pag eto input ilologout ung client
				ChatServer.broadcast(message, username, ipAddress); // go to the server to loop through the list of clients
		}
		
		ChatServer.remove(id);
		close();
	}

}
