package net.cloud.mmo.event.task.voidtasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.mmo.util.function.QuadFunction;
import net.cloud.mmo.util.function.TriFunction;

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
		return func.apply(this::execute, 0L, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> submitDelayed(TriFunction<Runnable, Long, TimeUnit, Future<?>> func, long delay)
	{
		return func.apply(this::execute, delay, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long period)
	{
		return func.apply(this::execute, 0L, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	public default Future<?> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long delay, long period)
	{
		return func.apply(this::execute, delay, period, TimeUnit.MILLISECONDS);
	}
	
}
