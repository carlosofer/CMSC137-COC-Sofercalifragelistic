import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client{

//------------------------------------- UI ------------------------------------------//	
	//frame
	static final JFrame frame = new JFrame("Clash Of Clans");
	//panels
	//static JPanel logoPanel = new JPanel();
	static JPanel chatPanel = new JPanel();
	static JPanel gamePanel = new JPanel();
	static JPanel optionsPanel = new JPanel();
	//space where all chat can be read
	static JTextArea chatbox = new JTextArea(); 	
	//space where users will input their message
	static JTextArea messagebox = new JTextArea();
	//for scrollable JTextArea
	static JScrollPane messageScroll;
	static JScrollPane chatScroll;
	//send button
	static JButton SendButton = new JButton("SEND");	
	//Grid Constraints
	static final GridBagConstraints a = new GridBagConstraints();
	static final GridBagConstraints b = new GridBagConstraints();
	static final GridBagConstraints c = new GridBagConstraints();
	
	

	private ObjectInputStream streamInput; //declare lng muna
	private ObjectOutputStream streamOutput; //declare lng muna
	private Socket socket;
	
	private String server;
	private int port;
	private String username;

	static  Client client;

	static Ground[][] button = new Ground[45][45]; // yung para sa mapa
	static BuildButton[] bldg = new BuildButton[5]; // yung para sa menu
	static JButton troopbutton = new JButton("Place Troop");

	static int bldgflag = 0; // kung may ibibuild o wala
	static int troopflag = 0; // kung may ilalagay na troop o wala
	
	public Client(String server, int port, String username){
		this.server = server;
		this.port = port;
		this.username = username;
	}
	
	public boolean start(){ //client.start method
		try {
			socket = new Socket(this.server, this.port); // initialize the clien't socket(connection to the server), add contents to the socket such as servername and its port number
		} catch (UnknownHostException e) {		// continuation: d ko pa sure ung usage ng parameters pero gumana XD
			System.out.println("Unknow Host!");
			return false;
		} catch (IOException e) {
			System.out.println("Error connecting!");
			return false;
		}
		
		String message = "Connected to server " + socket.getInetAddress() + ":"+ socket.getPort(); //server IP, server port
		System.out.println(message);
		
		try {
			System.out.println("Stream init!");
			streamInput = new ObjectInputStream(socket.getInputStream()); //initilized the client's inputStream (receiving)
			System.out.println("Done in stream");
			streamOutput = new ObjectOutputStream(socket.getOutputStream()); //initilized the client's outputStream (sending)
			System.out.println("Stream done!");
		} catch (IOException e) {
			System.out.println("Error creating I/O streams!");
			return false;
		}
		System.out.println("Listener will be started!"); //connection wasa made but no listener
		new ListenerFromServer().start(); // this is a thread running in the client .start() runs the thread extended class
		System.out.println("Listener was started!"); //with listener na
		try {
			streamOutput.writeObject(this.username); //since nakuha na ntn ung getInputStream from the socket, pwede na tyo mag lagay ng laman and it will automatically be send sa server
		} catch (IOException e) {				//continuation: user name ng client and una ntn nilgay na msg
			System.out.println("Error in connecting to server!");
			disconnect(); //terminates clients connection to prevent unauthorize access (beware of hackers)
			return false;
		}
		
		return true;
	}
	
	private void disconnect() { // when something goes wrong with the connection, close the streams to prevent misuse
		try { 
			if(streamInput != null) streamInput.close();
			if(streamOutput != null) streamOutput.close();
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
	}



	private void sendMessage(String message) {
		try {
			this.streamOutput.writeObject(message); // since client is already running and connections to server was set, we can now write
		} catch (IOException e) {				//continuation: our msg to StreamOutput and it will be automatically sent to the server 
			e.printStackTrace();			//remember streamOutput has the socket.getOutputStream which means we can write send packets directly to the server
		}		
	}

	private class ListenerFromServer extends Thread{
		public void run(){
			while(true){ //will run infinetely
				try {
					String message = (String)streamInput.readObject(); //gets the msg received from the server's broadcast
					chatbox.append(message + "\n");
				
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("Server has closed the connection: "+ e);					
					System.exit(0);
				}
			}
		}
	}


	public static void main(String[] args) {
		int portNumber = 1221;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		switch(args.length){
					// > javac Client username portNumber serverAddr
					case 3:
						serverAddress = args[2];
					// > javac Client username portNumber
					case 2:
						try {
							portNumber = Integer.parseInt(args[1]);
						}
						catch(Exception e) {
							System.out.println("Invalid port number.");
							System.out.println("Usage is: > java Client <username> <portNumber> <serverAddress>");
							return;
						}
					// > javac Client username
					case 1: 
						userName = args[0];
					// > java Client
					case 0:
						break;
					default:
						System.out.println("Usage is: > java Client <username> <portNumber> <serverAddress>");
					return;
		}
		client = new Client(serverAddress, portNumber, userName); //initialize a new client object
		
		if(!client.start())return; // the the client was unable to start (Returns false)
		Scanner in = new Scanner(System.in);

//---------------------------------------------- UI -------------------------------------------------------------//
		//frame
		frame.setResizable(false);
		frame.setPreferredSize(new Dimension(1280, 720));
		frame.setLayout(new BorderLayout());
		//chat
		chatPanel.setPreferredSize(new Dimension(300, 700));
		chatPanel.setBackground(Color.lightGray);
		chatPanel.setLayout(new GridBagLayout());	

		//game
		gamePanel.setPreferredSize(new Dimension(700, 700));
		gamePanel.setBackground(Color.PINK);
		gamePanel.setLayout(new GridLayout(45,45));

		//options
		optionsPanel.setPreferredSize(new Dimension(300, 700));
		optionsPanel.setLayout(new GridLayout(5, 1));
// ------------------------------------------------------------------------------------------------------------ //
		//chat layout
		a.gridwidth = GridBagConstraints.REMAINDER;
		a.anchor = GridBagConstraints.LINE_END;
		chatbox.setEditable(false);
		chatbox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatbox.setLineWrap(true);
        chatScroll = new JScrollPane(chatbox);
		chatScroll.setPreferredSize(new Dimension(290, 550));
		chatPanel.add(chatScroll, a);

		a.insets = new Insets(10,0,0,0);
		a.anchor = GridBagConstraints.PAGE_END;
		messagebox.requestFocusInWindow();
		messagebox.setLineWrap(true);
		messageScroll = new JScrollPane(messagebox);
		messageScroll.setPreferredSize(new Dimension(290, 50));
		chatPanel.add(messageScroll, a);

		chatPanel.add(SendButton, a);

			//getting the user message
			String userMessage = messagebox.getText();
				if(userMessage.equalsIgnoreCase("logout")){
				 	client.disconnect();
				 	in.close();
				 }



		SendButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent sendB){					
	                client.sendMessage(messagebox.getText());
	                messagebox.setText("");
				}
			});

