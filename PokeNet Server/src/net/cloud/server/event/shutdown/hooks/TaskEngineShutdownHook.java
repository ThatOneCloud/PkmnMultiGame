package net.cloud.server.event.shutdown.hooks;

import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;

import net.cloud.server.event.shutdown.ShutdownException;
import net.cloud.server.event.shutdown.ShutdownHook;

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
	 * @param out A PrintWriter to which status information will be output
	 */
	@Override
	public void shutdown(PrintWriter out) throws ShutdownException {
		out.println("Shutting down Task Engine");
		out.flush();
		
		// Tell the ExecutorService to stop
		taskExecutor.shutdown();
		
		out.println("Task Engine shut down");
		out.flush();
	}

}
