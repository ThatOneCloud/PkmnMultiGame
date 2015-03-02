package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Consumer;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A button which is represented only by an image, with no label. Might sometimes be better known as an IconButton. 
 * Behaves exactly the same as a normal button. Creation is a bit more concise. The empty string is not needed, and there 
 * is a constructor which does not need a width or height - the width and height of the button will be used instead.
 */
public class ImageButton extends AbstractButton {
	
	/** Default priority of a Button. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** Called when an action is performed on the button. */
	private Optional<Consumer<ImageButton>> actionHandler;
	
	/** Normal background. For when the button is just sitting there. */
	private BufferedImage normalImage;
	
	/** Background for when the button has focus, but is not pressed down */
	private BufferedImage focusImage;
	
	/** Background for when the button is pressed down. */
	private BufferedImage pressedImage;
	
	/**
	 * Create a new image button with the given image. It will be from the Button sprite set. 
	 * The width and height of the button will match the image.
	 * @param x X location
	 * @param y Y location
	 * @param spriteID The first ID of the image sprites to use
	 */
	public ImageButton(int x, int y, int spriteID)
	{
		// Create with no with or height to start
		super(PRIORITY, x, y);
		
		// Now load up the sprites
		spriteInit(spriteID);
		
		// and set the width & height to match the sprites
		super.setWidth(normalImage.getWidth());
		super.setHeight(normalImage.getHeight());
		
		// No action handler to start
		actionHandler = Optional.empty();
	}
	
	/**
	 * Create a new image button using the given image scaled to the given dimensions
	 * @param x X location
	 * @param y Y location
	 * @param width Width of the button
	 * @param height Height of the button
	 * @param spriteID The first ID of the image sprites to use
	 */
	public ImageButton(int x, int y, int width, int height, int spriteID)
	{
		// Create with specified width and height
		super(PRIORITY, x, y, width, height);
		
		// Normal sprite initialization, followed by scaling. Would have to load normal sprites anyways, so not inefficient.
		spriteInit(spriteID);
		scaleSprites();
		
		// No action handler to start
		actionHandler = Optional.empty();
	}
	
	/**
	 * Create a new image button using the given images
	 * @param x X location
	 * @param y Y location
	 * @param normal Image for when the button is just sitting there
	 * @param focused Image for when the button has key focus
	 * @param pressed Image for when the button is pressed down
	 */
	public ImageButton(int x, int y, BufferedImage normal, BufferedImage focused, BufferedImage pressed)
	{
		// Create with no with or height to start
		super(PRIORITY, x, y);
		
		this.normalImage = normal;
		this.focusImage = focused;
		this.pressedImage = pressed;
		
		// Set the width & height to match the sprites
		super.setWidth(normalImage.getWidth());
		super.setHeight(normalImage.getHeight());
		
		// No action handler to start
		actionHandler = Optional.empty();
	}
	
	/**
	 * Initialize the background sprites using the given set of button sprite assets. 
	 * The size will be left as the original
	 */
	private void spriteInit(int firstID)
	{
		// All of the default sprites. May be reset as a group later. 
		this.normalImage = SpriteManager.instance().getSprite(SpriteSet.BUTTON, firstID);
		this.focusImage = SpriteManager.instance().getSprite(SpriteSet.BUTTON, firstID+1);
		this.pressedImage = SpriteManager.instance().getSprite(SpriteSet.BUTTON, firstID+2);
	}
	
	/**
	 * Re-initialize the sprites, using the current sprite set, to match the current size. 
	 * Called after normal initialization if a scaled size is needed.
	 */
	private void scaleSprites()
	{
		// Use the existing sprite instead of getting from a sprite set
		this.normalImage = SpriteManager.instance().getScaledSprite(normalImage, getWidth(), getHeight());
		this.focusImage = SpriteManager.instance().getScaledSprite(focusImage, getWidth(), getHeight());
		this.pressedImage = SpriteManager.instance().getScaledSprite(pressedImage, getWidth(), getHeight());
	}

	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Draw background image based on state
		if(super.isPressedDown())
		{
			g.drawImage(pressedImage, offsetX, offsetY, null);
		}
		else if(super.getFocusHandler().hasFocus())
		{
			g.drawImage(focusImage, offsetX, offsetY, null);
		}
		else {
			g.drawImage(normalImage, offsetX, offsetY, null);
		}
	}

	/**
	 * Called when the button was pressed or selected via the action key. 
	 * Simply calls the action handler if there is one
	 */
	@Override
	protected void actionPerformed() 
	{
		// Just notify the action handler
		actionHandler.ifPresent((handler) -> handler.accept(this));
	}
	
	/**
	 * Modify the width of the button. The button assets will be appropriately updated. 
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		// Scale the sprites again
		scaleSprites();
	}
	
	/**
	 * Modify the height of the button. The button assets will be appropriately updated. 
	 */
	@Override
	public void setHeight(int height)
	{
		super.setHeight(height);
		
		// Scale the sprites again
		scaleSprites();
	}
	
	/**
	 * Set a new action to this button. When the button is pressed or has an action performed on it, 
	 * the action's method will be called and supply this button instance. By providing null as a parameter 
	 * to this method, you may remove the current handler. 
	 * @param action The method to call when action is taken on this button
	 */
	public void setActionHandler(Consumer<ImageButton> action)
	{
		this.actionHandler = Optional.ofNullable(action);
	}
	
	/**
	 * Sets the background images to use for this button. They are scaled to the correct size. 
	 * @param firstID The sprite ID of the first in the group - which is the normal background
	 */
	public void setSpriteGroup(int firstID)
	{
		// Set each of the sprites anew
		spriteInit(firstID);
		scaleSprites();
	}

}
