package net.cloud.gfx.elements.modal;

import java.awt.SecondaryLoop;
import java.awt.Toolkit;

/**
 * Base class for Dialog Listeners. These are objects which wait for a dialog to have some action performed 
 * on it, before allowing a return. The return value is also interpreted and coalesced into a single type. 
 * Namely, this provides the common functionality of waiting and notifying, and maintaining a flag on 
 * whether the value is ready and that value.
 * 
 * @param <T> Type of the coalesced return value from the dialog 
 */
public abstract class DialogListener<T> {
	
	/** The actual return value. Unavailable until flag is set */
	private T value;
	
	/** Flag, should only be set once the value is ready */
	private boolean valueReady;
	
	/** This plays nice with the EDT so the calling thread may be blocked without preventing more input */
	private SecondaryLoop secondaryLoop;
	
	/**
	 * Create a new listener that will have no value, and the value will of course not be ready. 
	 */
	public DialogListener()
	{
		value = null;
		valueReady = false;

		// I guess this is how you get a SecondaryLoop to use
		secondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
	}
	
	/**
	 * Wait until this listener has a value available. 
	 * This will block the calling thread. 
	 * @return The value resulting from interaction with the dialog
	 * @throws ModalException If the wait could not be performed (No blocking happened)
	 */
	public T waitForValue() throws ModalException
	{
		// Check if the value is there before even synchronizing
		if(valueReady)
		{
			return value;
		}
		
		// Entering the loop will block the calling thread, while still allowing AWT input
		if(!secondaryLoop.enter())
		{
			throw new ModalException("Could not block to wait for modal dialog value");
		}
		
		// We should probably fall out of the synchronized block before returning. Although this one doesn't matter too much...
		return value;
	}
	
	/**
	 * This should be used as compared to the <code>setValue</code> methods, this will do more work. 
	 * Assign the value and notify the listener that the value is ready. 
	 * Then, any waiting threads will be notified and allowed to return.
	 * @param value The value given back from the dialog
	 */
	public void notifyReady(T value)
	{
		valueReady = true;
		this.value = value;
		
		// This will unblock the thread that entered the loop
		secondaryLoop.exit();
	}

	/** @return The singular value representing the result of interaction with the dialog */
	public T getValue() {
		return value;
	}

	/** @param The value given back from dialog */
	public void setValue(T value) {
		this.value = value;
	}

	/** @return True only once the value is ready */
	public boolean isValueReady() {
		return valueReady;
	}

	/** @param valueReady True once the value is ready */
	public void setValueReady(boolean valueReady) {
		this.valueReady = valueReady;
	}
	
}
