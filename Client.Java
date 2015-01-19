import java.net.*;
import java.io.*;
import java.util.*;

public class Client  {
	
	// Static finals, used as arguments, for if statements etc
	private static final int FIRST_ARG = 0;
	private static final int SECOND_ARG = 1;
	private static final int THIRD_ARG = 2;

	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// the server, the port and the username
	private String server;
	private String username;
	private int port;

	
	 //Constructor called by console  
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Streams */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Sends the username to the server as a string
		// All other messages will be Handler objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// It works, the caller is informed
		return true;
	}

	/*
	 * To send a message to the console
	 */
	private void display(String msg) {
			System.out.println(msg);      

	}

	/*
	 * To send a message to the server
	 */
	void sendMessage(Handler msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}
        public static int nthOccurrence(String str, char c, int n) {
             int pos = str.indexOf(c, 0);
             while (n-- > 0 && pos != -1)
                pos = str.indexOf(c, pos+1);
             return pos;
        }
	 // When an error occurs close the Input/Output streams and disconnect 
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {}
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {}	
	}
	//For Testing Purposes
	String iden(String IDEN){
		IDEN = "Welcome" +IDEN.substring(4); 
		return IDEN;
	}
	String stat(String STAT){
		STAT = "Logged in since" +STAT.substring(4);
		return STAT;
	}
	String list(String LIST){
		LIST = "List of the users connected at" +LIST.substring(4);
		return LIST;
	}
	String quit(String QUIT){
		QUIT =  QUIT.substring(5) +" disconnected.";
		return QUIT;
	}
	String hail(String HAIL){
		HAIL =  HAIL.substring(5) +": Hello everyone";
		return HAIL;
	}
	String mesg(String MESG){
		MESG = "Message sent." +MESG.substring(4);
		return MESG;
	}
	
	/*
	 * To start the Client in console mode use one of the following command
	 * > java Client
	 * > java Client username
	 * > java Client username portNumber
	 * > java Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 * In console mode, if an error occurs the program simply stops
	 */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch(args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[SECOND_ARG];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[THIRD_ARG]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[FIRST_ARG];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		if(!client.start())
			return;
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
                 
           
		// loop forever for message from the user
		while(true) {
                  
                     String msg = scan.nextLine();
                     if(msg.length() < 4){
                      System.out.println("Please provide a proper command");  
                      
                    } else {
                     String Command = msg.substring(0,4);
                     System.out.print("> ");
			// read message from user
			 
			// logout if message is QUIT
                        if(Command.equals("QUIT")) {
                        	client.sendMessage(new Handler(Handler.QUIT,"",""));
                                // break to do the disconnect
                        	break;
                        } else if(Command.equals("STAT")) {
                        	client.sendMessage(new Handler(Handler.STAT,"",""));				
			
                        } else if(Command.equals("IDEN")) {
                            
                              if(msg.length() > 5){
                                 String User = msg.substring(5,msg.length());
                                 client.sendMessage(new Handler(Handler.IDEN,User,""));
                              } else {
                                     System.out.println("Please provide user IDEN username");
                                 }
   					
                        } else if(Command.equals("LIST")) {
                        	client.sendMessage(new Handler(Handler.LIST,"",""));				
			
                        } else if(Command.equals("HAIL")) {
                           
                             if(msg.length() > 5){
                                 String Message = msg.substring(5,msg.length());
                                    client.sendMessage(new Handler(Handler.HAIL,"", Message));	
                          
                              } else {
                                     System.out.println("Please enter proper command");
                                }
                        
                        } else if(Command.equals("MESG")){				// default to ordinary message
                        	if (msg.length() > 5){
                             String[] words = msg.split(" ");
                             int ulength = words[1].length();
                                String User = msg.substring(5,(ulength +5));
                                int index = User.length() + 5;
                                String Message = msg.substring(index ,msg.length());
                            client.sendMessage(new Handler(Handler.MESG,User,Message));
			
                            } else {
                                System.out.println("Provide proper command");
                                }
                        
                        	} else {
                            System.out.println("Provide proper command");
                        }
                    }
                }
         	client.disconnect();
                }
	    // Finished, disconnect 
	/*
	 * a class that waits for the message from the server prints in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					// console mode print the message and add back the prompt
						System.out.println(msg);
						System.out.print("> ");
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					break;
				}
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
