package net.cloud.gfx.focus;

/**
 * Defines an object which can hold focus in the system. A focusable object can be linked to other 
 * focusable objects to form a chain of sorts. This way, focus can be transferred from one to the next. 
 * A focusable object supports checking for whether or not it has focus, and for dealing with typed 
 * key events. (Since key events are delivered to an object with focus)
 * @see FocusHandler
 * @see FocusController
 */
public interface Focusable {
	
	/**
	 * Obtain the FocusHandler underlying this Focusable object. The handler will take care of tracking focus 
	 * and and chain of focusable objects for changing focus. This should never be null. That would not be cool. 
	 * @return The FocusHandler underlying this Focusable object
	 */
	public FocusHandler getFocusHandler();
	
	/**
	 * Tell this focusable object that it has received a key event. This is defined here since an 
	 * object with focus is the object that key events are sent to. However, nothing needs to be done 
	 * with this event from here. It can simply be discarded if desired. 
	 * @param key The character which was typed
	 */
	public void keyTyped(char key);
	
	/**
	 * Tell this object what the next focusable object in line should be. This will connect them, so this object 
	 * knows the next object and the given focusable knows this one is its previous.  This is the method that 
	 * should be used when constructing focus chains. <br>
	 * Default behavior is <code>FocusController.link(this, next);</code>
	 * @param next The next Focusable in line
	 */
	public default void linkNextFocusable(Focusable next)
	{
		FocusController.link(this, next);
	}
	
	/**
	 * Tell the handler what the previous Focusable in line should be. This will connect them, so this Focusable 
	 * knows the previous Focusable and the given Focusable knows this one is its next.  This is the method that 
	 * should be used when constructing focus chains. <br>
	 * Default behavior is <code>FocusController.link(previous, this);</code>
	 * @param previous The previous Focusable in line
	 */
	public default void linkPreviousFocusable(Focusable previous)
	{
		FocusController.link(previous, this);
	}
	
	/**
	 * Tell this Focusable that it should no longer be linked in a focus chain. If there is a next and/or previous 
	 * Focusable, they will then be joined to each other and this Focusable will no longer be within a focus chain. <br>
	 * Default behavior is <code>FocusController.unlink(getFocusHandler().getPrevious(), this, getFocusHandler().getNext());</code>
	 */
	public default void unlink()
	{
		FocusController.unlink(getFocusHandler().getPrevious(), this, getFocusHandler().getNext());
	}

}
