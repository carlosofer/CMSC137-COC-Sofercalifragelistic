import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private static int connectionID; //uniqure id for each connection - unused
	private static List<ClientThread> clientList = new ArrayList<ClientThread>(); //eto ung array list to keep track of all the clients
	private int port; //port no. to listen for connection
	private boolean keepRunning; //boolean that can be turned of to stop the server from running
	
	public ChatServer(int port){ //chat server obj constructor
		this.port = port; //assign the port
	}
	
	public static void main(String[] args){
		int portNumber = 1221; //default port number
		switch(args.length){
		case 0: break;
		case 1: // if the user provided a port number 
			try {
				portNumber = Integer.parseInt(args[0]); //use the user provided port number
			} catch (NumberFormatException e) {
				System.out.println("Invalid port number!"); //if port is not valid
				return;
			}
		default:
			System.out.println("Usage is: java ChatServer <port_number>");
			return;
		}
		
		ChatServer chatServer = new ChatServer(portNumber); // create a ChatServer object and start it
		chatServer.start(); //
	}
	
	public void start(){ //a method of the chatServer object
		// Just an initiator to what this server should do. "SERVER START"
		keepRunning = true; // initial loop contidition
		
		try {
			ServerSocket serverSocket = new ServerSocket(port); // create a new socket used by the server

			while(keepRunning){ //infinite loop waiting for conection 
				System.out.println("Server waiting for clients on port "+port+".");				
				Socket socket = serverSocket.accept(); //accepts connection everytime a new client use the port
			
				if(!keepRunning)break; //when something triggered the server to stop, break else

					ClientThread client = new ClientThread(socket); //initilized a new client thread NOTE: socket contains info about the client
					clientList.add(client); //save that client's info to the arraylist
					client.start(); // start that clients session NOTE: this is not the client class 
								//NOTE: the class of this obj extends a tread so .start() runs the thread

			}
			
			//if stopped
			serverSocket.close();
			
			for(ClientThread client : clientList){
				client.streamInput.close();
				client.streamOutput.close();
				client.socket.close();
			}
			
		} catch (IOException e) {
			System.out.println("Server socket exception: "+e);
		}
	}
	
	public static synchronized void broadcast(String message, String username, String ipAddress){ //sabi need daw ung synchronized kc we're working on multiple threads here
		for(ClientThread client: clientList){ // foreach client na nsa list ntn
			//if(client.getUsername != (String)username){ // ETO UN KATH DPAT MAKIKITA NYA YAN E HUHU
				if(!client.writeMessage(message, username)){ // run the method client.writeMessage in clientThread class
					clientList.remove(client); // if returend false, remove the client form the connection
					System.out.println("Disconnected from server: "+client.getUsername()+" was removed from list.");
				}else{
				System.out.println("Message Received from: " + username + " msg: " + message);
				}
			//}else{ //else kung xa ung sender skip lng xa sa pag brobroadcast (KATH)
			//	continue;
			//}		
		}
	}
	
	public static synchronized void remove(int id){
		for(ClientThread client : clientList){
			if(client.getConnectionId() == id){
				clientList.remove(client);
				return;
			}
		}
	}
	
	public static int getNewId(){
		return ++connectionID;
	}
}
