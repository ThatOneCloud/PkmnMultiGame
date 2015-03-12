package net.cloud.server.event.shutdown;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A class which contains ShutdownHooks. Hooks can be added, and this handler 
 * can then later be used to execute the shutdown code contained in each of the hooks. 
 * The class also supports a blocking operation - waitForShutdown. The calling thread 
 * will be blocked until another thread calls shutdownAll, at which point the blocked 
 * thread will execute the ShutdownHooks.
 */
public class ShutdownHandler {
	
	/** Correlates to what step of the shutdown process the handler can be in */
	private enum State {INIT, WAITING, NOTIFIED, IN_PROGRESS, DONE}
	
	/** List of all the hooks this handler is responsible for executing */
	private List<ShutdownHook> hooks = new LinkedList<>();
	
	/** The current stage of the shutdown process */
	private State state;

	/** Create a ShutdownHandler with an initially empty list of ShutdownHooks */
	public ShutdownHandler()
	{
		state = State.INIT;
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
	 * instead services should not fail when another service is already down.<br>
	 * If a thread is waiting for shutdown to happen, this will notify it so that thread 
	 * will be responsible for executing hooks.  Otherwise, the calling thread will execute the hooks.<br>
	 * If a problem is encountered, the method will not continue. The service that 
	 * failed to shutdown will still be in the handler - along with all services not yet handled.
	 * @param out A PrintWriter to which status information will be output
	 * @throws ShutdownException If the process could not complete
	 * @throws Exception If the handler is already done executing hooks
	 */
	public void shutdownAll(PrintWriter out) throws ShutdownException, Exception
	{
		// Doesn't make much sense to start shutting down if it's already happening or happened
		if(getState() == State.IN_PROGRESS || getState() == State.DONE)
		{
			throw new Exception("Cannot shutdown - already in progress or done.");
		}
		
		// Now check if this is being called while another thread is waiting for shutdown
		if(getState() == State.WAITING)
		{
			// There is. Notify it.
			setState(State.NOTIFIED);
			
			synchronized(this)
			{
				this.notify();
			}
		}
		// Amounts to checking if we're in INIT or NOTIFIED state
		else {
			// Regardless of whether we're at INIT or NOTIFIED, we'll actually start executing hooks now
			executeHooks(out);
		}
	}

	/**
	 * Useful for making sure the thread that created the handler is the one that executes 
	 * the hooks within it.  Blocks until some other thread calls <code>shutdownAll()</code> 
	 * at which point the blocked thread will execute the hooks.
	 * @param out A PrintWriter to which status information will be output
	 * @throws ShutdownException If there was a problem executing the hooks
	 * @throws Exception If the handler is already doing something else (not just initialized)
	 */
	public void waitForShutdown(PrintWriter out) throws ShutdownException, Exception
	{
		// We can only really start waiting if we haven't done anything else yet
		if(getState() != State.INIT)
		{
			throw new Exception("Cannot wait now - handler already in progress");
		}
		
		// In INIT state. Move to waiting state, where we'll sit until notified
		setState(State.WAITING);
		
		// And now for the sitting and waiting
		synchronized(this)
		{
			while(getState() == State.WAITING)
			{
				this.wait();
			}
		}
		
		// Done waiting - blocked thread has been notified to actually try shutting down
		shutdownAll(out);
	}
	
	/**
	 * Actually goes through and executes each ShutdownHook. The state will change to IN_PROGRESS 
	 * and later to DONE. If the process cannot complete, it will roll back to INIT and the failed 
	 * hooks will still be in the handler. An attempt is made to shutdown each hook, though. 
	 * @param out A PrintWriter to which status information will be output
	 * @throws ShutdownException If one or more ShutdownHooks encounter a problem. Message is combined 
	 * from each hook's exception - so printStackTrace may not prove useful. The message should be, however.
	 */
	private void executeHooks(PrintWriter out) throws ShutdownException {
		// Starting the process, so we're in progress
		setState(State.IN_PROGRESS);

		// Keep track of any exceptions that came up from the hooks
		List<ShutdownException> exceptions = new LinkedList<>();
		
		// Iterate through each hook
		ListIterator<ShutdownHook> hookIterator = hooks.listIterator();
		while(hookIterator.hasNext())
		{
			ShutdownHook hook = hookIterator.next();
			
			// Let the hook try to shutdown
			try {
				hook.shutdown(out);

				// Nothing went wrong. Remove it from the list (thanks to list iterator)
				hookIterator.remove();
			} catch (ShutdownException e) {
				// Hook encountered a problem. Add its exception to the list
				exceptions.add(e);
				
				// But keep going through the list (the problem hook is not removed)
				continue;
			}
		}
		
		// Check to see if any hooks had problems
		if(!exceptions.isEmpty())
		{
			// Some did. They're still in the handler, so roll back to INIT state
			setState(State.INIT);
			
			// Now any exceptions that arose get sorta crammed into one big one, I don't know
			String message = "One or more ShutdownHooks encountered exceptions: \n";
			for(ShutdownException e : exceptions)
			{
				message += e.getMessage() + "\n";
			}
			
			throw new ShutdownException(message, new Exception("ShutdownHandler encountered an issue"));
		}
		
		// All went smoothly, set state to done
		setState(State.DONE);
	}
	
	/**
	 * Should be used - do not directly access <code>state</code> field.
	 * @return The State the handler is in
	 */
	private synchronized State getState()
	{
		return this.state;
	}
	
	/**
	 * Change the state the handler is in
	 * @param newState The state the handler is now in
	 */
	private synchronized void setState(State newState)
	{
		this.state = newState;
	}
	
}
