package net.cloud.server.event.task;

/**
 * A TaskException is meant to be a singular exception that a Task can throw. 
 * A TaskException extends RuntimeException, and so it is unchecked. A Task's 
 * <code>execute()</code> method does not declare any checked exceptions, so a 
 * Task should catch checked exceptions and re-throw them wrapped in an unchecked 
 * TaskException.<br>
 * A TaskException cannot be created alone - it must be wrapped around a cause exception.<br>
 * A Task can be checked for exceptions by getting the value from its Future or by wrapping the 
 * Task in an ErrorLoggingTask.
 */
public class TaskException extends RuntimeException {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 426039742583675114L;
	
	/**
	 * The only constructor. This exception will not have its own message, rather 
	 * it simply wraps a cause caught from the execution of a task.
	 * @param cause Cause of the exception
	 */
	public TaskException(Throwable cause)
	{
		super(cause);
	}
	
	/**
	 * @return A message string indicating a TaskException, and the message from its cause
	 */
	public String getMessage()
	{
		return "[TaskException] Cause Message: " + super.getCause().getMessage();
	}
	
	/**
	 * Returns this exception's message. See {@link TaskException#getMessage}
	 */
	@Override
	public String toString()
	{
		return getMessage();
	}

}
