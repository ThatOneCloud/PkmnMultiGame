package net.cloud.mmo.event.task.voidtasks;

/**
 * A Task which will only execute a certain number of times (at least once)
 */
public abstract class CountedVoidTask extends CancellableVoidTask {
	
	/** The [constant] number of times the task will execute */
	private final int EXECUTION_LIMIT;
	
	/** Count of how many times this task has executed, so far */
	private int executionCount;
	
	/**
	 * Subclasses should call this constructor.
	 * @param executionCount The number of times this task will execute (it will execute at least once)
	 */
	public CountedVoidTask(int executionCount)
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
	public final void execute()
	{
		// Delegate the job to countedExecute
		countedExecute();
		
		// Increment the count (starting from 0)
		executionCount++;
		
		// See if we've run the designated number of times
		if(executionCount >= EXECUTION_LIMIT)
		{
			// and if so, stop executing.
			cancel();
		}
	}
	
	/**
	 * Rather than use <code>execute()</code>, a CountedTask relies on <code>countedExecute()</code> 
	 * to contain the custom code for the task to execute. 
	 */
	public abstract void countedExecute();

}
