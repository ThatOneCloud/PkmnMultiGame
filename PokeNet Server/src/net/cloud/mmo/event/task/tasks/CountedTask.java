package net.cloud.mmo.event.task.tasks;

/**
 * A Task which will only execute a certain number of times (at least once)
 *
 * @param <V> The type of the task's result
 */
public abstract class CountedTask<V> extends CancellableTask<V> {
	
	/** The [constant] number of times the task will execute */
	private final int EXECUTION_LIMIT;

	/** Count of how many times this task has executed, so far */
	private int executionCount;

	/**
	 * Subclasses should call this constructor.
	 * @param executionCount The number of times this task will execute (it will execute at least once)
	 */
	public CountedTask(int executionCount)
	{
		this.EXECUTION_LIMIT = executionCount;
		this.executionCount = 0;
	}

	/**
	 * Executes the task. This method is final, and takes care of canceling the 
	 * task after a certain number of executions. Instead, a new abstract method is introduced - 
	 * <code>countedExecute()</code> - to contain the task's job code.
	 */
	@Override
	public final V execute()
	{
		// Delegate the job to countedExecute and get the result from there
		V result = countedExecute();

		// Increment the count (starting from 0)
		executionCount++;

		// See if we've run the designated number of times
		if(executionCount >= EXECUTION_LIMIT)
		{
			// and if so, stop executing.
			cancel();
		}
		
		return result;
	}

	/**
	 * Rather than use <code>execute()</code>, a CountedTask relies on <code>countedExecute()</code> 
	 * to contain the custom code for the task to execute. 
	 * @return The result of the task's execution
	 */
	public abstract V countedExecute();

}
