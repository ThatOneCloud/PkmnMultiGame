package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Consumer;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A plain button. Shows a button with some text centered within it. May be clicked on, or activated 
 * via a press of the action key. Namely, this takes care of drawing whilst AbstractButton takes care 
 * of the events. When an action is performed, the action handler (if any) is called. <br>
 * Extra configuration can be done post-construction for those options which there is not a constructor for. 
 * The label font, priority, and action are primarily set this way. 
 */
public class Button extends AbstractButton {
	
	/** Default priority of a Button. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** The text showing as a hint on the button. */
	private CenteredText labelText;
	
	/** Called when an action is performed on the button. */
	private Optional<Consumer<Button>> actionHandler;
	
	/** Normal background. For when the button is just sitting there. */
	private BufferedImage normalBackground;
	
	/** Background for when the button has focus, but is not pressed down */
	private BufferedImage focusBackground;
	
	/** Background for when the button is pressed down. */
	private BufferedImage pressedBackground;

	/**
	 * Create a simple Button at the given location. It will have the given text on it. 
	 * The width and height will be a default of 100x25
	 * @param label A text label to describe the button. 
	 * @param x X-location
	 * @param y Y-location
	 */
	public Button(String label, int x, int y) 
	{
		// Provide a default width and height
		this(label, x, y, 100, 25);
	}
	
	/**
	 * Create a simple Button at the given location. It will have the given text on it, 
	 * centered within the button
	 * @param label A text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the button
	 * @param height The height of the button
	 */
	public Button(String label, int x, int y, int width, int height)
	{
		// Just use the default sprite set
		this(label, x, y, width, height, 0);
	}
	
	/**
	 * Create a simple Button at the given location. It will have the given text on it, 
	 * centered within the button. The image assets used will be those started at the given 
	 * sprite ID in the Button sprite set, and the next two for focused and pressed. 
	 * @param label A text label to describe the button
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the button
	 * @param height The height of the button
	 * @param firstSpriteID The sprite ID of the first background image asset
	 */
	public Button(String label, int x, int y, int width, int height, int firstSpriteID)
	{
		// Create with specified width and height
		super(PRIORITY, x, y, width, height);

		this.labelText = new CenteredText(label, width, height);
		this.labelText.setParent(new ParentElement(this));

		// Of course it does not start with an action handler
		actionHandler = Optional.empty();

		// No special sprite set specified. Use the default.
		spriteInit(firstSpriteID);
	}
	
	/**
	 * Initialize the background sprites using the given set of button sprite assets
	 * @param firstID The ID of the first sprite to use
	 */
	private void spriteInit(int firstID)
	{
		// All of the default sprites. May be reset as a group later. 
		this.normalBackground = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID, getWidth(), getHeight());
		this.focusBackground = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+1, getWidth(), getHeight());
		this.pressedBackground = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+2, getWidth(), getHeight());
	}
	
	/**
	 * Re-initialize the sprites, using the current sprite set, to match the current size
	 */
	private void reInitSprites()
	{
		// Use the existing sprite instead of getting from a sprite set
		this.normalBackground = SpriteManager.instance().getScaledSprite(normalBackground, getWidth(), getHeight());
		this.focusBackground = SpriteManager.instance().getScaledSprite(focusBackground, getWidth(), getHeight());
		this.pressedBackground = SpriteManager.instance().getScaledSprite(pressedBackground, getWidth(), getHeight());
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
	 * Draws the button image based on the current state, as well as the hint text. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		Graphics2D g2d = (Graphics2D) g;
		
		// Draw background image based on state
		if(super.isPressedDown())
		{
			g2d.drawImage(pressedBackground, offsetX, offsetY, null);
		}
		else if(super.getFocusHandler().hasFocus())
		{
			g2d.drawImage(focusBackground, offsetX, offsetY, null);
		}
		else {
			g2d.drawImage(normalBackground, offsetX, offsetY, null);
		}
		
		// Draw centered text element
		labelText.drawElement(g2d, offsetX + labelText.getX(), offsetY + labelText.getY());
	}
	
	/**
	 * Modify the width of the button. The button assets will be appropriately updated. 
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		// Inform the text label of the change, too, so text remains centered
		labelText.setWidth(width);
		
		// Scale the sprites again
		reInitSprites();
	}
	
	/**
	 * Modify the height of the button. The button assets will be appropriately updated. 
	 */
	@Override
	public void setHeight(int height)
	{
		super.setHeight(height);
		
		// Inform the text label of the change, too, so text remains centered
		labelText.setHeight(height);
		
		// Scale the sprites again
		reInitSprites();
	}
	
	/** @return The text on the button's label */
	public String getLabelText()
	{
		return labelText.getText();
	}
	
	/** @param label The new button label to use */
	public void setLabelText(String label)
	{
		labelText.setText(label);
	}
	
	/** @return The font being used for the label text */
	public Font getLabelFont()
	{
		return labelText.getFont();
	}
	
	/** @param font The new font to use for the label text */
	public void setLabelFont(Font font)
	{
		labelText.setFont(font);
	}
	
	/** @return The color being used for the label text */
	public Color getLabelColor()
	{
		return labelText.getColor();
	}
	
	/** @param color The new color to use for the label text */
	public void setLabelColor(Color color)
	{
		labelText.setColor(color);
	}
	
	/**
	 * Set a new action to this button. When the button is pressed or has an action performed on it, 
	 * the action's method will be called and supply this button instance. By providing null as a parameter 
	 * to this method, you may remove the current handler. 
	 * @param action The method to call when action is taken on this button
	 */
	public void setActionHandler(Consumer<Button> action)
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
	}

}
