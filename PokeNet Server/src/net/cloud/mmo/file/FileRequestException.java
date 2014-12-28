package net.cloud.mmo.file;

/**
 * A one-stop catch-all exception for anything that happens behind the File Server 
 * module.  Like other subsystems, this serves as a convenient exception 
 * so that a myriad of exception types don't need to be dealt with.
 */
public class FileRequestException extends Exception {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 1800825549498243161L;
	
	/**
	 * Create a new exception with the given message and no cause
	 * @param message The message as to why the exception happened
	 */
	public FileRequestException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new exception with the given message and cause
	 * @param message The message as to why the exception happened
	 * @param cause The reason the exception happened
	 */
	public FileRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * @return The name of the exception class followed by the message
	 */
	@Override
	public String toString()
	{
		return "[FileRequestException]: " + getMessage();
	}

}
