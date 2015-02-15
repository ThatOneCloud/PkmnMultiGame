package net.cloud.gfx.elements;

import java.awt.Point;

import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.focus.FocusController;

/**
 * A generic base class for buttons elements. It has the code to take care of being clicked on, 
 * handling key events, and maintaining basic state information (whether the button has focus, is pressed 
 * down, or is just sitting there.) 
 * Subclasses must take care of drawing themselves, and can override various methods to grab events.
 */
public abstract class AbstractButton extends AbstractElement {

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
	public void clicked(Element clicked, Point relPoint, boolean isRightClick) {}
	
	/**
	 * When a button is pressed, it will request focus - even if only momentarily. (Rather than via a click)
	 * This is so that focus can be kept on a button that was pressed, conditionally, depending on the release. 
	 */
	@Override
	public void pressed(Element pressed, Point relPoint)
	{
		super.pressed(pressed, relPoint);
		
		// Register focus. May be relinquished upon release
		FocusController.instance().register(pressed);
	}
	
	/**
	 * When the mouse is released, for sure the button will no longer be pressed down. Regardless of release point. 
	 * Also, if the button was already pressed down and the release is over the button, then that is what constitutes 
	 * clicking the button. (Not the actual click event - which is more specific)
	 */
	@Override
	public void released(Element released, Point relPoint, boolean onElement)
	{
		// Keep a local copy of if we are pressed down, so we can release the button sooner. For reasons. Just in case.
		boolean isPressedDown = super.isPressedDown();
		
		// Release now, so drawing will appear back to normal
		super.released(released, relPoint, onElement);
		
		// When the release is over a button that is pressed down and has focus, it's similar to a click. 
		// We'll give up focus, so the button does not stay focused. To keep focus by pressing, the mouse can be 
		// moved off the button and then released.
		if(isPressedDown && onElement && super.getFocusHandler().hasFocus())
		{
			FocusController.instance().deregister();
		}
		
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
			return;
		}
		
		super.keyTyped(key);
	}
	
	/**
	 * Called when an action has been performed on the button. Go figure. 
	 * Essentially this means the button was pushed, selected, or acted on in some way. 
	 */
	protected abstract void actionPerformed();

}
