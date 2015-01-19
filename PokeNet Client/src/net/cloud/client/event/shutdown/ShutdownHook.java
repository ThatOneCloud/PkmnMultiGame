package net.cloud.client.event.shutdown;

import java.io.PrintWriter;

/**
 * An interface defining a single method - shutdown().  Meant to encapsulate 
 * the code for gracefully stopping some service.
 */
public interface ShutdownHook {
	
	/**
	 * Called to tell this object that it's time to shut down the service it represents.
	 * @param out A PrintWriter to which status information will be output
	 * @throws ShutdownException If the shutdown process encountered an exception
	 */
	public void shutdown(PrintWriter out) throws ShutdownException;

}
