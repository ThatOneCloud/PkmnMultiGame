package net.cloud.mmo.event.shutdown;

/**
 * An interface defining a single method - shutdown().  Meant to encapsulate 
 * the code for gracefully stopping some service.
 */
public interface ShutdownHook {
	
	/**
	 * Called to tell this object that it's time to shut down the service it represents.
	 * @throws ShutdownException If the shutdown process encountered an exception
	 */
	public void shutdown() throws ShutdownException;

}
