import java.net.*;
import java.io.*;
import java.util.*;

public class Client  {

	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// the server, the port and the username
	private String server, username;
	private int port;

	// Constructor called by console
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

}
