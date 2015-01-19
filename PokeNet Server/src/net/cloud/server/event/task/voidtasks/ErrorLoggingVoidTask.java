package net.cloud.server.event.task.voidtasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.server.event.task.TaskException;
import net.cloud.server.logging.Logger;
import net.cloud.server.util.function.QuadFunction;
import net.cloud.server.util.function.TriFunction;

/**
 * A Task which will catch any errors from its execution and log them. This is useful 
 * since the TaskEngine will not otherwise simply report errors. They can be obtained 
 * by checking the Future returned from scheduling. When checking is not an option or 
 * not desirable, this class will instead leave some trace of the error.<br>
 * This task is a wrapper around a VoidTask. 
 * To create and use it, first create a VoidTask and then create an ErrorLoggingVoidTask 
 * around it. Then submit the ErrorLoggingVoidTask to the TaskEngine.
 */
public class ErrorLoggingVoidTask implements VoidTask {
	
	/** The task we're decorating */
	private VoidTask task;
	
	/**
	 * Create a new ErrorLoggingVoidTask by wrapping around an existing task. 
	 * This task will catch any errors from the task's execution and log them
	 * @param task The VoidTask to decorate with logging behavior
	 */
	public ErrorLoggingVoidTask(VoidTask task)
	{
		this.task = task;
	}
	
	/**
	 * Called when this Task is to be submitted immediately.
	 * Delegates to the wrapped task. See the corresponding method in {@link VoidTask}
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<?> submitImmediate(TriFunction<Runnable, Long, TimeUnit, Future<?>> func)
	{
		return task.applyTri(func, this::execute, 0L);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay.
	 * Delegates to the wrapped task. See the corresponding method in {@link VoidTask}
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<?> submitDelayed(TriFunction<Runnable, Long, TimeUnit, Future<?>> func, long delay)
	{
		return task.applyTri(func, this::execute, delay);
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically. 
	 * Delegates to the wrapped task. See the corresponding method in {@link VoidTask}
	 * @param func Function to schedule the task. 
	 * @param period The amount of time between executions of this task 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<?> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long period)
	{
		return task.applyQuad(func, this::execute, 0L, period);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically. 
	 * Delegates to the wrapped task. See the corresponding method in {@link VoidTask}
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<?> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long delay, long period)
	{
		return task.applyQuad(func, this::execute, delay, period);
	}
	
	/**
	 * The execution is delegated to the wrapped task.
	 * If the underlying task's execution causes any kind of error, 
	 * this will log the issue and re-throw the cause.
	 */
	@Override
	public final void execute()
	{
		// Delegate to the underlying task
		try {
			
			task.execute();
			
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
