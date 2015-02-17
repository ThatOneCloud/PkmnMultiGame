package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import net.cloud.client.util.IteratorException;
import net.cloud.client.util.Pair;
import net.cloud.gfx.constants.FontConstants;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * An element decorated (literally) with a border and title around it. In other words, it creates a frame 
 * around an existing element. The frame does not come with movement build into it, but can be achieved by wrapping 
 * this decorator further in a DraggableElement. In the top right of the frame, there may be buttons. These may be 
 * dynamically specified, so there can be none or several. There are pre-canned buttons to choose from that will 
 * perform a default action or further a specified action. 
 * @see FrameButton
 * @see FrameButtonAction
 */
public class FramedElement extends AbstractDecoratorElement {
	
	/** A text element to render the title text */
	private Text title;
	
	/** A map of the buttons in the frame. May be none. Each is mapped to the graphical button and an action */
	private EnumMap<FrameButton, Pair<AbstractButton, BiConsumer<FramedElement, AbstractButton>>> buttonMap;
	
	/** The background within the top. Fits same size as it, with the border on top. */
	private BufferedImage bgImage;
	
	/** Border framing the top. Aligned to width of wrapped element. */
	private BufferedImage topBorder;
	
	/** Border framing the right. Stretched to fit up to the top and bottom, but not over */
	private BufferedImage rightBorder;
	
	/** Border framing the bottom. Aligned to width of wrapped element. */
	private BufferedImage bottomBorder;
	
	/** Border framing the left. Stretched to fit up to the top and bottom, but not over */
	private BufferedImage leftBorder;
	
	/**
	 * Create a new FramedElement by wrapping the frame around an existing element. 
	 * There will always be the title showing. By default, there will be no buttons, though. 
	 * The default frame images will be used and scaled to the correct size. The scaling will not 
	 * happen if the element is resized through the element rather than through the decorator, post-creation. 
	 * Buttons may also be specified now, or later through one of the addFrameButton() methods. If added here, 
	 * they will behave as if done via <code>addFrameButton(buttonType)</code>
	 * @param title The title showing in the top left of the frame
	 * @param toDecorate The element to add a frame to
	 * @param buttons An optional list of buttons to include in the frame. Ordered right to left
	 */
	public FramedElement(String title, Element toDecorate, FrameButton... buttons)
	{
		super(toDecorate);

		// Create a map to store the buttons and their actions
		this.buttonMap = new EnumMap<>(FrameButton.class);
		
		// Get scaled images for all of the border sprites
		frameSpriteInit(1);
		
		// Get a scaled image for the background sprite. Must be done after frames.
		bgSpriteInit(SpriteSet.FRAME, 0);
		
		// The default frame isn't very big, and the text needs to fit
		this.title = new Text(title, leftBorder.getWidth() + 2, 3);
		this.title.setFontSize(FontConstants.SIZE_SMALL);
		
		// Add any of the buttons specified
		for(FrameButton bType : buttons)
		{
			addFrameButton(bType);
		}
	}
	
	/**
	 * Initialize the background sprite using the given sprite
	 * @param firstID The ID of the first sprite in the border set, the top border
	 */
	private void bgSpriteInit(SpriteSet set, int ID)
	{
		// Same size as the top border
		this.bgImage = SpriteManager.instance().getScaledSprite(set, ID, topBorder.getWidth(), topBorder.getHeight());
	}
	
