package net.cloud.mmo.event.task.voidtasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.mmo.util.function.QuadFunction;
import net.cloud.mmo.util.function.TriFunction;

/**
 * A Task which will only execute a certain number of times (at least once). <br>
 * This task is a wrapper around a CancellableVoidTask. 
 * To create and use it, first create a CancellableVoidTask and then create a CountedVoidTask 
 * around it. Then submit the CountedVoidTask to the TaskEngine.
 */
public class CountedVoidTask implements VoidTask {
	
	/** The [constant] number of times the task will execute */
	private final int EXECUTION_LIMIT;
	
	/** Count of how many times this task has executed, so far */
	private int executionCount;
	
	/** The task we're decorating */
	private CancellableVoidTask task;
	
	/**
	 * Create a new CountedVoidTask by wrapping around an existing task. 
	 * This task will only execute a given number of times before stopping itself.
	 * @param task The CancellableVoidTask to decorate with counting behavior
	 * @param executionCount The number of times this task will execute (it will execute at least once)
	 */
	public CountedVoidTask(CancellableVoidTask task, int executionCount)
	{
		this.EXECUTION_LIMIT = executionCount;
		this.executionCount = 0;
		this.task = task;
	}
	
	/**
	 * Called when this Task is to be submitted immediately.
	 * Delegates to the wrapped task. See the corresponding method in {@link CancellableVoidTask}
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
	 * Delegates to the wrapped task. See the corresponding method in {@link CancellableVoidTask}
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
	 * Delegates to the wrapped task. See the corresponding method in {@link CancellableVoidTask}
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
	 * Delegates to the wrapped task. See the corresponding method in {@link CancellableVoidTask}
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
	 * Executes the task. This method is final, and takes care of canceling the 
	 * task after a certain number of executions. The execution is delegated to the 
	 * wrapped task.
	 */
	@Override
	public final void execute()
	{
		// Delegate to the wrapped task
		task.execute();
		
		// Increment the count (starting from 0)
		executionCount++;
		
		// See if we've run the designated number of times
		if(executionCount >= EXECUTION_LIMIT)
		{
			// and if so, stop executing (via the wrapped cancellable task)
			task.cancel();
		}
	}

}
