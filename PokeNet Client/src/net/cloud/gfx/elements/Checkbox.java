package net.cloud.gfx.elements;

import java.util.Optional;
import java.util.function.Consumer;

import net.cloud.gfx.constants.Priority;

/**
 * A checkbox is a box which well.. uh.. may be checked. Any number of them may be checked. 
 * There is a text label to the right of the checkbox to indicate what it is for.  <br>
 * As with a normal button, only so many constructor options are available. For wider control, 
 * there are setter methods to configure things such as font, priority, and action. 
 */
public class Checkbox extends SelectableButton {
	
	/** Default priority of a Button. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** Called when an action is performed on the button. */
	private Optional<Consumer<Checkbox>> actionHandler;
	
	/**
	 * Create a new checkbox with default size, image assets, and priority. The default size is 
	 * 15x15. If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 */
	public Checkbox(String label, int x, int y)
	{
		// Defaults!
		this(label, x, y, 15, 15, 3);
	}
	
	/**
	 * Create a new checkbox with default image assets, and priority. The provided dimensions are only for the button. 
	 * If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the checkbox itself - does not include the text
	 * @param height The height of the checkbox itself - does not include the text
	 */
	public Checkbox(String label, int x, int y, int width, int height)
	{
		// Less defaults!
		this(label, x, y, width, height, 3);
	}

	/**
	 * Create a new checkbox with the given parameters. The provided dimensions are only for the button. 
	 * If you want to resize much bigger than that, it's probably a better idea to use a larger sprite. 
	 * If you want to use a custom sprite, prefer this constructor to avoid extra image work. 
	 * @param label Text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the checkbox itself - does not include the text
	 * @param height The height of the checkbox itself - does not include the text
	 * @param firstSpriteID The sprite ID of the first background image asset
	 */
	public Checkbox(String label, int x, int y, int width, int height, int firstSpriteID)
	{
		// All taken care of by SelectableButton!
		super(label, PRIORITY, x, y, width, height, firstSpriteID);
	}
	
	/**
	 * Toggles the state of the checkbox, and then informs the action handler if one is present. 
	 */
	@Override
	public void actionPerformed()
	{
		// Superclass will take care of selection stuff
		super.actionPerformed();
		
		// Then we worry about passing the event off to a handler
		actionHandler.ifPresent((handler) -> handler.accept(this));
	}
	
	/**
	 * Set a new action to this button. When the button is pressed or has an action performed on it, 
	 * the action's method will be called and supply this button instance. By providing null as a parameter 
	 * to this method, you may remove the current handler. 
	 * @param action The method to call when action is taken on this button
	 */
	public void setActionHandler(Consumer<Checkbox> action)
	{
		this.actionHandler = Optional.ofNullable(action);
	}

}
