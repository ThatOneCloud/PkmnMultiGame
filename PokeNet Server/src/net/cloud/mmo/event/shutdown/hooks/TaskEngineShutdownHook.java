package net.cloud.mmo.event.shutdown.hooks;

import java.util.concurrent.ScheduledExecutorService;

import net.cloud.mmo.event.shutdown.ShutdownException;
import net.cloud.mmo.event.shutdown.ShutdownHook;

/**
 * A ShutdownHook designed to stop the task engine. 
 * When this hook completes, the task engine will not accept 
 * any more tasks - but the tasks currently running will complete.
 */
public class TaskEngineShutdownHook implements ShutdownHook {
	
	/** The ExecutorService the tasks are being run on */
	ScheduledExecutorService taskExecutor;
	
	/**
	 * Create a shutdown hook for a TaskEngine
	 * @param taskExecutor The ExecutorService the engine using to run tasks
	 */
	public TaskEngineShutdownHook(ScheduledExecutorService taskExecutor)
	{
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Stop the task service. It will not accept new tasks, but
	 * currently running tasks will make an attempt to complete. 
	 * Returns immediately, rather than waiting for the tasks to complete.
	 */
	@Override
	public void shutdown() throws ShutdownException {
		System.out.println("Shutting down Task Engine");
		
		// Tell the ExecutorService to stop
		taskExecutor.shutdown();
		
		System.out.println("Task Engine shut down");
	}

}
