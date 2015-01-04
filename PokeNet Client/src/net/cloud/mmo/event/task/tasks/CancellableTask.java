package net.cloud.mmo.event.task.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.mmo.util.function.QuadFunction;
import net.cloud.mmo.util.function.TriFunction;

/**
 * A Task which can be cancelled (via the task itself). 
 * Any task can be cancelled using the Future returned from scheduling it. 
 * However, this class includes a <code>cancel()</code> method which can be 
 * called from within the <code>execute()</code> method of the task itself.
 * 
 * @param <V> The type of the task's result
 */
public abstract class CancellableTask<V> implements Task<V> {
	
	/** The future from scheduling the task */
	protected Future<V> ourFuture;
	
	/**
	 * Called when this Task is to be submitted immediately. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> submitImmediate(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func)
	{
		return applyTri(func, this::execute, 0L);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> submitDelayed(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func, long delay)
	{
		return applyTri(func, this::execute, delay);
	}
	
	/**
	 * Apply the TriFunction given in the submit methods.  This carries out the side-effects 
	 * particular to the CancellableTask
	 * @param func The function which will be applied
	 * @param executeMethod The method to call to execute the task
	 * @param delay The amount of time between submit the task and running it the first time
	 * @return A Future resulting from the scheduling of the task
	 */
	public Future<V> applyTri(
			TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func,
			Callable<V> executeMethod,
			long delay)
	{
		ourFuture = func.apply(executeMethod, delay, TimeUnit.MILLISECONDS);
		return ourFuture;
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task.
	 * @param period The amount of time between executions of this task 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long period)
	{
		return applyQuad(func, this::execute, 0L, period);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long delay, long period)
	{
		return applyQuad(func, this::execute, delay, period);
	}

	/**
	 * Apply the QuadFunction given in the submit methods.  This carries out the side-effects 
	 * particular to the CancellableTask
	 * @param func Function to schedule the task. 
	 * @param executeMethod The method to call to execute the task
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task
	 * @return A Future resulting from the scheduling of the task
	 */
	public Future<V> applyQuad(
			QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func,
			Runnable executeMethod,
			long delay,
			long period)
	{
		ourFuture = func.apply(executeMethod, delay, period, TimeUnit.MILLISECONDS);
		return ourFuture;
	}
	
	/**
	 * Cancel the task. The task will either not be executed or will not execute again.
	 */
	public void cancel()
	{
		ourFuture.cancel(false);
	}
	


}
