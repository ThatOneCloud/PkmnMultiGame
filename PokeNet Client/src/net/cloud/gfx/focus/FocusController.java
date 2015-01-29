package net.cloud.gfx.focus;

/**
 * Deals with FocusHandlers and maintaining who has focus. Also has methods useful for 
 * creating links between handlers and other such utility functions. 
 * Singleton - even if there were disjoint systems, it'd be a tad clunky to allow more than 
 * one thing focus. And so much simpler to utilize this if it's singleton. 
 */
public class FocusController {
	
	/** Singleton instance */
	private static FocusController instance;
	
	/** The Focusable which currently has focus. Null is acceptable and means there is none. */
	private Focusable currentFocus;
	
	/**
	 * Create a new FocusController. There will be no handler which has focus to begin with. 
	 */
	private FocusController()
	{
		currentFocus = null;
	}
	
	/**
	 * Obtain the single instance of the FocusController, so that focus stuff can be dealt with. 
	 * @return The FocusController instance
	 */
	public static FocusController instance()
	{
		if(instance == null)
		{
			synchronized(FocusController.class)
			{
				if(instance == null)
				{
					instance = new FocusController();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Link two focusable objects, so that they are connected in a chain. Neither value may be null. 
	 * The first object will have its next handler become the second handler. 
	 * The second object will have its previous handler become the first handler.
	 * @param first The first object in the chain-to-be
	 * @param second The second object in the chain-to-be - comes after the first. 
	 */
	public static void link(Focusable first, Focusable second)
	{
		first.getFocusHandler().setNext(second);
		second.getFocusHandler().setPrevious(first);
	}
	
	/**
	 * Unlink a focusable object from its chain. This means it will no longer be connected to any previous or next Focusable, 
	 * and instead those objects will be linked together if possible - to maintain the focus chain. 
	 * @param prev The previous object. Null permissible.
	 * @param middle The object to unlink. Null not okay.
	 * @param next The next object. Null permissible.
	 */
	public static void unlink(Focusable prev, Focusable middle, Focusable next)
	{
		// Action depends on how linked the handler is
		if(prev == null && next == null)
		{
			// It's pretty much already unlinked. 
		} 
		else if(prev == null) {
			// Has a forward handler which no longer needs to go back to middle
			next.getFocusHandler().setPrevious(null);
			middle.getFocusHandler().setNext(null);
		}
		else if(next == null) {
			// Has a previous handler which no longer needs to link forward
			prev.getFocusHandler().setNext(null);
			middle.getFocusHandler().setPrevious(null);
		}
		else {
			// Connected both ways. Remove the middle, join the sides
			middle.getFocusHandler().setPrevious(null);
			middle.getFocusHandler().setNext(null);
			
			prev.getFocusHandler().setNext(next);
			next.getFocusHandler().setPrevious(prev);
		}
	}
	
	/**
	 * Register that the given Focusable object now has focus. If there is already an object that has focus, 
	 * it will lose focus. 
	 * @param newFocus The object which is gaining focus
	 */
	public void register(Focusable newFocus)
	{
		// Tell the current handler that it's lost focus (it's sure about to)
		if(currentFocus != null)
		{
			currentFocus.getFocusHandler().focusLost();
		}
		
		// Switch over the objects
		currentFocus = newFocus;
		
		// Tell the new handler it's got focus now
		currentFocus.getFocusHandler().focusGained();
	}
	
	/**
	 * Obtain the Focusable object that currently has focus in the system. 
	 * This will be null if there is no such object at the moment.
	 * @return The object that currently holds focus
	 */
	public Focusable currentFocusable()
	{
		return currentFocus;
	}

}
