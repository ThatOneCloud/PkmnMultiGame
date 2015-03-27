package net.cloud.client.event.shutdown.hooks;

import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.cloud.client.event.shutdown.ShutdownException;
import net.cloud.client.event.shutdown.ShutdownHook;

/**
 * A ShutdownHook designed to stop the task engine. 
 * When this hook completes, the task engine will not accept 
 * any more tasks - but the tasks currently running will complete.
 */
public class TaskEngineShutdownHook implements ShutdownHook {
	
	/** How long to wait for tasks to finish before calling it quits */
	public static final long TERMINATION_TIMEOUT = 1000;
	
	/** The ExecutorService the tasks are being run on */
	private ScheduledExecutorService taskExecutor;
	
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
	 * @param out A PrintWriter to which status information will be output
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException
	{
		out.println("Shutting down Task Engine");
		out.flush();
		
		// Tell the ExecutorService to stop
		try {
			taskExecutor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS);
			taskExecutor.shutdown();
		} catch (InterruptedException e) {
			out.println("Task Engine interrupted during shutdown");
			out.flush();
		}
		
		out.println("Task Engine shut down");
		out.flush();
	}

}
