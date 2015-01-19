package net.cloud.gfx.focus;

/**
 * A FocusHandler meant to be used by a Container element. It does not act too dissimilar from 
 * the SingleFocusHandler. It can have its own focus, and lose that focus as well. However, 
 * when traversing to the next handler, it will not be simply what the container is linked next to. 
 * Instead, traversing forward will move to the first handler in the container. Traversing backwards 
 * will move to the Focusable before this grouped one.  The handler has its own previous and next 
 * Focusable, but also a first and last Focusable contained within it. When a link is made, the chain 
 * is manipulated to be as if the previous and first Focusable are linked, the first and this are linked, 
 * and the last and next are linked. <br>
 * To utilize the container's focus chain, first, set the first and last children. It's okay if there are 
 * no children, it can be left undone. But setting the children must happen before linking, for correct 
 * behavior. After setting the children, establish the links to the previous and next Focusable (the ones 
 * that are before and after the entire container in the chain)
 */
public class ContainerFocusHandler implements FocusHandler {
	
	/** A flag for whether or not this handler alone has focus */
	private boolean hasFocus;
	
	/** The previous handler in line, if any. This would still refer to the overall previous, not the first child */
	private Focusable previous;
	
	/** The next handler in line, if any. This would still refer to the overall next, not the last child */
	private Focusable next;
	
	/** The child in the container which takes focus first. Not necessarily the first child. */
	private Focusable first;
	
	/** The child in the container which takes focus last. Not necessarily the last child */
	private Focusable last;
	
	/**
	 * Create a new ContainerFocusHandler. It will not start with focus. 
	 * There will be no previous, next, first, or last Focusable. These must be linked and set 
	 * externally like with SingleFocusHandler. 
	 */
	public ContainerFocusHandler() {
		this.hasFocus = false;
		
		this.previous = null;
		this.next = null;
		
		this.first = null;
		this.last = null;
	}

	/** Called when this handler has gained focus. Has no bearing on any contained handlers */
	@Override
	public void focusGained() {
		// Set the focus flag. Can be checked on demand by hasFocus()
		this.hasFocus = true;
	}

	/** Called when this handler has lost focus. Has no bearing on any contained handlers */
	@Override
	public void focusLost() {
		// Clear the focus flag
		this.hasFocus = false;
	}

	/**
	 * This handler will traverse to the first child Focusable if one is present. 
	 * Otherwise, to prevent interruptions in traversal, the Focusable which we are linked to will be used (if present). 
	 */
	@Override
	public void traverseNext() {
		// Primarily we want to move to the first child, even though we're linked to a different Focusable
		if(first != null)
		{
			FocusController.instance().register(first);
		}
		// But to prevent interruptions in traversal, if there is no such first Focusable, go to the next one
		else if(next != null)
		{
			FocusController.instance().register(next);
		}
	}

	/**
	 * This handler will traverse back to the Focusable it is linked to (the previous link) if one is present. 
	 * So unlike traverseNext() which goes to a child Focusable, this will just go to the linked Focusable. 
	 */
	@Override
	public void traversePrevious() {
		// Transfer focus over if there's a handler behind this one
		if(previous != null)
		{
			FocusController.instance().register(previous);
		}
	}

	/**
	 * Check to see if this handler alone has focus. If a contained Focusable does, this won't search through. 
	 * Instead if a Container wants to see if a child has focus, it may use its list of children to check that way. 
	 * @return True if this handler alone has focus. 
	 */
	@Override
	public boolean hasFocus() {
		return hasFocus;
	}

	/**
	 * Obtain the Focusable which this one is linked to. Traversing to the next will try to move to a child Focusable, 
	 * but that is not the linked one. This will return the linked Focusable, <b>not</b> the first child. 
	 * @return The Focusable linked next to this one
	 */
	@Override
	public Focusable getNext() {
		return next;
	}

	@Override
	public void setNext(Focusable next) {
		this.next = next;
	}

	@Override
	public Focusable getPrevious() {
		return previous;
	}

	@Override
	public void setPrevious(Focusable previous) {
		this.previous = previous;
	}
	
	/**
	 * @return The first Focusable in the container's traversal chain
	 */
	public Focusable getFirst() {
		return first;
	}
	
	/**
	 * @param first The first Focusable in the container's traversal chain
	 */
	public void setFirst(Focusable first) {
		this.first = first;
	}
	
	/**
	 * @return The last Focusable in the container's traversal chain
	 */
	public Focusable getLast() {
		return last;
	}
	
	/**
	 * @param last The last Focusable in the container's traversal chain
	 */
	public void setLast(Focusable last) {
		this.last = last;
	}

}
