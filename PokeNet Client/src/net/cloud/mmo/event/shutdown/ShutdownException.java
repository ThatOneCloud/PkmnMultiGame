package net.cloud.mmo.event.shutdown;

/**
 * An exception thrown as the result of trying to shutdown some 
 * service via a ShutdownHook.  The shutdown action may otherwise 
 * result in multiple types of exceptions - this funnels them down to one.
 * It should be constructed by passing in the causing exception, 
 * so the root cause may still be examined. 
 */
public class ShutdownException extends Exception {

	/** Eclipse generated Serialization ID */
	private static final long serialVersionUID = 9043887060442481592L;

	/**
	 * Create a ShutdownException, chained with its cause. 
	 * A ShutdownException is created when a ShutdownHook encounters 
	 * an exception.
	 * @param message Brief description of the root cause
	 * @param cause The exception that the ShutdownHook encountered
	 */
	public ShutdownException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Get a specially formatted message. Shows the reason a ShutdownException 
	 * was thrown, as well as the cause that resulted in the exception.
	 */
	public String getMessage()
	{
		return this.toString() + "\n   Caused by " + super.getCause().toString();
	}
	
	/**
	 * Formatted very similar to typical exception, slightly different. 
	 * Class name is in square brackets. <br> Ex: [ShutdownException]: cause
	 * @return A string with the name of the exception followed by the message
	 */
	public String toString()
	{
		return "[ShutdownException]: " + super.getMessage();
	}

}
