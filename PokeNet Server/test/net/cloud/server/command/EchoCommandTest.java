package net.cloud.server.command;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;

import net.cloud.server.event.command.CommandException;
import net.cloud.server.event.command.CommandHandler;

import org.junit.Test;

public class EchoCommandTest {

	@Test
	public void testReqParam() {
		String expectedResult = "hello";
		
		try {
			String result = CommandHandler.getInstance().handleCommand("::echo hello").get();
			
			assertTrue(result.equals(expectedResult));
		} catch (InterruptedException | ExecutionException | CommandException e) {
			fail("No exception expected");
		}
	}
	
	@Test
	public void testShortOptParam() {
		String expectedResult = "helloworld";
		
		try {
			String result = CommandHandler.getInstance().handleCommand("::echo hello -c world").get();
			
			assertTrue(result.equals(expectedResult));
		} catch (InterruptedException | ExecutionException | CommandException e) {
			fail("No exception expected");
		}
	}
	
	@Test
	public void testLongOptParam() {
		String expectedResult = "helloworld";
		
		try {
			String result = CommandHandler.getInstance().handleCommand("::echo hello --concat world").get();
			
			assertTrue(result.equals(expectedResult));
		} catch (InterruptedException | ExecutionException | CommandException e) {
			fail("No exception expected");
		}
	}
	
	@Test
	public void testQuoteDelim() {
		String expectedResult = "hello world";
		
		try {
			String result = CommandHandler.getInstance().handleCommand("::echo hello -c \" world\"").get();
			
			assertTrue(result.equals(expectedResult));
		} catch (InterruptedException | ExecutionException | CommandException e) {
			fail("No exception expected");
		}
	}
	
	@Test
	public void testInvalParam() {
		try {
			CommandHandler.getInstance().handleCommand("::echo").get();
			
			fail("Exception should have been thrown");
		} catch (InterruptedException | ExecutionException | CommandException e) {
			// Goody. Exception expected due to lack of params
			assertTrue(true);
		}
	}

}
