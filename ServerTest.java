import static org.junit.Assert.*;

import org.junit.Test;


public class ServerTest {
	private final int PORT_NUMBER = 1500;
	Server testServer;
	Server test = new Server(0);
	
	@Test
	public void initialization() {
		testServer = new Server(PORT_NUMBER);
	
	}
	@Test
	public void testServerNotNull() {
		assertNotNull(new Server(PORT_NUMBER));
	}

	@Test
	public void testUserConnected() {
		String output = test.iden("IDEN Ryan");
		//System.out.println(output);
		assertEquals("Welcome Ryan", output);
	}
	@Test
	public void testSTAT() {
		String output = test.stat("STAT 01/01/01");
		//System.out.println(output);
		assertEquals("Logged in since 01/01/01", output);
	}
	@Test
	public void testLIST() {
		String output = test.list("LIST 01/01/01");
		//System.out.println(output);
		assertEquals("List of the users connected at 01/01/01", output);
	}
	@Test
	public void testQUIT() {
		String output = test.quit("QUIT Ryan");
		//System.out.println(output);
		assertEquals("Ryan disconnected.", output);
	}
	@Test
	public void testHAIL() {
		String output = test.hail("HAIL Ryan");
		//System.out.println(output);
		assertEquals("Ryan: Hello everyone", output);
	}
	@Test
	public void testMESG() {
		String output = test.mesg("MESG");
		//System.out.println(output);
		assertEquals("Message sent.", output);
	}

}
