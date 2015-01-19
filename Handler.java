import java.io.*;
/*
 * This class defines the different type of messages that will be exchanged between the
 * Clients and the Server. 
 * When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
 * need to count bytes or to wait for a line feed at the end of the frame
 */
public class Handler implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// STAT: To receive the number of the users connected
	// MESG: Send a message to a specific user
	// QUIT: To disconnect from the Server
	// IDEN: If user is logged in
	// LIST: To receive the list of the users connected
	// HAIL: If another user has logged in
	static final int STAT = 0, MESG = 1, QUIT = 2,  IDEN = 3, LIST = 4, HAIL = 5;
	private int type;
	private String message;
	private String user;
	// constructor
	public Handler(int type, String user, String message) {
           
        this.type = type;
        this.user = user;
		this.message = message;
	}
	
	// getters
	int getType() {
		return type;
	}
    
    String getUser() {
        return user;
        }
        
	String getMessage() {
		return message;
	}
}

