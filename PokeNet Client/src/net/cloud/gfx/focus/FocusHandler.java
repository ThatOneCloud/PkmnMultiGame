package net.cloud.gfx.focus;

/**
 * This interface defines some object which is capable of dealing with focus. 
 * That is, methods for dealing with gaining and losing focus, as well as traversing 
 * through other focusable objects. Only one FocusHandler should have focus at once 
 * in an application. 
 * @see Focusable
 */
public interface FocusHandler {
	
	/**
	 * Called when this FocusHandler has gained focus. 
	 */
	public void focusGained();
	
	/**
	 * Called when this FocusHandler no longer has focus. (And previously did)
	 */
	public void focusLost();
	
	/**
	 * Change focus so that the next handler in line will gain focus and 
	 * this handler will lose it. 
	 */
	public void traverseNext();
	
	/**
	 * Change focus so that the previous handler in line will gain focus and 
	 * this handler will lose it. 
	 */
	public void traversePrevious();
	
	/**
	 * Check to see if this handler currently has focus
	 * @return True if this handler currently has focus
	 */
	public boolean hasFocus();
	
	/**
	 * Obtain the next Focusable in the chain. This may return null if there is not one. 
	 * @return The Focusable which is traversed after this one
	 */
	public Focusable getNext();
	
	/**
	 * Do not call to set up a link. Instead use <code>Focusable.linkNext(Focusable)</code><br>
	 * Tells this handler that the given Focusable is next in line. Does not set previous. 
	 * @param next The next Focusable in line
	 */
	public void setNext(Focusable next);
	
	/**
	 * Obtain the previous Focusable in the chain. This may return null if there is not one. 
	 * @return The Focusable which is traversed prior to this one
	 */
	public Focusable getPrevious();
	
	/**
	 * Do not call to set up a link. Instead use <code>Focusable.linkPrevious(Focusable)</code><br>
	 * Tells this handler that the given Focusable is previous in line. Does not set next. 
	 * @param previous The previous Focusable in line
	 */
	public void setPrevious(Focusable previous);

}
