package net.cloud.client.util;

/**
 * Custom exception for the StrongIterator to throw. 
 * Exists to enforce checking for exceptions while iterating lists 
 * that may not per say be entirely synchronized. 
 */
public class IteratorException extends Exception {

	/** Eclipse generated serial ID */
	private static final long serialVersionUID = 5117589268904955808L;
	
	/** 
	 * Constructor that accepts a cause and no message. Since this exception 
	 * is really only going to be thrown as the result of another. 
	 * @param cause The initial exception thrown during iteration
	 */
	public IteratorException(Throwable cause)
	{
		super(cause);
	}

}
