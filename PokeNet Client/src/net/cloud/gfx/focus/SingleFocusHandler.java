package net.cloud.gfx.focus;

/**
 * A focus handler which acts as a single link. This handler will keep track of the focus 
 * of a single thing, and can have a previous and next handler in line. 
 */
public class SingleFocusHandler implements FocusHandler {
	
	/** A flag for whether or not this handler has focus */
	private boolean hasFocus;
	
	/** The previous handler in line, if any */
	private Focusable previous;
	
	/** The next handler in line, if any */
	private Focusable next;
	
	/**
	 * Create a new focus handler for a single thing. It will by default not have focus 
	 * and there will not be any previous or next handler. 
	 */
	public SingleFocusHandler()
	{
		this.hasFocus = false;
		this.previous = null;
		this.next = null;
	}

	@Override
	public void focusGained() {
		// Set the focus flag. Can be checked on demand by hasFocus()
		this.hasFocus = true;
	}

	@Override
	public void focusLost() {
		// Clear the focus flag
		this.hasFocus = false;
	}

	@Override
	public void traverseNext() {
		// Transfer focus over if there's a handler up next
		if(next != null)
		{
			FocusController.instance().register(next);
		}
	}

	@Override
	public void traversePrevious() {
		// Transfer focus over if there's a handler behind this one
		if(previous != null)
		{
			FocusController.instance().register(previous);
		}
	}

	@Override
	public boolean hasFocus() {
		return hasFocus;
	}
	
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

}
