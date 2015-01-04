package net.cloud.mmo.event.task.tasks;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.mmo.util.function.QuadFunction;
import net.cloud.mmo.util.function.TriFunction;

/**
 * A Task is a piece of code that will be run to perform some job. 
 * A Task has a return value, and scheduling one returns a Future 
 * which can be used to obtain the results of the Task.
 * 
 * @param <V> The return type of the Task's result
 */
@FunctionalInterface
public interface Task<V> {
	
	/**
	 * Execute whatever code is necessary to complete the job
	 * @return The result of the execution
	 */
	public V execute() throws RuntimeException;
	
	/**
	 * Called when this Task is to be submitted immediately
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> submitImmediate(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func)
	{
		return applyTri(func, this::execute, 0L);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time to wait before executing the task
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> submitDelayed(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func, long delay)
	{
		return applyTri(func, this::execute, delay);
	}
	
	/**
	 * Apply the TriFunction given in the submit methods.
	 * @param func The function which will be applied
	 * @param executeMethod The method to call to execute the task
	 * @param delay The amount of time between submit the task and running it the first time
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> applyTri(
			TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func,
			Callable<V> executeMethod,
			long delay) {
		return func.apply(executeMethod, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically
	 * @param func Function to schedule the task. 
	 * @param period The amount of time between each execution
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long period)
	{
		return applyQuad(func, this::execute, 0L, period);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long delay, long period)
	{
		return applyQuad(func, this::execute, delay, period);
	}
	
	/**
	 * Apply the QuadFunction given in the submit methods.
	 * @param func Function to schedule the task. 
	 * @param executeMethod The method to call to execute the task
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<V> applyQuad(
			QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func,
			Runnable executeMethod,
			long delay,
			long period)
	{
		return func.apply(executeMethod, delay, period, TimeUnit.MILLISECONDS);
	}

}
