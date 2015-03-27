package net.cloud.server.task;

import java.util.concurrent.ExecutionException;

import net.cloud.server.event.task.TaskEngine;
import net.cloud.server.event.task.tasks.Task;
import net.cloud.server.event.task.voidtasks.VoidTask;

import org.junit.Test;

import static org.junit.Assert.*;

/** Test plain tasks. Both Task and VoidTask */
public class TaskTest {
	
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
			String value = TaskEngine.instance().submitImmediate(task).get();
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
			TaskEngine.instance().submitImmediate(task).get();
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
			TaskEngine.instance().submitImmediate(task).get();
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
			TaskEngine.instance().submitImmediate(task).get();
			fail("Exception should have been thrown");
		} catch (InterruptedException | ExecutionException e) {
			// Goody - it got thrown and caught
			assertTrue(true);
		}
	}

}
