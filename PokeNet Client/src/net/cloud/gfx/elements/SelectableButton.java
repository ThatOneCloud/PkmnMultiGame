package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A base class for selectable buttons, such as checkboxes and radio buttons. 
 * Extends upon the abstract button by actually offering the drawing code, and keeps track of whether 
 * or not the button is currently selected. <br>
 * In general, a selectable button has 6 sprites - the cross product of normal, focused, pressed and selected, not selected. 
 * They are drawn as a button with label text drawn to the right of the button. <br>
 * <code>actionPerformed()</code> is still left to subclasses, but this class will take care of toggling selection state. 
 */
public abstract class SelectableButton extends AbstractButton {
	
	/** Flag indicating if this button is selected or not right now */
	private boolean isSelected;
	
	/** The text showing as a label to the right of the button */
	private Text label;
	
	/** Normal not selected */
	private BufferedImage normalNotSelected;
	
	/** Focused not selected */
	private BufferedImage focusedNotSelected;
	
	/** Pressed not selected */
	private BufferedImage pressedNotSelected;
	
	/** Normal selected */
	private BufferedImage normalSelected;
	
	/** Focused selected */
	private BufferedImage focusedSelected;
	
	/** Pressed selected */
	private BufferedImage pressedSelected;
	
	/**
	 * Create a button with the given fields. The parent will be left null. 
	 * @param label The text label to accompany the button
	 * @param priority Z-priority
	 * @param x X-location
	 * @param y Y-location
	 * @param width Width of the button itself
	 * @param height Height of the button itself
	 * @param firstSpriteID The sprite ID of the first background image asset
	 */
	public SelectableButton(String label, int priority, int x, int y, int width, int height, int firstSpriteID)
	{
		super(priority, x, y, width, height);
		
		this.isSelected = false;
		
		// Get scaled images for all of the sprites 
		spriteInit(firstSpriteID);
		
		// Create a label which is positioned to the right of the button
		this.label = new Text(label, width + 3, 0);
		this.label.setParent(new ParentElement(this));
	}
	
	/**
	 * Initialize the background sprites using the given set of button sprite assets
	 * @param firstID The ID of the first sprite to use
	 */
	private void spriteInit(int firstID)
	{
		// All of the default sprites. May be reset as a group later. 
		this.normalNotSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID, getWidth(), getHeight());
		this.focusedNotSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+1, getWidth(), getHeight());
		this.pressedNotSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+2, getWidth(), getHeight());
		this.normalSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+3, getWidth(), getHeight());
		this.focusedSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+4, getWidth(), getHeight());
		this.pressedSelected = SpriteManager.instance().getScaledSprite(SpriteSet.BUTTON, firstID+5, getWidth(), getHeight());
	}
	
	/**
	 * Re-initialize the sprites, using the current sprite set, to match the current size
	 */
	private void reInitSprites()
	{
		// Use the existing sprite instead of getting from a sprite set
		this.normalNotSelected = SpriteManager.instance().getScaledSprite(normalNotSelected, getWidth(), getHeight());
		this.focusedNotSelected = SpriteManager.instance().getScaledSprite(focusedNotSelected, getWidth(), getHeight());
		this.pressedNotSelected = SpriteManager.instance().getScaledSprite(pressedNotSelected, getWidth(), getHeight());
		this.normalSelected = SpriteManager.instance().getScaledSprite(normalSelected, getWidth(), getHeight());
		this.focusedSelected = SpriteManager.instance().getScaledSprite(focusedSelected, getWidth(), getHeight());
		this.pressedSelected = SpriteManager.instance().getScaledSprite(pressedSelected, getWidth(), getHeight());
	}
	
	/**
	 * Called when the button was pressed or selected via the action key. 
	 * This will toggle the selection state of the button. Subclasses should call 
	 * <code>super.actionPerformed()</code> or manually toggle the selection state when this happens. 
	 */
	@Override
	protected void actionPerformed() 
	{
		// Toggle selection state
		toggleSelected();
	}

	/**
	 * Draw the button based on its current state, with label text to the right of the button
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		Graphics2D g2d = (Graphics2D) g;
		
		// There are some more state possibilities now, but still just choose the right one. If/else seems the most obvious, still. 
		if(isSelected)
		{
			if(super.isPressedDown())
			{
				g2d.drawImage(pressedSelected, offsetX, offsetY, null);
			}
			else if(super.getFocusHandler().hasFocus())
			{
				g2d.drawImage(focusedSelected, offsetX, offsetY, null);
			}
			else {
				g2d.drawImage(normalSelected, offsetX, offsetY, null);
			}
		}
		else
		{
			if(super.isPressedDown())
			{
				g2d.drawImage(pressedNotSelected, offsetX, offsetY, null);
			}
			else if(super.getFocusHandler().hasFocus())
			{
				g2d.drawImage(focusedNotSelected, offsetX, offsetY, null);
			}
			else {
				g2d.drawImage(normalNotSelected, offsetX, offsetY, null);
			}
		}
		
		// And then the text can do its thing
		label.drawElement(g2d, offsetX + label.getX(), offsetY + label.getY());
	}
	
	/** @return True if the button is currently selected */
	public boolean isSelected()
	{
		return isSelected;
	}
	
	/** @param state Whether the button should become selected or not */
	public void setSelected(boolean state)
	{
		this.isSelected = state;
	}
	
	/**
	 * Convenience method, changes the selection to the opposite state. Equivalent to <br>
	 * <code>setSelected(!isSelected());</code>
	 */
	public void toggleSelected()
	{
		setSelected(!isSelected());
	}
	
	/**
	 * Modify the width of the button. The button assets will be appropriately updated. 
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
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
		
		// Scale the sprites again
		reInitSprites();
	}
	
	/** @return The text on the button's label */
	public String getLabelText()
	{
		return label.getText();
	}
	
	/** @param label The new button label to use */
	public void setLabelText(String label)
	{
		this.label.setText(label);
	}
	
	/** @return The font being used for the label text */
	public Font getLabelFont()
	{
		return label.getFont();
	}
	
	/** @param font The new font to use for the label text */
	public void setLabelFont(Font font)
	{
		label.setFont(font);
	}
	
	/** @return The color being used for the label text */
	public Color getLabelColor()
	{
		return label.getColor();
	}
	
	/** @param color The new color to use for the label text */
	public void setLabelColor(Color color)
	{
		label.setColor(color);
	}
	
	/**
	 * Sets the background images to use for this button. They are scaled to the correct size. 
	 * @param firstID The sprite ID of the first in the group - which is the normal not selected background
	 */
	public void setSpriteGroup(int firstID)
	{
		// Set each of the sprites anew
		spriteInit(firstID);
	}

}
