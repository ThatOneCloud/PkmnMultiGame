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
		ourFuture = func.apply(this::execute, 0L, TimeUnit.MILLISECONDS);
		return ourFuture;
	}
	
	/**
	 * Called when this Task is to be submitted after some delay. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> submitDelayed(TriFunction<Callable<V>, Long, TimeUnit, Future<V>> func, long delay)
	{
		ourFuture = func.apply(this::execute, delay, TimeUnit.MILLISECONDS);
		return ourFuture;
	}
	
	/**
	 * Called when this Task is to be submitted immediately and run periodically. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long period)
	{
		ourFuture = func.apply(this::execute, 0L, period, TimeUnit.MILLISECONDS);
		return ourFuture;
	}
	
	/**
	 * Called when this Task is to be submitted after some delay and run periodically. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<V> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<V>> func, long delay, long period)
	{
		ourFuture = func.apply(this::execute, delay, period, TimeUnit.MILLISECONDS);
		return ourFuture;
	}


	
	// provide a method to cancel the future (but don't allow any real access to the future or knowledge that we have it)
	public void cancel()
	{
		ourFuture.cancel(false);
	}
	


}