// ------------------------------------------------------------------------------------------------------------ //
//---------------------------------------- CENTER PANEL ------------------------------------------------------//
// i-seset yung grid/lupa na paglalagyan ng base
		for(int i=0; i<45; i++){
			for(int j=0; j<45; j++){
				button[i][j] = new Ground();
				button[i][j].setPreferredSize(new Dimension(25, 25));
				button[i][j].setBackground(Color.GREEN);
				button[i][j].row = i;
				button[i][j].col = j;
				button[i][j].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent event){
							Ground src = (Ground)event.getSource();
							Ground gr = button[src.row][src.col];
							if(bldgflag !=0){ // pag may ibibuild
								boolean valid = false;
								// boundrow and boundcol
								// sila yung magdedetermine nung range ng ibibuild na building
								int boundrow = 0;
								int boundcol = 0;
								Building b = null;
								if(bldgflag==1){ // pag town hall
									boundrow = 3;
									boundcol = 3;
									b = new Building("Town Hall", 1, 7, 7, 2000);
								} else if(bldgflag==2){ // pag army camp
									boundrow = 1;
									boundcol = 1;
									b = new Building("Army Camp", 2, 2, 2, 400);
								} else if(bldgflag==3){ // pag gold mine
									boundrow = 1;
									boundcol = 1;
									b = new Building("Gold Mine", 3, 3, 3, 750);
								} else if(bldgflag==4){ // pag elixir collector
									boundrow = 1;
									boundcol = 1;
									b = new Building("Elixir Collector", 4, 3, 3, 750);
								} else if(bldgflag==5){ // pag wall
									boundrow = 0;
									boundcol = 0;
									b = new Building("Wall", 5, 1, 1, 550);
								}
								// i-checheck niya kung out of bounds ba yung ibibuild tapos ichecheck kung tatama ba sa building o troop yung ibibuild
								// magsisimula siya dun sa may boundary ng building o yung pinaka outline ng building, papunta sa center
								check:
								if(b.row%2!=0 && b.col%2!=0){ 
									for(int i = boundrow; i>=0; i--){
										for(int j = boundcol; j>=0; j--){
											if(((src.row-i)>=0 && (src.col-j)>=0) && ((src.row+i)<45 && (src.col+j)<45)){
												if(button[src.row-i][src.col-j].occupiedflag == false && button[src.row-i][src.col-j].steppedflag == false && button[src.row+i][src.col+j].occupiedflag == false && button[src.row+i][src.col+j].steppedflag == false){
													if(button[src.row-i][src.col+j].occupiedflag == false && button[src.row-i][src.col+j].steppedflag == false && button[src.row+i][src.col-j].occupiedflag == false && button[src.row+i][src.col-j].steppedflag == false){
														valid = true;
													} else {
														valid=false;
														break check;
													}
												} else {
													valid=false;
													break check;
												}
											} else {
												valid=false;
												break check;
											}
										}
									}
								} else {
									for(int i = boundrow; i>=0; i--){
										for(int j = boundcol; j>=0; j--){
											if(((src.row-i)>=0 && (src.col-j)>=0) && ((src.row+i)<45 && (src.col+j)<45)){
												if(button[src.row+i][src.col+j].occupiedflag == false && button[src.row+i][src.col+j].steppedflag == false){
													valid = true;
												} else {
													valid=false;
													break check;
												}
											} else {
												valid=false;
												break check;
											}
										}
									}
								}
								
								// pag walang conflict, proceed to build
								// iaassign na yung building sa bawat tiles na range ng building na yun
								// tapos ididisable yung button na naassign ng building
								building:
								if(valid == true){
									if(b.row%2!=0 && b.col%2!=0){
										for(int i = boundrow; i>=0; i--){
											for(int j = boundcol; j>=0; j--){
												button[src.row-i][src.col-j].build(bldgflag, b, button[src.row-i][src.col-j]);
												button[src.row-i][src.col-j].occupiedflag = true;
												button[src.row-i][src.col-j].steppedflag = true;
												button[src.row-i][src.col+j].build(bldgflag, b, button[src.row-i][src.col+j]);
												button[src.row-i][src.col+j].occupiedflag = true;
												button[src.row-i][src.col+j].steppedflag = true;
												button[src.row+i][src.col-j].build(bldgflag, b, button[src.row+i][src.col-j]);
												button[src.row+i][src.col-j].occupiedflag = true;
												button[src.row+i][src.col-j].steppedflag = true;
												button[src.row+i][src.col+j].build(bldgflag, b, button[src.row+i][src.col+j]);
												button[src.row+i][src.col+j].occupiedflag = true;
												button[src.row+i][src.col+j].steppedflag = true;
												button[src.row][src.col].build(bldgflag, b, button[src.row][src.col]);
												button[src.row-i][src.col-j].setEnabled(false);
												button[src.row-i][src.col+j].setEnabled(false);
												button[src.row+i][src.col-j].setEnabled(false);
												button[src.row+i][src.col+j].setEnabled(false);
												button[src.row][src.col].setEnabled(false);
												button[src.row][src.col].occupiedflag = true;
												button[src.row][src.col].steppedflag = true;
											}
										}
									} else {
										for(int i = boundrow; i>=0; i--){
											for(int j = boundcol; j>=0; j--){
												button[src.row+i][src.col+j].build(bldgflag, b, button[src.row+i][src.col+j]);
												button[src.row+i][src.col+j].occupiedflag = true;
												button[src.row+i][src.col+j].steppedflag = true;
												button[src.row][src.col].build(bldgflag, b, button[src.row][src.col]);
												button[src.row+i][src.col+j].setEnabled(false);
												button[src.row][src.col].setEnabled(false);
												button[src.row][src.col].occupiedflag = true;
												button[src.row][src.col].steppedflag = true;
											}
										}
									}
									
									bldgflag = 0;
								}
								
							}
							if(troopflag == 1){
								button[src.row][src.col].setBackground(Color.RED);
								button[src.row][src.col].setEnabled(false);
								troopflag = 0;
								
								Thread t = new Thread() {
									public void run(){
										while(true){
											if(button[gr.row][gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row][gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row][gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
											if(button[gr.row][gr.col-1].steppedflag == false){
												gr.setBackground(Color.GREEN);
												gr.setEnabled(true);
												button[gr.row][gr.col-1].setBackground(Color.RED);
												button[gr.row][gr.col-1].setEnabled(false);
												gr = button[gr.row][gr.col-1];
											}
										}
									}
								};
								
							}
					}
				});
				gamePanel.add(button[i][j]);
			}
		}
//------------------------------------------------------------------------------------------------------------//

//----------------------------------------OPTIONS PANEL -------------------------------------------------------//
		// yung sa menu buttons
		for(int i=0; i<5; i++){
			bldg[i] = new BuildButton(i+1);
			bldg[i].setText(bldg[i].bldgname);
			bldg[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){
						BuildButton src = (BuildButton)event.getSource();
						bldgflag = src.bldgnum;
						if(troopflag!=0)
							troopflag = 0;
				}
			});
			optionsPanel.add(bldg[i]);
		}
		troopbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				troopflag = 1;
				if(bldgflag!=0)
					bldgflag = 0;
			}
		});

		optionsPanel.add(troopbutton);
//-----------------------------------------------------------------------------------------------------------//
		
		
		//adding the panels to frame
		frame.add(chatPanel, BorderLayout.WEST);
		frame.add(gamePanel, BorderLayout.CENTER);
		frame.add(optionsPanel, BorderLayout.EAST);
		//frame additional options
		frame.setVisible(true);		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		


	


	}//static void main end

}//class end