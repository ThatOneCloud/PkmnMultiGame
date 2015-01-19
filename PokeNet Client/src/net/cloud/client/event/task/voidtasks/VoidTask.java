package net.cloud.client.event.task.voidtasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.client.util.function.QuadFunction;
import net.cloud.client.util.function.TriFunction;

/**
 * A VoidTask is some task which has no return value (void type) 
 * This is different from Task so that "return null" does not have to be 
 * included at the end of every execute method - easier lambda usage.<br>
 * Also contains default methods which take in the method used to schedule the task. 
 * These can be overridden for special scheduling behavior.
 */
@FunctionalInterface
public interface VoidTask {
	
	/**
	 * Execute the task. Take some action, with no return value.
	 */
	public void execute();
	
	/**
	 * Called when this Task is to be submitted immediately
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> submitImmediate(TriFunction<Runnable, Long, TimeUnit, Future<?>> func)
	{
		return applyTri(func, this::execute, 0L);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time to wait before executing the task
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> submitDelayed(TriFunction<Runnable, Long, TimeUnit, Future<?>> func, long delay)
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
	public default Future<?> applyTri(
			TriFunction<Runnable, Long, TimeUnit, Future<?>> func,
			Runnable executeMethod,
			long delay) {
		return func.apply(executeMethod, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically
	 * @param func Function to schedule the task. 
	 * @param period The amount of time between each execution
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long period)
	{
		return func.apply(this::execute, 0L, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically
	 * @param func Function to schedule the task. 
	 * @param delay The amount of time to wait before executing the task
	 * @param period The amount of time between each execution
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long delay, long period)
	{
		return func.apply(this::execute, delay, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Apply the QuadFunction given in the submit methods.
	 * @param func Function to schedule the task. 
	 * @param executeMethod The method to call to execute the task
	 * @param delay The amount of time between submit the task and running it the first time
	 * @param period The amount of time between executions of this task
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> applyQuad(
			QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func,
			Runnable executeMethod,
			long delay,
			long period)
	{
		return func.apply(executeMethod, delay, period, TimeUnit.MILLISECONDS);
	}
	
}
