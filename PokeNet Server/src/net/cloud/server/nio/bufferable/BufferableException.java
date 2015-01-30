package net.cloud.server.nio.bufferable;

/**
 * An exception for Bufferable methods to throw. This is in part so that exception 
 * hygiene is somewhat enforced, and also so that the exception can report the position 
 * in the buffer that caused an issue in the first place. 
 */
public class BufferableException extends Exception {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 6688919578794529364L;
	
	/** Position in the buffer where the exception occurred */
	private int position;
	
	/**
	 * Create a new exception with a cause message and position, but no chained exception 
	 * @param message A message detailing what went wrong
	 * @param position The position in the buffer that caused an issue
	 */
	public BufferableException(String message, int position)
	{
		super(message);
		
		this.position = position;
	}
	
	/**
	 * Create a new exception with a cause message and position, and a chained exception 
	 * @param message A message detailing what went wrong
	 * @param cause The exception that has caused this one to be created
	 * @param position The position in the buffer that caused an issue
	 */
	public BufferableException(String message, Throwable cause, int position)
	{
		super(message, cause);
		
		this.position = position;
	}
	
	/**
	 * The returned string has the name of the exception, cause message, and causing position concatenated
	 */
	@Override
	public String toString()
	{
		return super.getMessage() + "...  Exception attributed to position " + position + " in buffer.";
	}

}
