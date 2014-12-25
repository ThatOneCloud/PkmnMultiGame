package net.cloud.mmo.task;

import java.util.concurrent.ExecutionException;

import net.cloud.mmo.event.shutdown.ShutdownException;
import net.cloud.mmo.event.shutdown.ShutdownHook;
import net.cloud.mmo.event.task.TaskEngine;
import net.cloud.mmo.event.task.tasks.Task;
import net.cloud.mmo.event.task.voidtasks.VoidTask;
import net.cloud.mmo.util.IOUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/** Test plain tasks. Both Task and VoidTask */
public class TaskTest {
	
	private static ShutdownHook taskEngineHook;
	
	@BeforeClass
	public static void startTaskEngine()
	{
		taskEngineHook = TaskEngine.getInstance().getShutdownHook();
	}
	
	@Test
	public void testSuccessfulExecution()
	{
		// Going to test if a Task completes successfully
		final String VALUE = "Expected return value";
		
		Task<String> task = new Task<String>() {
			@Override
			public String execute() throws RuntimeException {
				return VALUE;
			}
		};
		
		try {
			String value = TaskEngine.getInstance().submitImmediate(task).get();
			assertTrue(value.equals(VALUE));
		} catch (InterruptedException | ExecutionException e) {
			fail("Task should not have thrown exception");
		}
	}
	
	@Test
	public void testExceptionalExecution()
	{
		Task<String> task = new Task<String>() {
			@Override
			public String execute() throws RuntimeException {
				throw new RuntimeException("Expected exception");
			}
		};
		
		try {
			TaskEngine.getInstance().submitImmediate(task).get();
			fail("Exception should have been thrown");
		} catch (InterruptedException | ExecutionException e) {
			// Goody. Exception was thrown and caught
			assertTrue(true);
		}
	}
	
	@Test
	public void testSuccessfulVoidExecution()
	{		
		VoidTask task = new VoidTask() {
			@Override
			public void execute() throws RuntimeException {
				// Do nothing. I guess we'll assert that this was called.. not like we'd know if it wasn't
				assertTrue(true);
			}
		};
		
		try {
			// No value, but if we make it to the assertion we're good
			TaskEngine.getInstance().submitImmediate(task).get();
			assertTrue(true);
		} catch (InterruptedException | ExecutionException e) {
			fail("Task should not have thrown exception");
		}
	}
	
	@Test
	public void testExceptionalVoidExecution()
	{		
		VoidTask task = new VoidTask() {
			@Override
			public void execute() throws RuntimeException {
				// Throw an exception, so Future.get() throws an exception
				throw new RuntimeException("Expected exception");
			}
		};
		
		try {
			// No value, but an exception should occur
			TaskEngine.getInstance().submitImmediate(task).get();
			fail("Exception should have been thrown");
		} catch (InterruptedException | ExecutionException e) {
			// Goody - it got thrown and caught
			assertTrue(true);
		}
	}
	
	@AfterClass
	public static void stopTaskEngine()
	{
		try {
			taskEngineHook.shutdown(IOUtil.SYS_OUT);
		} catch (ShutdownException e) {
			e.printStackTrace();
		}
	}

}
