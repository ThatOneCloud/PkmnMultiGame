package net.cloud.client.event.shutdown;

/**
 * Defines some service which is most likey started at some point, 
 * and can later be shut down via a ShutdownHook.
 */
public interface ShutdownService {
	
	/**
	 * Get the ShutdownHook associated with this service. 
	 * There is no guarantee the hook has been created. There is also no absolute guarantee 
	 * as to the behavior when that is the case. However, a NullPointerException 
	 * would be great to throw. ('cause the hook is null, but let's avoid null checks)
	 * @return The ShutdownHook responsible for this service
	 * @throws NullPointerException If the hook is not available
	 */
	public ShutdownHook getShutdownHook() throws NullPointerException;

}
