package net.cloud.gfx.elements;

import java.awt.Point;

import net.cloud.gfx.constants.KeyConstants;

/**
 * A generic base class for buttons elements. It has the code to take care of being clicked on, 
 * handling key events, and maintaining basic state information (whether the button has focus, is pressed 
 * down, or is just sitting there.) 
 * Subclasses must take care of drawing themselves, and can override various methods to grab events.
 */
public abstract class AbstractButton extends Element {

	/**
	 * Create a button with the given fields. The parent will be left null. 
	 * @param priority Z-priority
	 * @param x X-location
	 * @param y Y-location
	 * @param width Width of the entire button
	 * @param height Height of the entire button
	 */
	public AbstractButton(int priority, int x, int y, int width, int height)
	{
		super(null, priority, x, y, width, height);
	}
	
	/**
	 * Overridden to do nothing. A mouse click will not make a general button gain focus, 
	 * because then it just looks silly and feels a bit unintuitive. Could still key over to it, though.
	 */
	@Override
	public void clicked(Point relPoint, boolean isRightClick) {}
	
	/**
	 * pressed isn't overridden, isn't that odd? Well, anyways... <br>
	 * When the mouse is released, for sure the button will no longer be pressed down. Regardless of release point. 
	 * Also, if the button was already pressed down and the release is over the button, then that is what constitutes 
	 * clicking the button. (Not the actual click event - which is more specific)
	 */
	@Override
	public void released(Point relPoint, boolean onElement)
	{
		// Keep a local copy of if we are pressed down, so we can release the button sooner. For reasons. Just in case.
		boolean isPressedDown = super.isPressedDown();
		
		// Release now, so drawing will appear back to normal
		super.released(relPoint, onElement);
		
		// Take action if the release is over the button and we were pressed down, as well
		if(isPressedDown && onElement)
		{
			actionPerformed();
		}
	}
	
	/**
	 * Take care of button-specific key events, such as the action key being 
	 * used to perform the button's action. Other events are allowed to propagate 
	 * back to the superclass, such as focus traversal keys.
	 */
	@Override
	public void keyTyped(char key) 
	{
		// Really we're only interested in the action key. Inherently have focus if receiving key event.
		if(key == KeyConstants.ACTION_KEY)
		{
			// Call action performed so custom things happen
			actionPerformed();
		}
	}
	
	/**
	 * Called when an action has been performed on the button. Go figure. 
	 * Essentially this means the button was pushed, selected, or acted on in some way. 
	 */
	protected abstract void actionPerformed();

}
