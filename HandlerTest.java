import static org.junit.Assert.*;

import org.junit.Test;


public class HandlerTest {

	@Test
	public void testSTAT() {
		Handler test = new Handler(0, null, null);
		int output = test.getType();
		//String output = test.getType();
		assertEquals(0, output);
	}
	@Test
	public void testMESSAGE() {
		Handler test = new Handler(1, null, null);
		int output = test.getType();
		assertEquals(1, output);
	}
	@Test
	public void testQUIT() {
		Handler test = new Handler(2, null, null);
		int output = test.getType();
		assertEquals(2, output);
	}
	@Test
	public void testIDEN() {
		Handler test = new Handler(3, null, null);
		int output = test.getType();
		assertEquals(3, output);
	}
	@Test
	public void testLIST() {
		Handler test = new Handler(4, null, null);
		int output = test.getType();
		assertEquals(4, output);
	}
	@Test
	public void testHAIL() {
		Handler test = new Handler(5, null, null);
		int output = test.getType();
		assertEquals(5, output);
	}
}
