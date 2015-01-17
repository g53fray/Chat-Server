import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
  private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		
	}
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		
			System.out.println(time);
		
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		
			System.out.print(messageLf);
		
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		Handler cm;
		// the date I connect
		String date;
                
                boolean loggedIn = false;

		// Constructore
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}
		// what will run forever
		public void run() {
			// Loop until QUIT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (Handler) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}

				// Switch on the type of message receive
				switch(cm.getType()) {

				case Handler.MESSAGE:
					if(loggedIn){	
						boolean exists = false;
						for(ClientThread thread : al){
							if(thread.username.equalsIgnoreCase(cm.getParam())){
								if(thread.writeMsg(username + ": " + cm.getMessage())){
									writeMsg("Message sent.");
									exists = true;
									break;
								}
							}
						}
						if(!exists)
							writeMsg("User: " + cm.getParam() + " does not exist.");
					}
					else{
						writeMsg("Error! User not logged in");
					}
					break;
					
				case Handler.QUIT:
					writeMsg(username + " disconnected.");
					keepGoing = false;
					break;
					
				case Handler.STAT:
					writeMsg("There are " + al.size() + " users currently logged in.\n");
					writeMsg("Current session status: ");
					if(loggedIn){
						writeMsg("Logged in since " + this.date);
					}
					else{
						writeMsg("Not logged in");
					}
					break;
					
				case Handler.LIST:
					if(loggedIn){
						writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
						// scan all the users connected
						for(int i = 0; i < al.size(); ++i) {
							ClientThread ct = al.get(i);
							writeMsg((i+1) + ") " + ct.username + " since " + ct.date + "\n");
						}	
					}
					else{
						writeMsg("Error! User not logged in");
					}
					break;
					
				case Handler.IDEN:
					if(loggedIn){
						writeMsg("Already logged in");
						break;
					}
					boolean usernameExists = false;
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						if(cm.getParam().equals(ct.username))
							usernameExists = true;
					}
					if(usernameExists){
						writeMsg("Error! User " + cm.getParam() + " already exists. Please try another Username.");
						al.remove(al.size() - 1);
					}
					else{
						this.loggedIn = true;
						this.username = cm.getParam();
						writeMsg("Welcome " + username);
						date = new Date().toString() + "\n";
					}
					break;
					
				case Handler.HAIL:
					if(loggedIn){
						broadcast(username + ": " + cm.getMessage());
					}
					else{
						writeMsg("Error! User not logged in.");
					}
				}
					
			}
			// remove client from the arrayList containing the list of the
			// connected Clients
			close();
		}
		
}

