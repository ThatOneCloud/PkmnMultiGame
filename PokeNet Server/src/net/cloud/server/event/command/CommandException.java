package net.cloud.server.event.command;

/**
 * An Exception used by the Command system. 
 * In particular, the message is intended to be descriptive 
 * and viable for the client to see. 
 */
public class CommandException extends Exception {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 2886298910736314380L;
	
	/**
	 * Create a new CommandException. with the given message. 
	 * The message is the one that'll be returned by getMessage()
	 * @param message A meaningful message - so a client can read it
	 */
	public CommandException(String message)
	{
		super(message);
	}
	
	/**
	 * Create a new CommandException, with a message and chained cause
	 * @param message A meaningful message - so a client can read it
	 * @param cause The Throwable that caused the CommandException. For chaining.
	 */
	public CommandException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
}
