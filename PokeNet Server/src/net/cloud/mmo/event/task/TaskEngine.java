package net.cloud.mmo.event.task;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import net.cloud.mmo.event.shutdown.ShutdownHook;
import net.cloud.mmo.event.shutdown.ShutdownService;
import net.cloud.mmo.event.shutdown.hooks.TaskEngineShutdownHook;
import net.cloud.mmo.event.task.tasks.Task;
import net.cloud.mmo.event.task.voidtasks.VoidTask;

/**
 * The TaskEngine is responsible for running jobs for other parts of the server. 
 * Tasks can be run immediately or after a delay. They can also be scheduled 
 * to run at a fixed rate every so often.  All tasks can be cancelled, as well. <br>
 * Submitting a task returns a Future which can be used to cancel the task, and
 * obtain a result. VoidTasks will have a null result. <br>
 * The TaskEngine is a ShutdownService, and so its hook will stop execution of tasks.
 */
public class TaskEngine implements ShutdownService {
	
	/** Number of threads the engine will have available for running tasks */
	public static final int THREAD_POOL_SIZE = 4;
	
	/** Singleton instance */
	private static TaskEngine instance;
	
	/** The underlying object that will handle running tasks */
	private ScheduledExecutorService taskExecutor;
	
	/** The hook to stop the task engine */
	private ShutdownHook shutdownHook;
	
	/** Private constructor. Creates the executor and hook */
	private TaskEngine()
	{
		
		ThreadFactory f = new ThreadFactory()
		{

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread t, Throwable e) {
						// TODO Auto-generated method stub
						System.err.println("uncaught exception" + e);
					}
					
				});
				return t;
			}
			
		};
		
		
		
		taskExecutor = Executors.newScheduledThreadPool(THREAD_POOL_SIZE, f);
		
		// Create the hook now - the pool starts when this instance is created
		shutdownHook = new TaskEngineShutdownHook(taskExecutor);
	}
	
	
	/**
	 * Obtain a reference to the TaskEngine. This reference can be used to 
	 * submit tasks.
	 * @return The singleton TaskEngine instance
	 */
	public static TaskEngine getInstance()
	{
		if(instance == null)
		{
			instance = new TaskEngine();
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
		return task.submitImmediate(taskExecutor::schedule);
	}
	
	/**
	 * Submit a VoidTask to run after waiting some amount of time
	 * @param task The task containing the code to run on the TaskEngine
	 * @param delay The amount of time to wait before executing the task
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> submitDelayed(VoidTask task, long delay)
	{
		return task.submitDelayed(taskExecutor::schedule, delay);
	}
	
	/**
	 * Submit a VoidTask to run as soon as possible, and run it repeatedly
	 * @param task The task containing the code to run on the TaskEngine
	 * @param period The amount of time between each execution
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> scheduleImmediate(VoidTask task, long period)
	{
		return task.scheduleImmediate(taskExecutor::scheduleAtFixedRate, period);
	}
	
	/**
	 * Submit a VoidTask to run after waiting some amount of time, and run it repeatedly
	 * @param task The task containing the code to run on the TaskEngine
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @return A Future to determine when the task completes. A VoidTask will return null from the Future.
	 */
	public Future<?> scheduleDelayed(VoidTask task, long delay, long period)
	{
		return task.scheduleDelayed(taskExecutor::scheduleAtFixedRate, delay, period);
	}
	
	
	/**
	 * Submit a Task to run as soon as possible.
	 * @param task The task containing the code to run on the TaskEngine
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public <V> Future<V> submitImmediate(Task<V> task)
	{
		return task.submitImmediate(taskExecutor::schedule);
	}
	
	/**
	 * Submit a Task to run after waiting some amount of time
	 * @param task The task containing the code to run on the TaskEngine
	 * @param delay The amount of time to wait before executing the task
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public <V> Future<V> submitDelayed(Task<V> task, long delay)
	{
		return task.submitDelayed(taskExecutor::schedule, delay);
	}
	
	/**
	 * Submit a Task to run as soon as possible, and run it repeatedly
	 * @param task The task containing the code to run on the TaskEngine
	 * @param period The amount of time between each execution
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public Future<?> scheduleImmediate(Task<?> task, long period)
	{
		return task.scheduleImmediate(taskExecutor::scheduleAtFixedRate, period);
	}
	
	/**
	 * Submit a Task to run after waiting some amount of time, and run it repeatedly
	 * @param task The task containing the code to run on the TaskEngine
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @return A Future to determine when the task completes. Contains a value returned from the task
	 */
	public Future<?> scheduleDelayed(Task<?> task, long delay, long period)
	{
		return task.scheduleDelayed(taskExecutor::scheduleAtFixedRate, delay, period);
	}

	/**
	 * Obtain the ShutdownHook for the TaskEngine. 
	 * The hook is created when the object is created, and so NPE shouldn't be an issue. 
	 * Shutting down the engine prevents tasks from being submitted, and will attempt 
	 * to allow all currently running tasks to complete.
	 */
	@Override
	public ShutdownHook getShutdownHook() throws NullPointerException {
		return shutdownHook;
	}

}
