import static org.junit.Assert.*;

import org.junit.Test;

public class ClientTest {
	Client test = new Client(null, 0, null);

	@Test
	public void testUserConnected() {
		String output = test.iden("IDEN Ray");
		//System.out.println(output);
		assertEquals("Welcome Ray", output);
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
		String output = test.quit("QUIT Ray");
		//System.out.println(output);
		assertEquals("Ray disconnected.", output);
	}
	@Test
	public void testHAIL() {
		String output = test.hail("HAIL Ray");
		//System.out.println(output);
		assertEquals("Ray: Hello everyone", output);
	}
	@Test
	public void testMESG() {
		String output = test.mesg("MESG");
		//System.out.println(output);
		assertEquals("Message sent.", output);
	}
	
}
