package net.cloud.client.event.task.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.client.event.task.TaskException;
import net.cloud.client.logging.Logger;
import net.cloud.client.util.function.QuadFunction;
import net.cloud.client.util.function.TriFunction;

/**
 * A Task which will catch any errors from its execution and log them. This is useful 
 * since the TaskEngine will not otherwise simply report errors. They can be obtained 
 * by checking the Future returned from scheduling. When checking is not an option or 
 * not desirable, this class will instead leave some trace of the error.<br>
 * This task is a wrapper around a Task. 
 * To create and use it, first create a VoidTask and then create an ErrorLoggingTask 
 * around it. Then submit the ErrorLoggingVoidTask to the TaskEngine.
 */
public class ErrorLoggingTask<V> implements Task<V> {
	
	/** The task we're decorating */
	private Task<V> task;
	
	/**
	 * Create a new ErrorLoggingTask by wrapping around an existing task. 
	 * This task will catch any errors from the task's execution and log them
	 * @param task The Task to decorate with logging behavior
	 */
	public ErrorLoggingTask(Task<V> task)
	{
		this.task = task;
	}
	
	/**
	 * Called when this Task is to be submitted immediately.
	 * Delegates to the wrapped task. See the corresponding method in {@link Task}
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> submitImmediate(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func)
	{
		return task.applyTri(func, this::execute, 0L);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay.
	 * Delegates to the wrapped task. See the corresponding method in {@link Task}
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> submitDelayed(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func, long delay)
	{
		return task.applyTri(func, this::execute, delay);
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically. 
	 * Delegates to the wrapped task. See the corresponding method in {@link Task}
	 * @param func Function to schedule the task. 
	 * @param period The amount of time between executions of this task 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long period)
	{
		return task.applyQuad(func, this::execute, 0L, period);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically. 
	 * Delegates to the wrapped task. See the corresponding method in {@link Task}
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long delay, long period)
	{
		return task.applyQuad(func, this::execute, delay, period);
	}
	
	/**
	 * The execution is delegated to the wrapped task.
	 * If the underlying task's execution causes any kind of error, 
	 * this will log the issue and re-throw the cause.
	 */
	@Override
	public final V execute()
	{
		// Delegate to the underlying task
		try {
			
			V result = task.execute();
			
			return result;
			
		// Catch issues in this order - report them slightly differently
		// Re-throw so the worker thread can deal with it appropriately as well
		} catch(TaskException taskExc) {
			Logger.instance().logException("Task execution resulting in an exception", taskExc);
			throw taskExc;
		} catch(RuntimeException runExc) {
			Logger.instance().logException("Task execution resulting in an exception", runExc);
			throw runExc;
		} catch(Exception exc) {
			Logger.instance().logException("Task execution resulting in an exception", exc);
			throw exc;
		}
	}

}
