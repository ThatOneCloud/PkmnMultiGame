package net.cloud.client.event.task;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import net.cloud.client.ConfigConstants;
import net.cloud.client.event.shutdown.ShutdownHook;
import net.cloud.client.event.shutdown.ShutdownService;
import net.cloud.client.event.shutdown.hooks.TaskEngineShutdownHook;
import net.cloud.client.event.task.tasks.Task;
import net.cloud.client.event.task.voidtasks.VoidTask;
import net.cloud.client.logging.Logger;

/**
 * The TaskEngine is responsible for running jobs for other parts of the server. 
 * Tasks can be run immediately or after a delay. They can also be scheduled 
 * to run at a fixed rate every so often.  All tasks can be cancelled, as well. <br>
 * Submitting a task returns a Future which can be used to cancel the task, and
 * obtain a result. VoidTasks will have a null result. If the task engine is shutdown when 
 * the task is submitted, the task will not be run, and TaskEngine.REJECTED will be returned.<br>
 * The TaskEngine is a ShutdownService, and so its hook will stop execution of tasks.
 */
public class TaskEngine implements ShutdownService {
	
	/** The future returned when the task engine is shut down */
	public static final Future<Object> REJECTED = new RejectedFuture();
	
	/** Singleton instance */
	private static TaskEngine instance;
	
	/** The underlying object that will handle running tasks */
	private ScheduledExecutorService taskExecutor;
	
	/** The hook to stop the task engine */
	private ShutdownHook shutdownHook;
	
	/** Private constructor. Creates the executor and hook */
	private TaskEngine()
	{
		taskExecutor = Executors.newScheduledThreadPool(ConfigConstants.THREAD_POOL_SIZE);
		
		// Create the hook now - the pool starts when this instance is created
		shutdownHook = new TaskEngineShutdownHook(taskExecutor);
		
		Logger.writer().println("Task Engine now running");
		Logger.writer().flush();
	}
	
	
	/**
	 * Obtain a reference to the TaskEngine. This reference can be used to 
	 * submit tasks.
	 * @return The singleton TaskEngine instance
	 */
	public static TaskEngine instance()
	{
		if(instance == null)
		{
			synchronized(TaskEngine.class)
			{
				if(instance == null)
				{
					instance = new TaskEngine();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Submit a VoidTask to run as soon as possible.
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> submitImmediate(VoidTask task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.submitImmediate(taskExecutor::schedule);
	}
	
	/**
	 * Submit a VoidTask to run after waiting some amount of time
	 * @param delay The amount of time to wait before executing the task
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> submitDelayed(long delay, VoidTask task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.submitDelayed(taskExecutor::schedule, delay);
	}
	
	/**
	 * Submit a VoidTask to run as soon as possible, and run it repeatedly
	 * @param period The amount of time between each execution
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> scheduleImmediate(long period, VoidTask task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.scheduleImmediate(taskExecutor::scheduleAtFixedRate, period);
	}
	
	/**
	 * Submit a VoidTask to run after waiting some amount of time, and run it repeatedly
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> scheduleDelayed(long delay, long period, VoidTask task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.scheduleDelayed(taskExecutor::scheduleAtFixedRate, delay, period);
	}
	
	
	/**
	 * Submit a Task to run as soon as possible.
	 * @param task The task containing the code to run on the TaskEngine
	 * @param <V> Type of the result from the task
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	@SuppressWarnings("unchecked")
	public <V> Future<V> submitImmediate(Task<V> task)
	{
		if(taskExecutor.isShutdown())
		{
			return (Future<V>) REJECTED;
		}
		
		return task.submitImmediate(taskExecutor::schedule);
	}
	
	/**
	 * Submit a Task to run after waiting some amount of time
	 * @param delay The amount of time to wait before executing the task
	 * @param task The task containing the code to run on the TaskEngine
	 * @param <V> Type of the result from the task
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	@SuppressWarnings("unchecked")
	public <V> Future<V> submitDelayed(long delay, Task<V> task)
	{
		if(taskExecutor.isShutdown())
		{
			return (Future<V>) REJECTED;
		}
		
		return task.submitDelayed(taskExecutor::schedule, delay);
	}
	
	/**
	 * Submit a Task to run as soon as possible, and run it repeatedly
	 * @param period The amount of time between each execution
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public Future<?> scheduleImmediate(long period, Task<?> task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.scheduleImmediate(taskExecutor::scheduleAtFixedRate, period);
	}
	
	/**
	 * Submit a Task to run after waiting some amount of time, and run it repeatedly
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public Future<?> scheduleDelayed(long delay, long period, Task<?> task)
	{
		if(taskExecutor.isShutdown())
		{
			return REJECTED;
		}
		
		return task.scheduleDelayed(taskExecutor::scheduleAtFixedRate, delay, period);
	}

	/**
	 * Obtain the ShutdownHook for the TaskEngine. 
	 * The hook is created when the object is created, and so NPE shouldn't be an issue. 
	 * Shutting down the engine prevents tasks from being submitted, and will attempt 
	 * to allow all currently running tasks to complete.
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException
	{
		return shutdownHook;
	}

}
