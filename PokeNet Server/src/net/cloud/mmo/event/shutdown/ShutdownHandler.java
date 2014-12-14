package net.cloud.mmo.event.shutdown;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A class which contains ShutdownHooks. Hooks can be added, and this handler 
 * can then later be used to execute the shutdown code contained in each of the hooks.
 */
public class ShutdownHandler {
	
	/** List of all the hooks this handler is responsible for executing */
	private Queue<ShutdownHook> hooks = new LinkedList<>();

	/** Create a ShutdownHandler with an initially empty list of ShutdownHooks */
	public ShutdownHandler() {
	}
	
	/**
	 * Add a ShutdownHook to the list this handler is taking care of. 
	 * Hooks should probably not be added to more than one handler, 
	 * although multiple handlers can be instantiated.
	 * @param hook The ShutdownHook this handler will later execute
	 */
	public void addHook(ShutdownHook hook)
	{
		hooks.add(hook);
	}
	
	/**
	 * Not an ideal operation, but possible. Remove a ShutdownHook from the handler.
	 * @param hook The ShutdownHook to remove. Must be the same object instance.
	 * @return True if the hook was removed
	 */
	public boolean removeHook(ShutdownHook hook)
	{
		return hooks.remove(hook);
	}
	
	/**
	 * Run the shutdown procedure for all of the hooks in this handler. 
	 * Do not count on the execution occurring in any particular order, 
	 * instead services should not fail when another service is already down.
	 * If a problem is encountered, the method will not continue. The service that 
	 * failed to shutdown will still be in the handler - along with all services not yet handled.
	 * @throws ShutdownException If the process could not complete
	 */
	public void shutdownAll() throws ShutdownException
	{
		// Pop from the head of the queue (FIFO order)
		while(!hooks.isEmpty())
		{
			// Peek to begin with, don't remove just yet...
			ShutdownHook hook = hooks.peek();
			
			// Let the hook try to shutdown
			try {
				hook.shutdown();
				
				// Nothing went wrong. Remove it from the queue
				hooks.remove();
			} catch (ShutdownException e) {
				// Uh oh. Something went wrong.. stop here. Chain exceptions.
				throw new ShutdownException("ShutdownHandler could not complete shutdown process. Ceasing shutdown.", e);
			}
		}
	}

}