	/**
	 * Initialize the border sprites using the given set of frame sprite assets
	 * @param firstID The ID of the first sprite in the border set, the top border
	 */
	private void frameSpriteInit(int firstID)
	{
		// Note that order matters
		this.topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.FRAME, firstID, getDecoratedElement().getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.FRAME, firstID+2, getDecoratedElement().getWidth(), -1);
		this.leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.FRAME, firstID+3, -1, getHeight());
		this.rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.FRAME, firstID+1, -1, getHeight());
		
	}
	
	/**
	 * Re-initialize the background to be the same size as the top border again
	 */
	private void reInitBgSprite()
	{
		this.bgImage = SpriteManager.instance().getScaledSprite(bgImage, topBorder.getWidth(), topBorder.getHeight());
	}
	
	/**
	 * Re-initialize the sprites, using the current sprite set and new size
	 */
	private void reInitFrameSprites()
	{
		// Note that order matters
		this.topBorder = SpriteManager.instance().getScaledSprite(topBorder, super.getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(bottomBorder, super.getWidth(), -1);
		this.leftBorder = SpriteManager.instance().getScaledSprite(leftBorder, -1, getHeight());
		this.rightBorder = SpriteManager.instance().getScaledSprite(rightBorder, -1, getHeight());
	}
	
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Draw the wrapped element
		super.drawElement(g, offsetX + leftBorder.getWidth(), offsetY + topBorder.getHeight());
		
		// Title background goes under the border
		g.drawImage(bgImage, offsetX, offsetY, null);
		
		// Now decorate with a 'fancy' frame
		g.drawImage(topBorder, offsetX + leftBorder.getWidth(), offsetY, null);
		g.drawImage(rightBorder, offsetX + leftBorder.getWidth() + super.getWidth(), offsetY, null);
		g.drawImage(bottomBorder, offsetX + leftBorder.getWidth(), offsetY + topBorder.getHeight() + super.getHeight(), null);
		g.drawImage(leftBorder, offsetX, offsetY, null);
		
		// Of course we have a title to accompany the frame
		title.drawElement(g, offsetX + title.getX(), offsetY + title.getY());
		
		// Draw the buttons. Good god the generic types
		for(Entry<FrameButton, Pair<AbstractButton, BiConsumer<FramedElement, AbstractButton>>> entry : buttonMap.entrySet())
		{
			// But yes. Have each button draw itself.
			AbstractButton b = entry.getValue().first;
			b.drawElement(g, offsetX + b.getX(), offsetY + b.getY());
		}
	}
	
	/**
	 * Behaves like the super class in that if the top element is the wrapped element, it will be treated as 'this' 
	 * However this element will also be the top at the point if the point is over the frame border anywhere
	 */
	@Override
	public Element topElementAtPoint(Point point) throws IteratorException
	{
		// Get some shifted rectangles that are the relative bounds for this and the wrapped element
		Rectangle frameRect = new Rectangle(getRectangle());
		frameRect.setLocation(0, 0);
		Rectangle wrappedRect = new Rectangle(super.getRectangle());
		wrappedRect.setLocation(leftBorder.getWidth(), topBorder.getHeight());
		
		// Check if the point is over the frame before delving into any child hierarchies
		if(frameRect.contains(point) && !wrappedRect.contains(point))
		{
			// Yeah, it's over the frame. We know the buttons would lie within this frame, so check for those
			for(Entry<FrameButton, Pair<AbstractButton, BiConsumer<FramedElement, AbstractButton>>> entry : buttonMap.entrySet())
			{
				// Pull the button out of the pair
				AbstractButton b = entry.getValue().first;
				
				// See if the point lies somewhere within the button
				if(b.getRectangle().contains(point))
				{
					// Maintain general behavior by asking the button to find its top, rather than assuming it is the button
					point.translate(-b.getX(), -b.getY());
					return b.topElementAtPoint(point);
				}
			}
			
			// It wasn't one of the buttons, it must just be us
			return this;
		}
		
		// Now do the typical decorator lookup, with a point shifted relative to the wrapped element
		point.translate(-leftBorder.getWidth(), -topBorder.getHeight());
		return super.topElementAtPoint(point);
	}
	
	/**
	 * Uses the default image for the button and performs the default action for the button when pressed. 
	 * Make sure to look and see what the default action is!<br>
	 * See {@link FramedElement#addFrameButton(FrameButton, int, BiConsumer)}
	 * @param buttonType The type of button, so defaults are available
	 */
	public void addFrameButton(FrameButton buttonType)
	{
		addFrameButton(buttonType, buttonType.getDefaultSprite(), buttonType.getDefaultAction().getAction());
	}
	
	/**
	 * Uses the default image for the button <br>
	 * See {@link FramedElement#addFrameButton(FrameButton, int, BiConsumer)}
	 * @param buttonType The type of button, so defaults are available
	 * @param action An action to perform when the button is pressed
	 */
	public void addFrameButton(FrameButton buttonType, BiConsumer<FramedElement, AbstractButton> action)
	{
		addFrameButton(buttonType, buttonType.getDefaultSprite(), action);
	}
	
	/**
	 * Uses a pre-canned action and the default image for the button <br>
	 * See {@link FramedElement#addFrameButton(FrameButton, int, BiConsumer)}
	 * @param buttonType The type of button, so defaults are available
	 * @param action A pre-made action to perform when the button is pressed
	 */
	public void addFrameButton(FrameButton buttonType, FrameButtonAction action)
	{
		addFrameButton(buttonType, buttonType.getDefaultSprite(), action.getAction());
	}
	
	/**
	 * Uses a pre-canned button action <br>
	 * See {@link FramedElement#addFrameButton(FrameButton, int, BiConsumer)}
	 * @param buttonType The type of button, so defaults are available
	 * @param buttonSprite The sprite ID of the button
	 * @param action A pre-made action to perform when the button is pressed
	 */
	public void addFrameButton(FrameButton buttonType, int buttonSprite, FrameButtonAction action)
	{
		addFrameButton(buttonType, buttonSprite, action.getAction());
	}
	
	/**
	 * Add a button to this frame. It will be positioned to the left of any existing buttons, or in the 
	 * top right of the frame if there are no other buttons. The type of the button defines, well, what kind it is. 
	 * Frames really only need a limited number of buttons, so typing them allows for easier use. If a button of the 
	 * same type already exists, then it will be overwritten. The sprite is the ID of the image in the Button set. Its 
	 * size will not be scaled, so it should fit within the top border of the frame. The action is a method that will 
	 * be called when the button is pressed. It is provided both this FramedElement instance and the Button instance 
	 * that was pressed. 
	 * @param buttonType The type of button, so defaults are available
	 * @param buttonSprite The sprite ID of the button
	 * @param action An action to perform when the button is pressed
	 */
	public void addFrameButton(FrameButton buttonType, int buttonSprite, BiConsumer<FramedElement, AbstractButton> action)
	{
		// Pre-load the button image, we'll use it for some measurements in advance
		BufferedImage bImg = SpriteManager.instance().getSprite(SpriteSet.BUTTON, buttonSprite);
		
		// The position of the button is based off of the other buttons. It'll be added to the left of them. 
		int bPosX = getWidth() - rightBorder.getWidth() - 1;
		for(Entry<FrameButton, Pair<AbstractButton, BiConsumer<FramedElement, AbstractButton>>> entry : buttonMap.entrySet())
		{
			bPosX -= entry.getValue().first.getWidth();
		}
		bPosX -= bImg.getWidth();
		
		// Create a Button element. No label, it's an icon button
		ImageButton b = new ImageButton(bPosX, 3, buttonSprite);
		
		
		// Attach an action to the button, so when it is performed, it calls our action
		b.setActionHandler((actionB) -> action.accept(this, actionB));
		
		// Place a mapping for the newly created button and its action
		buttonMap.put(buttonType, new Pair<>(b, action));
	}
	
	/**
	 * Removes a button from the frame, clean and simple. Will not reposition the remaining buttons, 
	 * since this is likely border scenario. (Create the frame only with the desired buttons). 
	 * If the button doesn't exist, nothing will happen
	 * @param buttonType The type of the button to remove
	 */
	public void removeFrameButton(FrameButton buttonType)
	{
		buttonMap.remove(buttonType);
	}
	
	/**
	 * Get the bounding rectangle of just the top border, the part that contains the title information. 
	 * This is useful for setting the restrictions on where dragging may occur, for example.
	 * @return A bounding rectangle on the top border, relative to within this element
	 */
	public Rectangle getTitleBounds()
	{
		return new Rectangle(leftBorder.getWidth(), 0, topBorder.getWidth(), topBorder.getHeight());
	}
	
	/**
	 * Get a new bounding rectangle which covers both the original element as well as the frame
	 */
	@Override
	public Rectangle getRectangle()
	{
		// Create a new rectangle - don't want to change the original
		Rectangle r = new Rectangle();
		
		// Modify it, so it is extended to the frame's bounds
		r.setBounds(getX(), getY(), getWidth(), getHeight());
		
		return r;
	}

	/** Modified to re-scale the frame image */
	@Override
	public void setRectangle(Rectangle r)
	{
		// This will set the rectangle on the wrapped element
		super.setRectangle(r);
		
		// and this will scale the sprites
		reInitFrameSprites();
		reInitBgSprite();
	}
	
	/** Shifted to include the frame */
	@Override
	public int getWidth()
	{
		return getDecoratedElement().getWidth() + leftBorder.getWidth() + rightBorder.getWidth();
	}
	
	/** Modified to re-scale the frame image */
	@Override
	public void setWidth(int width)
	{
		// Keep the width before resizing - so we know how much to shift buttons
		int origWidth = getWidth();
		
		
		// Change the original dimension
		super.setWidth(width);
		
		// then resize the sprites
		reInitFrameSprites();
		reInitBgSprite();
		
		// then shift the buttons
		int buttonShiftX = getWidth() - origWidth;
		for(Entry<FrameButton, Pair<AbstractButton, BiConsumer<FramedElement, AbstractButton>>> entry : buttonMap.entrySet())
		{
			entry.getValue().first.setX(entry.getValue().first.getX() + buttonShiftX);
		}
	}
	
	/** Shifted to include the frame */
	@Override
	public int getHeight()
	{
		return getDecoratedElement().getHeight() + topBorder.getHeight() + bottomBorder.getHeight();
	}
	
	/** Modified to re-scale the frame image */
	@Override
	public void setHeight(int height)
	{
		// Change the original dimension
		super.setHeight(height);
		
		// then resize the sprites
		reInitFrameSprites();
		reInitBgSprite();
	}

	/** @return The text in the title of the frame */
	public String getTitleText()
	{
		return title.getText();
	}
	
	/** @param title The new text to show as a frame title */
	public void setTitleText(String title)
	{
		this.title.setText(title);
	}
	
	/** @return The font being used for the title. */
	public Font getTitleFont()
	{
		return title.getFont();
	}
	
	/** @param font The font to use for the title. Can set this to change size and such as well */
	public void setTitleFont(Font font)
	{
		title.setFont(font);
	}
	
	/** @return The color of the title text */
	public Color getTitleColor()
	{
		return title.getColor();
	}
	
	/** @param color The color of the title text */
	public void setTitleColor(Color color)
	{
		title.setColor(color);
	}
	
	/**
	 * Sets the border images to use for this frame. They are scaled to the correct size.
	 * @param firstID The Sprite ID of the first in the group - which is the top border
	 */
	public void setFrameSpriteGroup(int firstID)
	{
		frameSpriteInit(firstID);
	}
	
	/**
	 * Sets the background sprite that will be a background for the title border. 
	 * It is set to the same size as the top border, so if setting both frame sprites and this, you should 
	 * set the frame sprites first.
	 * @param set The SpriteSet the background is in 
	 * @param ID The sprite ID of the background image
	 */
	public void setBgSprite(SpriteSet set, int ID)
	{
		bgSpriteInit(set, ID);
	}

}
