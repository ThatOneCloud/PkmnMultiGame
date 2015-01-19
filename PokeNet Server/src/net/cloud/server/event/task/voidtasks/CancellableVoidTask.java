package net.cloud.server.event.task.voidtasks;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.cloud.server.util.function.QuadFunction;
import net.cloud.server.util.function.TriFunction;

/**
 * A VoidTask which can be cancelled (via the task itself). 
 * Any task can be cancelled using the Future returned from scheduling it. 
 * However, this class includes a <code>cancel()</code> method which can be 
 * called from within the <code>execute()</code> method of the task itself.
 */
public abstract class CancellableVoidTask implements VoidTask {
	
	/** The future from scheduling the task */
	protected Future<?> ourFuture;
	
	/**
	 * Called when this Task is to be submitted immediately. Keeps a reference to the Future 
	 * resulting from scheduling the task - so that the task may cancel itself.
	 * @param func Function to schedule the task. 
	 * @return A Future resulting from the scheduling of the task
	 */
	@Override
	public Future<?> submitImmediate(TriFunction<Runnable, Long, TimeUnit, Future<?>> func)
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
	public Future<?> submitDelayed(TriFunction<Runnable, Long, TimeUnit, Future<?>> func, long delay)
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
	@Override
	public Future<?> applyTri(
			TriFunction<Runnable, Long, TimeUnit, Future<?>> func,
			Runnable executeMethod,
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
	public Future<?> scheduleImmediate(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long period)
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
	public Future<?> scheduleDelayed(QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func, long delay, long period)
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
	@Override
	public Future<?> applyQuad(
			QuadFunction<Runnable, Long, Long, TimeUnit, Future<?>> func,
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
