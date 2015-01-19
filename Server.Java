import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	
	//List of the Clients
	private ArrayList<ClientThread> al;
	private SimpleDateFormat sdf;
	private int port;r
	private boolean keepGoing;
	 
        //  server constructor	 
	public Server(int port) {

		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	public void start() {
		keepGoing = true;
		// create socket server 
		try 
		{
			ServerSocket serverSocket = new ServerSocket(port);
			// infinite loop
			while(keepGoing) 
			{
				display("Server waiting for Clients on port " + port + ".");
				Socket socket = serverSocket.accept();  

				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  
				al.add(t);									// add client to array list
				t.start();
			}

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
		
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
	
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		
			System.out.println(time);
		
	}
	
	private synchronized void broadcast(String message) {

		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// message to console
		
			System.out.print(messageLf);
		
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
		
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}
	
	public static void main(String[] args) {
		// start server on port 1500
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
	//Testing Purposes
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

  // Client thread
	class ClientThread extends Thread {
	
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
	
		int id;
		String username;
		Handler cm;
		String date;
                
                boolean loggedIn = false;
	
		ClientThread(Socket socket) {
			this.socket = socket;

			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// create output / input
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				username = (String) sInput.readObject();
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}
	
		public void run() {
			
			boolean keepGoing = true;
			while(keepGoing) {
			
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
				//type of messge recieved from handler
				switch(cm.getType()) {
					case Handler.MESG:
					if(loggedIn){
						boolean exists = false;
						for(ClientThread thread : al){
							if(thread.username.equalsIgnoreCase(cm.getUser())){
								if(thread.writeMsg(username + ": " + cm.getMessage())){
									writeMsg("Message sent.");
									exists = true;
									break;
								}
							}
						}
						if(!exists)
							writeMsg("User: " + cm.getUser() + " does not exist.");
					}
					else{
						writeMsg("Error! User not logged in");
					}
					break;

				case Handler.QUIT:
					writeMsg(username + " disconnected.");
                                        al.remove(al.size() - 1);
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
						if(cm.getUser().equals(ct.username))
							usernameExists = true;
					}
					if(usernameExists){
						writeMsg("Error! User " + cm.getUser() + " already exists. Please try another Username.");
						al.remove(al.size() - 1);
					}
					else{
						this.loggedIn = true;
						this.username = cm.getUser();
						writeMsg("Welcome " + username);
						date = new Date().toString() + "\n";
                                                System.out.println(username + " connected");
					}
					break;

				case Handler.HAIL:
					if(loggedIn){
						broadcast(username + ":" + cm.getMessage());
					}
					else{
						writeMsg("Error! User not logged in.");
					}
				}
			}
			close();
		}
		
		private void close() {
			// close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
		private boolean writeMsg(String msg) {
			
			if(!socket.isConnected()) {
				close();
				return false;
			}
		
			try {
				sOutput.writeObject(msg);
			}
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

