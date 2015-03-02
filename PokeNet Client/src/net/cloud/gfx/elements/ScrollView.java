package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Optional;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A ScrollView is an element which contains a single other element. 
 * The ScrollView will make it so that only a portion of the other element is visible, although this may be all of it. 
 * This is done by framing the element, and providing scroll bars (vertical and/or horizontal)
 * The scroll bar and frame behavior can be set
 */
public class ScrollView extends AbstractElement {
	
	/** Things tend to show over views */
	public static final int PRIORITY = Priority.MED_LOW;
	
	/**
	 * A simple enum, scroll bars may either always be showing or only show when they would be needed. 
	 * Yes, an enum rather than booleans. It's... clearer.
	 */
	public static enum ScrollbarVisibility {ALWAYS, WHEN_NEEDED};
	
	/** Offset for the top border sprite */
	private static final int TOP_BORDER = 0;
	
	/** Offset for the right border sprite */
	private static final int RIGHT_BORDER = 1;
	
	/** Offset for the bottom border sprite */
	private static final int BOTTOM_BORDER = 2;
	
	/** Offset for the left border sprite */
	private static final int LEFT_BORDER = 3;
	
	/** Vertical scroll button */
	private static final int VERTICAL = 0;
	
	/** Horizontal scroll button */
	private static final int HORIZONTAL = 1;
	
	/** Jump to begin or end button */
	private static final int JUMP = 0;
	
	/** Click one step scroll button */
	private static final int CLICK = 1;
	
	/** Beginning of the scroll area */
	private static final int BEGIN = 0;
	
	/** Ending of the scroll area */
	private static final int END = 1;
	
	/** How much to move the scroll bar each click. Deterministic and maybe rough. In future, could use double-precision location and move by percentage */
	private static final int SCROLL_AMOUNT = 5;
	
	/** The 'mode' the vertical scroll bar will be in */
	private ScrollbarVisibility verticalVisibility;
	
	/** The 'mode' the horizontal scroll bar will be in */
	private ScrollbarVisibility horizontalVisibility;
	
	/** Whether or not to display the frame when neither of the scrollbars are showing */
	private boolean hideFrame;
	
	/** The element we're viewing */
	private Element view;
	
	/** The vertical scroll bar */
	private Optional<Scrollbar> verticalBar;
	
	/** The horizontal scroll bar */
	private Optional<Scrollbar> horizontalBar;
	
	/** The ID of the first sprite this scroll view is using, from the SCROLL set */
	private int firstSpriteID;
	
	/** Image for the top border */
	private BufferedImage topBorder;
	
	/** Image for the right border (both of them, same image) */
	private BufferedImage rightBorder;
	
	/** Image for the bottom border (both of them, same image) */
	private BufferedImage bottomBorder;
	
	/** Image for the left border */
	private BufferedImage leftBorder;
	
	/** The buttons for scrolling */
	private ImageButton scrollButtons[][][];
	
	/**
	 * Minimal constructor. 
	 * Create a new ScrollView which will have its top left at the given x, y location and have a final width and height as given. 
	 * The scroll bars will both always show.
	 * Image assets will still be default, and hiding the frame is false.
	 * @param view The element to show in the view
	 * @param x The x location
	 * @param y The y location
	 * @param width Total width of the view and the scroll bar
	 * @param height Total height of the view and the scroll bar
	 */
	public ScrollView(Element view, int x, int y, int width, int height)
	{
		this(view, x, y, width, height, ScrollbarVisibility.ALWAYS, ScrollbarVisibility.ALWAYS);
	}
	
	/**
	 * Nearly full specification constructor. 
	 * Create a new ScrollView which will have its top left at the given x, y location and have a final width and height as given. 
	 * The scroll bars will use the given modes. 
	 * Image assets will still be default, and hiding the frame is false.
	 * @param view The element to show in the view
	 * @param x The x location
	 * @param y The y location
	 * @param width Total width of the view and the scroll bar
	 * @param height Total height of the view and the scroll bar
	 * @param verticalMode Visibility mode for the vertical scroll bar
	 * @param horizontalMode Visibility mode for the horizontal scroll bar
	 */
	public ScrollView(Element view, int x, int y, int width, int height, ScrollbarVisibility verticalMode, ScrollbarVisibility horizontalMode)
	{
		super(PRIORITY, x, y, width, height);
		
		this.view = view;
		this.view.setParent(new ParentElement(this));
		this.verticalVisibility = verticalMode;
		this.horizontalVisibility = horizontalMode;
		
		this.hideFrame = false;
		
		this.firstSpriteID = 0;
		initBorders(0);
		initScrollButtons(18);
		assignButtonActions();
		
		// Scrollbars are created later, when they are needed, since at start they may not be and we do not know
		verticalBar = Optional.empty();
		horizontalBar = Optional.empty();
	}
	
	/**
	 * Initialize the border images. They are sized to fit around the outside boundaries of the scroll view
	 * @param firstSpriteID The ID of the first sprite in the set (the top border)
	 */
	private void initBorders(int firstSpriteID)
	{
		topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.SCROLL, firstSpriteID + TOP_BORDER, getWidth(), -1);
		rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.SCROLL, firstSpriteID + RIGHT_BORDER, -1, getHeight());
		bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.SCROLL, firstSpriteID + BOTTOM_BORDER, getWidth(), -1);
		leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.SCROLL, firstSpriteID + LEFT_BORDER, -1, getHeight());
	}
	
	/**
	 * Initialize the scroll buttons (and their images). The images are never resized, they are used as-is. 
	 * This relies on the borders being initialized, already
	 * @param firstSpriteID The ID of the first sprite in the set (the top border)
	 */
	private void initScrollButtons(int firstSpriteID)
	{
		// Sprite IDs we'll consistently refer back to
		final int firstJumpSpriteID = firstSpriteID;
		final int firstClickSpriteID = firstSpriteID + 3;
		
		// We'll also consistently need the width and height of the buttons
		final int buttonWidth = SpriteManager.instance().getSprite(SpriteSet.BUTTON, firstSpriteID).getWidth();
		final int buttonHeight = SpriteManager.instance().getSprite(SpriteSet.BUTTON, firstSpriteID).getHeight();
		
		// The vertical buttons are aligned on the x-axis and the horizontal on the y-axis
		final int vertXLoc = getWidth() - rightBorder.getWidth() - buttonWidth;
		final int horiYLoc = getHeight() - bottomBorder.getHeight() - buttonHeight;
		
		// Stored in a 3D array - Vertical/Horizontal, Jump/Click, Begin/End
		scrollButtons = new ImageButton[2][2][2];
		
		// Each button is given its own location and image
		scrollButtons[VERTICAL][JUMP][BEGIN] = createButton(
				vertXLoc,
				topBorder.getHeight(),
				firstJumpSpriteID, 0);
		scrollButtons[VERTICAL][JUMP][END] = createButton(
				vertXLoc,
				getHeight() - 2*bottomBorder.getHeight() - 2*buttonHeight,
				firstJumpSpriteID, 180);
		
		scrollButtons[VERTICAL][CLICK][BEGIN] = createButton(
				vertXLoc,
				topBorder.getHeight() + buttonHeight,
				firstClickSpriteID, 0);
		scrollButtons[VERTICAL][CLICK][END] = createButton(
				vertXLoc,
				getHeight() - 2*bottomBorder.getHeight() - 3*buttonHeight,
				firstClickSpriteID, 180);
		
		scrollButtons[HORIZONTAL][JUMP][BEGIN] = createButton(
				leftBorder.getWidth(),
				horiYLoc,
				firstJumpSpriteID, 270);
		scrollButtons[HORIZONTAL][JUMP][END] = createButton(
				getWidth() - 2*rightBorder.getWidth() - 2*buttonWidth,
				horiYLoc,
				firstJumpSpriteID, 90);
		
		scrollButtons[HORIZONTAL][CLICK][BEGIN] = createButton(
				leftBorder.getWidth() + buttonWidth,
				horiYLoc,
				firstClickSpriteID, 270);
		scrollButtons[HORIZONTAL][CLICK][END] = createButton(
				getWidth() - 2*rightBorder.getWidth() - 3*buttonWidth,
				horiYLoc,
				firstClickSpriteID, 90);
		
		// We also tell each button we're its parent
		for(int i = 0; i < scrollButtons.length; ++i)
		{
			for(int j = 0; j < scrollButtons[i].length; ++j)
			{
				for(int k = 0; k < scrollButtons[i][j].length; ++k)
				{
					scrollButtons[i][j][k].setParent(new ParentElement(this));
				}
			}
		}
	}
	
	/**
	 * Generic code for creating a scroll button
	 * @param x X location
	 * @param y Y location
	 * @param firstSpriteID Sprite for normal image
	 * @param rotation Degrees of rotation
	 * @return A newly created image button with the given parameters
	 */
	private ImageButton createButton(int x, int y, int firstSpriteID, int rotation)
	{
		return new ImageButton(
				x, y,
				SpriteManager.instance().getRotatedSprite(SpriteSet.BUTTON, firstSpriteID, rotation),
				SpriteManager.instance().getRotatedSprite(SpriteSet.BUTTON, firstSpriteID+1, rotation),
				SpriteManager.instance().getRotatedSprite(SpriteSet.BUTTON, firstSpriteID+2, rotation));
	}
	
	/**
	 * Set the action handler on each of the scroll buttons. 
	 * The scroll buttons must have been initialized before this is called. 
	 * The scroll bars may or may not be initialized, only the Optional needs to be
	 */
	private void assignButtonActions()
	{
		// Lambdas make this not look like a wretched mess!
		scrollButtons[VERTICAL][JUMP][BEGIN].setActionHandler((button) -> verticalBar.ifPresent((bar) -> bar.jumpBegin()));
		scrollButtons[VERTICAL][JUMP][END].setActionHandler((button) -> verticalBar.ifPresent((bar) -> bar.jumpEnd()));
		scrollButtons[VERTICAL][CLICK][BEGIN].setActionHandler((button) -> verticalBar.ifPresent((bar) -> bar.move(-SCROLL_AMOUNT)));
		scrollButtons[VERTICAL][CLICK][END].setActionHandler((button) -> verticalBar.ifPresent((bar) -> bar.move(SCROLL_AMOUNT)));
		scrollButtons[HORIZONTAL][JUMP][BEGIN].setActionHandler((button) -> horizontalBar.ifPresent((bar) -> bar.jumpBegin()));
		scrollButtons[HORIZONTAL][JUMP][END].setActionHandler((button) -> horizontalBar.ifPresent((bar) -> bar.jumpEnd()));
		scrollButtons[HORIZONTAL][CLICK][BEGIN].setActionHandler((button) -> horizontalBar.ifPresent((bar) -> bar.move(-SCROLL_AMOUNT)));
		scrollButtons[HORIZONTAL][CLICK][END].setActionHandler((button) -> horizontalBar.ifPresent((bar) -> bar.move(SCROLL_AMOUNT)));
	}

	/**
	 * Draw all of the scroll view. The border, scroll bars, scroll buttons, and wrapped element view are all drawn. 
	 * Much of the sizing happens here, so that it is as dynamic as possible and will adapt to changes in the 
	 * viewed element.
	 * @param g The graphics object to draw to
	 * @param offsetX How far off on the X axis this element is from the Graphic's origin
	 * @param offsetY How far off on the Y axis this element is from the Graphic's origin
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Have the borders drawn
		drawBorders(g, offsetX, offsetY);
		
		// Updates the existence of the vertical bar
		updateVerticalBarVisibility();
		
		// Updates the existence of the horizontal bar
		updateHorizontalBarVisibility();
		
		// Updates the size and position of both bars and related buttons
		updateBarDimensions();
		
		// Now that the scrollbars have been updated, we draw the bars if they're present. Vertical first.
		drawVerticalBar(g, offsetX, offsetY);

		// Horizontal's turn
		drawHorizontalBar(g, offsetX, offsetY);
		
		// Draw the view last - gives us the most recent scroll figures
		drawElementView(g, offsetX, offsetY);
	}

	/**
	 * Draw just the borders. These are the four borders that frame the scroll view.
	 * They are only drawn if they should be showing.
	 * @param g The graphics object to draw to
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 */
	private void drawBorders(Graphics g, int offsetX, int offsetY)
	{
		// Draw the borders, if there's a scroll bar or the frame isn't being hidden
		if(verticalBar.isPresent() || horizontalBar.isPresent() || !hideFrame)
		{
			g.drawImage(topBorder, offsetX, offsetY, null);
			g.drawImage(rightBorder, offsetX + getWidth() - rightBorder.getWidth(), offsetY, null);
			g.drawImage(bottomBorder, offsetX, offsetY + getHeight() - bottomBorder.getHeight(), null);
			g.drawImage(leftBorder, offsetX, offsetY, null);
		}
	}
	
	/**
	 * Updates the existence of the vertical scroll bar. 
	 * This will essentially create or destroy the scroll bar based on whether or not it should be showing.
	 */
	private void updateVerticalBarVisibility()
	{
		// Do we have a bar and no longer need it?
		if(verticalBar.isPresent() && !vScrollbarNeeded() && verticalVisibility == ScrollbarVisibility.WHEN_NEEDED)
		{
			// Removing and setting it to empty is like saying it no longer exists
			verticalBar.get().setParent(null);
			verticalBar = Optional.empty();
		}
		// Or do we not have it and need it now?
		else if(!verticalBar.isPresent() && vScrollbarNeeded())
		{
			// Create a scroll bar
			int trackBegin = scrollButtons[VERTICAL][CLICK][BEGIN].getY() + scrollButtons[VERTICAL][CLICK][BEGIN].getHeight();
			int trackEnd = scrollButtons[VERTICAL][CLICK][END].getY();
			Scrollbar scrollBar =  new Scrollbar(
					scrollButtons[VERTICAL][CLICK][BEGIN].getX(),
					scrollButtons[VERTICAL][CLICK][BEGIN].getY() + scrollButtons[VERTICAL][CLICK][BEGIN].getHeight(),
					VERTICAL, trackBegin, trackEnd, firstSpriteID+4);
			
			// We're going to be its parent
			scrollBar.setParent(new ParentElement(this));
			
			// Wrap it in an optional and assign it, this tells the view that the scrollbar exists
			verticalBar = Optional.of(scrollBar);
		}
	}
	
	/**
	 * Updates the existence of the horizontal scroll bar. 
	 * This will essentially create or destroy the scroll bar based on whether or not it should be showing.
	 */
	private void updateHorizontalBarVisibility()
	{
		// Do we still need the horizontal bar?
		if(horizontalBar.isPresent() && !hScrollbarNeeded() && horizontalVisibility == ScrollbarVisibility.WHEN_NEEDED)
		{
			// Removing and setting it to empty is like saying it no longer exists
			horizontalBar.get().setParent(null);
			horizontalBar = Optional.empty();
		}
		// Or do we not have the horizontal bar and need it now?
		else if(!horizontalBar.isPresent() && hScrollbarNeeded())
		{
			// Create a scroll bar
			int trackBegin = scrollButtons[HORIZONTAL][CLICK][BEGIN].getX() + scrollButtons[HORIZONTAL][CLICK][BEGIN].getWidth();
			int trackEnd = scrollButtons[HORIZONTAL][CLICK][END].getX();
			Scrollbar scrollBar =  new Scrollbar(
					scrollButtons[HORIZONTAL][CLICK][BEGIN].getX() + scrollButtons[HORIZONTAL][CLICK][BEGIN].getWidth(),
					scrollButtons[HORIZONTAL][CLICK][BEGIN].getY(),
					HORIZONTAL, trackBegin, trackEnd, firstSpriteID+4);

			// We're going to be its parent
			scrollBar.setParent(new ParentElement(this));

			// Wrap it in an optional and assign it, this tells the view that the scrollbar exists
			horizontalBar = Optional.of(scrollBar);
		}
	}
	
	/**
	 * Update the size and position of the scroll bars. This includes scroll buttons that are affected. 
	 * Depending on the current layout, the bars and buttons will be placed so that they utilize 
	 * as much space as possible.
	 */
	private void updateBarDimensions()
	{
		// Update the position and sizing of the scroll bars. First, are they both there?
		if(verticalBar.isPresent() && horizontalBar.isPresent())
		{
			// Both bars are present so they must each respect each other and... back off
			scrollButtons[VERTICAL][JUMP][END].setY(getHeight() - 2*bottomBorder.getHeight() - 2*scrollButtons[0][0][0].getHeight());
			scrollButtons[VERTICAL][CLICK][END].setY(scrollButtons[VERTICAL][JUMP][END].getY() - scrollButtons[0][0][0].getHeight());
			
			scrollButtons[HORIZONTAL][JUMP][END].setX(getWidth() - 2*rightBorder.getWidth() - 2*scrollButtons[0][0][0].getWidth());
			scrollButtons[HORIZONTAL][CLICK][END].setX(scrollButtons[HORIZONTAL][JUMP][END].getX() - scrollButtons[0][0][0].getWidth());
			
			// Both bars are going to be shown, so are they both the right length? Check on that
			// The bar length is the (scroll view length / element length) * scroll track length (i.e. percentage of stuff in view)
			int vBarLength = (int) ((scrollViewHeight() / (double) view.getHeight()) * verticalBar.get().getTrackLength());
			int hBarLength = (int) ((scrollViewWidth() / (double) view.getWidth()) * horizontalBar.get().getTrackLength());
			
			verticalBar.get().setLength(vBarLength);
			horizontalBar.get().setLength(hBarLength);
		}
		// It wasn't both, so was it just the vertical bar?
		else if(verticalBar.isPresent())
		{
			// Extend the vertical end buttons all the way down.
			// Note how horizontal aren't changed - there's no need. Same reason there is no "else neither"
			scrollButtons[VERTICAL][JUMP][END].setY(getHeight() - bottomBorder.getHeight() - scrollButtons[0][0][0].getHeight());
			scrollButtons[VERTICAL][CLICK][END].setY(scrollButtons[VERTICAL][JUMP][END].getY() - scrollButtons[0][0][0].getHeight());
			
			int vBarLength = (int) ((scrollViewHeight() / (double) view.getHeight()) * verticalBar.get().getTrackLength());
			
			verticalBar.get().setLength(vBarLength);
		}
		// It wasn't just the vertical bar, so is it just the horizontal bar?
		else if(horizontalBar.isPresent())
		{
			// Extend the end horizontal buttons all the way to the edge
			scrollButtons[HORIZONTAL][JUMP][END].setX(getWidth() - rightBorder.getWidth() - scrollButtons[0][0][0].getWidth());
			scrollButtons[HORIZONTAL][CLICK][END].setX(scrollButtons[HORIZONTAL][JUMP][END].getX() - scrollButtons[0][0][0].getWidth());
			
			int hBarLength = (int) ((scrollViewWidth() / (double) view.getWidth()) * horizontalBar.get().getTrackLength());
			
			horizontalBar.get().setLength(hBarLength);
		}
	}
	
	/**
	 * Draw the vertical scroll bar. This includes all of the buttons included in the bar.
	 * @param g The graphics object to draw to
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private void drawVerticalBar(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Of course we only draw it if it exists
		if(verticalBar.isPresent())
		{
			// The vertical bar is present. Tell it to draw.
			verticalBar.get().drawElement(g, offsetX + verticalBar.get().getX(), offsetY + verticalBar.get().getY());
			
			// We'll also need to draw the buttons that go with the vertical bar
			for(int j = 0; j < scrollButtons[VERTICAL].length; ++j)
			{
				for(int k = 0; k < scrollButtons[VERTICAL][j].length; ++k)
				{
					// Looks complicated but we're just having it draw itself and giving it the proper offset
					scrollButtons[VERTICAL][j][k].drawElement(g, offsetX + scrollButtons[VERTICAL][j][k].getX(), offsetY + scrollButtons[VERTICAL][j][k].getY());
				}
			}
			
			// Draw the right border again to the left of the vertical scrollbar
			g.drawImage(rightBorder, offsetX + getWidth() - 2*rightBorder.getWidth() - scrollButtons[0][0][0].getWidth(), offsetY, null);
		}
	}
	
	/**
	 * Draw the horizontal scroll bar. This includes all of the buttons included in the bar.
	 * @param g The graphics object to draw to
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private void drawHorizontalBar(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Of course we only draw it if it exists
		if(horizontalBar.isPresent())
		{
			// The vertical bar is present. Tell it to draw.
			horizontalBar.get().drawElement(g, offsetX + horizontalBar.get().getX(), offsetY + horizontalBar.get().getY());
			
			// We'll also need to draw the buttons that go with the vertical bar
			for(int j = 0; j < scrollButtons[HORIZONTAL].length; ++j)
			{
				for(int k = 0; k < scrollButtons[HORIZONTAL][j].length; ++k)
				{
					// Looks complicated but we're just having it draw itself and giving it the proper offset
					scrollButtons[HORIZONTAL][j][k].drawElement(g, offsetX + scrollButtons[HORIZONTAL][j][k].getX(), offsetY + scrollButtons[HORIZONTAL][j][k].getY());
				}
			}
			
			// Draw the right border again to the left of the vertical scrollbar
			g.drawImage(bottomBorder, offsetX, offsetY + getHeight() - 2*bottomBorder.getHeight() - scrollButtons[0][0][0].getHeight(), null);
		}
	}
	
	/**
	 * Draw the wrapped element. It is drawn so that it will draw its entire self, but in a shifted 
	 * way so that only the part within the scroll view will show. 
	 * @param g The graphics object to draw to
	 * @param offsetX Offset X
	 * @param offsetY Offset Y
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private void drawElementView(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// These will be how far we shift the view
		int drawOffsetX = 0;
		int drawOffsetY = 0;
		
		// Figure them out if there is a bar. It's (scroll percentage) * (max view offset)
		if(verticalBar.isPresent())
		{
			drawOffsetY = (int) (verticalBar.get().getScrollPercentage() * (view.getHeight() - scrollViewHeight()));
		}
		if(horizontalBar.isPresent())
		{
			drawOffsetX = (int) (horizontalBar.get().getScrollPercentage() * (view.getWidth() - scrollViewWidth()));
		}
		
		// We trick the view and tell it to draw shifted around, and clip the draw area to within this scroll view's area
		Shape prevClip = g.getClip();
		g.setClip(offsetX, offsetY, scrollViewWidth(), scrollViewHeight());
		view.drawElement(g, offsetX + view.getX() + leftBorder.getWidth() - drawOffsetX, offsetY + view.getY() + topBorder.getHeight() - drawOffsetY);
		g.setClip(prevClip);
	}
	
	/**
	 * A ScrollView is interested in key events which may tell it to scroll. 
	 * These are the SCROLL and ARROW key constants.
	 * @param key The key that was typed
	 */
	@Override
	public void keyTyped(char key) 
	{
		// A few keys are actually used for scrolling, including some spoofed scrolling characters.
		switch(key)
		{
		
		case KeyConstants.UP_ARROW:
		case KeyConstants.SCROLL_UP:
			// Tell the vertical bar to move, just as if the click begin button was pressed
			verticalBar.ifPresent((bar) -> bar.move(-SCROLL_AMOUNT));
			return;
			
		case KeyConstants.DOWN_ARROW:
		case KeyConstants.SCROLL_DOWN:
			// Tell the vertical bar to move, just as if the click end button was pressed
			verticalBar.ifPresent((bar) -> bar.move(SCROLL_AMOUNT));
			return;
			
		case KeyConstants.LEFT_ARROW:
		case KeyConstants.SCROLL_LEFT:
			// Tell the horizontal bar to move, just as if the click begin button was pressed
			horizontalBar.ifPresent((bar) -> bar.move(-SCROLL_AMOUNT));
			return;
			
		case KeyConstants.RIGHT_ARROW:
			case KeyConstants.SCROLL_RIGHT:
				// Tell the horizontal bar to move, just as if the click end button was pressed
				horizontalBar.ifPresent((bar) -> bar.move(SCROLL_AMOUNT));
				return;
		}
		
		// Nothing we're interested in, pass the event up
		super.keyTyped(key);
	}
	
	/**
	 * Determine the top element. This will take into consideration the scroll bars, buttons, and 
	 * viewed element. 
	 * @param point The point to look for
	 * @return The top element at the point
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	@Override
	public Element topElementAtPoint(Point point) throws IteratorException
	{
		// We really only have a few children of our own we need to check. We'll start with the scroll bars.
		Element top = topFromScrollBars(point);
		if(top != null)
		{
			return top;
		}
		
		// Next see if the vertical scroll buttons yield anything
		top = topFromVerticalBar(point);
		if(top != null)
		{
			return top;
		}

		// Then the horizontal scroll buttons
		top = topFromHorizontalBar(point);
		if(top != null)
		{
			return top;
		}
		
		// Well, there's also the element view. Is it within and over that?
		top = topFromView(point);
		if(top != null)
		{
			return top;
		}
		
		// If nothing else, it's on us
		return this;
	}

	/**
	 * Find the top element from the scroll bars. This will look at just the scroll bars, not the buttons. 
	 * @param point The point to look for
	 * @return The top element, or null if it is not from the bars
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private Element topFromScrollBars(Point point) throws IteratorException
	{
		// Check the vertical bar first. Is the scroll bar itself on the point?
		if(verticalBar.isPresent() && verticalBar.get().getRectangle().contains(point))
		{
			point.translate(-verticalBar.get().getX(), -verticalBar.get().getY());
			
			return verticalBar.get().topElementAtPoint(point);
		}
		
		// Maybe the horizontal bar is?
		if(horizontalBar.isPresent() && horizontalBar.get().getRectangle().contains(point))
		{
			point.translate(-horizontalBar.get().getX(), -horizontalBar.get().getY());
			
			return horizontalBar.get().topElementAtPoint(point);
		}
		
		// Neither of them were on the point, so return no result
		return null;
	}
	
	/**
	 * Find the top element from the vertical scroll buttons
	 * @param point The point to look for
	 * @return The top element, or null if it is not from the buttons
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private Element topFromVerticalBar(Point point) throws IteratorException
	{
		// Don't bother if there is no vertical bar
		if(!verticalBar.isPresent())
		{
			return null;
		}
		
		// Otherwise we'll check each of the vertical scroll buttons
		if(verticalBar.isPresent())
		{
			for(int j = 0; j < scrollButtons[VERTICAL].length; ++j)
			{
				for(int k = 0; k < scrollButtons[VERTICAL][j].length; ++k)
				{
					if(scrollButtons[VERTICAL][j][k].getRectangle().contains(point))
					{
						point.translate(-scrollButtons[VERTICAL][j][k].getX(), -scrollButtons[VERTICAL][j][k].getY());
						
						return scrollButtons[VERTICAL][j][k].topElementAtPoint(point);
					}
				}
			}
		}
		
		// Wasn't any of them
		return null;
	}

	/**
	 * Find the top element from the horizontal scroll buttons
	 * @param point The point to look for
	 * @return The top element, or null if it is not from the buttons
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private Element topFromHorizontalBar(Point point) throws IteratorException
	{
		// Don't bother if there is no horizontal bar
		if(!horizontalBar.isPresent())
		{
			return null;
		}
		
		// Otherwise we'll check each of the horizontal scroll buttons
		for(int j = 0; j < scrollButtons[HORIZONTAL].length; ++j)
		{
			for(int k = 0; k < scrollButtons[HORIZONTAL][j].length; ++k)
			{
				if(scrollButtons[HORIZONTAL][j][k].getRectangle().contains(point))
				{
					point.translate(-scrollButtons[HORIZONTAL][j][k].getX(), -scrollButtons[HORIZONTAL][j][k].getY());
					
					return scrollButtons[HORIZONTAL][j][k].topElementAtPoint(point);
				}
			}
		}
		
		// Wasn't any of them
		return null;
	}

	/**
	 * Find the top element from the wrapped element
	 * @param point The point to look for
	 * @return The top element, or null if it is not from the wrapped element
	 * @throws IteratorException If there was an issue iterating child elements
	 */
	private Element topFromView(Point point) throws IteratorException
	{
		// If the point is within the viewing area and view element, then it's up to that
		if(withinScrollView(point) && view.getRectangle().contains(point))
		{
			point.translate(-view.getX(), -view.getY());

			return view.topElementAtPoint(point);
		}
		
		// Nope, wasn't the wrapped element
		return null;
	}
	
	/**
	 * Sets the position rectangle, and also updates the size of the view so that 
	 * everything still appears correctly. 
	 * @param r The new location rectangle
	 */
	@Override
	public void setRectangle(Rectangle r)
	{
		// How much are the width and height changing?
		int widthDelta = r.width - super.getWidth();
		int heightDelta = r.height - super.getHeight();
		
		super.setRectangle(r);
		
		// Run through all of the resizing operations
		resizeForWidthChange(widthDelta);
		resizeForHeightChange(heightDelta);
	}
	
	/**
	 * Adjust the width of the scroll view and update the components 
	 * so that everything still appears correctly. 
	 * @param width The new width
	 */
	@Override
	public void setWidth(int width)
	{
		// How much is the width changing? Negative for getting smaller
		int widthDelta = width - super.getWidth();
		
		super.setWidth(width);
		
		// Only do the width resizing operations
		resizeForWidthChange(widthDelta);
	}
	
	/**
	 * Adjust the height of the scroll view and update the components 
	 * so that everything still appears correctly. 
	 * @param width The new height
	 */
	@Override
	public void setHeight(int height)
	{
		// How much is the height changing? Negative for getting smaller
		int heightDelta = height - super.getHeight();
				
		super.setHeight(height);
		
		// Only do the height resizing operations
		resizeForHeightChange(heightDelta);
	}
	
	/**
	 * Resize the necessary pieces to make things look right after the width 
	 * of the scroll view has changed.
	 * @param widthDelta How much the width has changed, negative for smaller
	 */
	private void resizeForWidthChange(int widthDelta)
	{
		// Resize the border images, their length may need to change
		resizeWidthBorders();
		
		// Reposition the buttons. Almost all depend on this, so we're not splitting hairs
		repositionScrollButtons(widthDelta, 0);
		
		// Reposition the vertical scroll bar
		verticalBar.ifPresent((bar) -> bar.setX(scrollButtons[VERTICAL][CLICK][BEGIN].getX()));
		
		// The horizontal bar will have its length adjusted during drawing, but we must define the new track size
		int trackBegin = scrollButtons[HORIZONTAL][CLICK][BEGIN].getX() + scrollButtons[HORIZONTAL][CLICK][BEGIN].getWidth();
		int trackEnd = scrollButtons[HORIZONTAL][CLICK][END].getX();
		horizontalBar.ifPresent((bar) -> bar.setTrack(trackBegin, trackEnd));
	}
	
	/**
	 * Resize the necessary pieces to make things look right after the height 
	 * of the scroll view has changed.
	 * @param heightDelta How much the height has changed, negative for smaller
	 */
	private void resizeForHeightChange(int heightDelta)
	{
		// Resize the border images, their length may need to change
		resizeHeightBorders();
		
		// Reposition the buttons. Almost all depend on this, so we're not splitting hairs
		repositionScrollButtons(0, heightDelta);
		
		// Reposition the horizontal scroll bar
		horizontalBar.ifPresent((bar) -> bar.setY(scrollButtons[HORIZONTAL][CLICK][BEGIN].getY()));
		
		// The vertical bar will have its length adjusted during drawing, but we must define the new track size
		int trackBegin = scrollButtons[VERTICAL][CLICK][BEGIN].getY() + scrollButtons[VERTICAL][CLICK][BEGIN].getHeight();
		int trackEnd = scrollButtons[VERTICAL][CLICK][END].getY();
		verticalBar.ifPresent((bar) -> bar.setTrack(trackBegin, trackEnd));
	}
	
	/**
	 * Adjust the position of the scroll buttons. This does not differentiate between a change in 
	 * width and a change in height - almost all of the buttons are adjusted anyways. 
	 * Those that would not be affected are not, and a 0 parameter will not change them. 
	 * @param widthDelta How much the width has changed, negative for smaller
	 * @param heightDelta How much the height has changed, negative for smaller
	 */
	private void repositionScrollButtons(int widthDelta, int heightDelta)
	{		
		// We can just adjust the existing location of each. Some just aren't going to change
		scrollButtons[VERTICAL][JUMP][BEGIN].setX(scrollButtons[VERTICAL][JUMP][BEGIN].getX() + widthDelta);
		
		scrollButtons[VERTICAL][JUMP][END].setX(scrollButtons[VERTICAL][JUMP][END].getX() + widthDelta);
		scrollButtons[VERTICAL][JUMP][END].setY(scrollButtons[VERTICAL][JUMP][END].getY() + heightDelta);
		
		scrollButtons[VERTICAL][CLICK][BEGIN].setX(scrollButtons[VERTICAL][CLICK][BEGIN].getX() + widthDelta);
		
		scrollButtons[VERTICAL][CLICK][END].setX(scrollButtons[VERTICAL][CLICK][END].getX() + widthDelta);
		scrollButtons[VERTICAL][CLICK][END].setY(scrollButtons[VERTICAL][CLICK][END].getY() + heightDelta);
		
		scrollButtons[HORIZONTAL][JUMP][BEGIN].setY(scrollButtons[HORIZONTAL][JUMP][BEGIN].getY() + heightDelta);
		
		scrollButtons[HORIZONTAL][JUMP][END].setX(scrollButtons[HORIZONTAL][JUMP][END].getX() + widthDelta);
		scrollButtons[HORIZONTAL][JUMP][END].setY(scrollButtons[HORIZONTAL][JUMP][END].getY() + heightDelta);
		
		scrollButtons[HORIZONTAL][CLICK][BEGIN].setY(scrollButtons[HORIZONTAL][CLICK][BEGIN].getY() + heightDelta);
		
		scrollButtons[HORIZONTAL][CLICK][END].setX(scrollButtons[HORIZONTAL][CLICK][END].getX() + widthDelta);
		scrollButtons[HORIZONTAL][CLICK][END].setY(scrollButtons[HORIZONTAL][CLICK][END].getY() + heightDelta);
	}
	
	/**
	 * Using the existing border images, adjust their length
	 */
	private void resizeWidthBorders()
	{
		topBorder = SpriteManager.instance().getScaledSprite(topBorder, getWidth(), -1);
		bottomBorder = SpriteManager.instance().getScaledSprite(bottomBorder, getWidth(), -1);
	}
	
	/**
	 * Using the existing border images, adjust their length
	 */
	private void resizeHeightBorders()
	{
		leftBorder = SpriteManager.instance().getScaledSprite(leftBorder, -1, getHeight());
		rightBorder = SpriteManager.instance().getScaledSprite(rightBorder, -1, getHeight());
	}
	
	/**
	 * Check to see if the given point is within the scroll view. 
	 * This is the total area that is shown within the scroll view, not just the wrapped element
	 * @param point The point to look into
	 * @return True if the point is within the viewing area
	 */
	private boolean withinScrollView(Point point)
	{
		return leftBorder.getWidth() < point.x
				&& point.x < leftBorder.getWidth() + scrollViewWidth()
				&& topBorder.getHeight() < point.y
				&& point.y < topBorder.getHeight() + scrollViewHeight();
	}
	
	/**
	 * Check to see if the vertical scroll bar is needed (i.e. the wrapped element is too wide 
	 * too be shown in the viewing area)
	 * @return True if the scroll bar is needed to navigate the wrapped element
	 */
	private boolean vScrollbarNeeded()
	{
		// Can we display all of the element in our full vertical space?
		return view.getHeight() > (getHeight() - (topBorder.getHeight() + bottomBorder.getHeight()));
	}
	
	/**
	 * Check to see if the horizontal scroll bar is needed (i.e. the wrapped element is too wide 
	 * too be shown in the viewing area)
	 * @return True if the scroll bar is needed to navigate the wrapped element
	 */
	private boolean hScrollbarNeeded()
	{
		// Can we display all of the element in our full horizontal space?
		return view.getWidth() > (getWidth() - (leftBorder.getWidth() + rightBorder.getWidth()));
	}
	
	/**
	 * @return The width of the viewing area
	 */
	private int scrollViewWidth()
	{
		return verticalBar.isPresent() ? getWidth() - 2*rightBorder.getWidth() - verticalBar.get().getWidth() : getWidth() - rightBorder.getWidth();
	}
	
	/**
	 * @return The height of the viewing area
	 */
	private int scrollViewHeight()
	{
		return horizontalBar.isPresent() ? getHeight() - 2*bottomBorder.getHeight() - horizontalBar.get().getHeight() : getHeight() - bottomBorder.getHeight();
	}
	
	/**
	 * The scroll bar that the ScrollView uses. It takes care of tracking its own position and being dragged, 
	 * as well as drawing itself. It will not take care of updating its size and such, the scroll view will 
	 * handle that (as the view has per-view of the environment)
	 */
	private class Scrollbar extends AbstractElement {
		
		/** Offset for the top border sprite ID */
		private static final int TOP_BORDER = 0;
		
		/** Offset for the top background sprite ID */
		private static final int TOP_BACKGROUND = 1;
		
		/** Offset for the center sprite ID */
		private static final int CENTER = 2;
		
		/** Offset for the bottom background sprite ID */
		private static final int BOTTOM_BACKGROUND = 3;
		
		/** Offset for the bottom border sprite ID */
		private static final int BOTTOM_BORDER = 4;
		
		/** Whether the scrollbar is horizontal or vertical */
		private final int orientation;
		
		/** Coordinate where the track begins. Orientation dependent */
		private int trackBegin;
		
		/** Coordinate where the track ends. Orientation dependent */
		private int trackEnd;
		
		/** The very top scroll bar sprite. Not meant to be resized. */
		private BufferedImage topBorder;
		
		/** Sprite for the stretch-able top portion of the scroll bar */
		private BufferedImage topBg;
		
		/** Center image, not meant to be resized */
		private BufferedImage centerImg;
		
		/** Sprite for the stretch-able bottom portion of the scroll bar */
		private BufferedImage bottomBg;
		
		/** The very bottom scroll bar sprite. Not meant to be resized. */
		private BufferedImage bottomBorder;
		
		/**
		 * Create a scroll bar at the given location, using the given orientation (which is either VERTICAL or HORIZONTAL)
		 * @param x X location
		 * @param y Y location
		 * @param orientation VERTICAL or HORIZONTAL, the ScrollView constant
		 * @param trackBegin The coordinate where the track begins. X or Y depending on orientation
		 * @param trackEnd The coordinate where the track ends. X or Y
		 * @param firstSpriteID The ID of the first scroll bar sprite to use in the SCROLL set (The top border for the scroll bar)
		 * @throws IllegalArgumentException If orientation is invalid
		 */
		public Scrollbar(int x, int y, int orientation, int trackBegin, int trackEnd, int firstSpriteID)
		{
			super(ScrollView.PRIORITY, x, y);
			
			// Because why not practice hygiene
			if(orientation != VERTICAL && orientation != HORIZONTAL)
			{
				throw new IllegalArgumentException("Orientation is not VERTICAL or HORIZONTAL");
			}
			
			this.orientation = orientation;
			
			setTrack(trackBegin, trackEnd);
			
			initSprites(firstSpriteID);
			
			// Our width and height will be based off of the images, to start
			if(orientation == VERTICAL)
			{
				super.setWidth(topBorder.getWidth());
				super.setHeight(topBorder.getHeight() + topBg.getHeight() + centerImg.getHeight() + bottomBg.getHeight() + bottomBorder.getHeight());
			}
			else {
				super.setWidth(topBorder.getWidth() + topBg.getWidth() + centerImg.getWidth() + bottomBg.getWidth() + bottomBorder.getWidth());
				super.setHeight(topBorder.getHeight());
			}
		}
		
		/**
		 * Initialize the images for this scroll bar. They will be rotated depending on the orientation of the bar, 
		 * and will be left their original size.
		 * @param firstSpriteID The ID of the scroll bar's top border image
		 */
		private void initSprites(int firstSpriteID)
		{
			// Depending on the orientation, we'll need rotation.
			if(orientation == VERTICAL)
			{
				// VERTICAL leaves us with the normal images
				topBorder = SpriteManager.instance().getSprite(SpriteSet.SCROLL, firstSpriteID + TOP_BORDER);
				topBg = SpriteManager.instance().getSprite(SpriteSet.SCROLL, firstSpriteID + TOP_BACKGROUND);
				centerImg = SpriteManager.instance().getSprite(SpriteSet.SCROLL, firstSpriteID + CENTER);
				bottomBg = SpriteManager.instance().getSprite(SpriteSet.SCROLL, firstSpriteID + BOTTOM_BACKGROUND);
				bottomBorder = SpriteManager.instance().getSprite(SpriteSet.SCROLL, firstSpriteID + BOTTOM_BORDER);
			}
			// If it's not vertical, it must be horizontal
			else {
				// HORIZONTAL leaves us with images rotated 90 degrees
				topBorder = SpriteManager.instance().getRotatedSprite(SpriteSet.SCROLL, firstSpriteID + TOP_BORDER, 90);
				topBg = SpriteManager.instance().getRotatedSprite(SpriteSet.SCROLL, firstSpriteID + TOP_BACKGROUND, 90);
				centerImg = SpriteManager.instance().getRotatedSprite(SpriteSet.SCROLL, firstSpriteID + CENTER, 90);
				bottomBg = SpriteManager.instance().getRotatedSprite(SpriteSet.SCROLL, firstSpriteID + BOTTOM_BACKGROUND, 90);
				bottomBorder = SpriteManager.instance().getRotatedSprite(SpriteSet.SCROLL, firstSpriteID + BOTTOM_BORDER, 90);
			}
		}

		/**
		 * Draw the scroll bar, by combining all of the images
		 * @param g The graphics object to draw to
		 * @param offsetX How far off on the X axis this element is from the Graphic's origin
		 * @param offsetY How far off on the Y axis this element is from the Graphic's origin
		 * @throws IteratorException If there was an issue iterating child elements
		 */
		@Override
		public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
		{
			// Draw is slightly different based on orientation
			if(orientation == VERTICAL)
			{
				g.drawImage(topBorder, offsetX, offsetY, null);
				g.drawImage(topBg, offsetX, offsetY + topBorder.getHeight(), null);
				g.drawImage(centerImg, offsetX, offsetY + topBorder.getHeight() + topBg.getHeight(), null);
				g.drawImage(bottomBg, offsetX, offsetY + topBorder.getHeight() + topBg.getHeight() + centerImg.getHeight(), null);
				g.drawImage(bottomBorder, offsetX, offsetY + topBorder.getHeight() + topBg.getHeight() + centerImg.getHeight() + bottomBg.getHeight(), null);
			}
			else {
				g.drawImage(topBorder, offsetX, offsetY, null);
				g.drawImage(topBg, offsetX + topBorder.getWidth(), offsetY, null);
				g.drawImage(centerImg, offsetX + topBorder.getWidth() + topBg.getWidth(), offsetY, null);
				g.drawImage(bottomBg, offsetX + topBorder.getWidth() + topBg.getWidth() + centerImg.getWidth(), offsetY, null);
				g.drawImage(bottomBorder, offsetX + topBorder.getWidth() + topBg.getWidth() + centerImg.getWidth() + bottomBg.getWidth(), offsetY, null);
			}
			
		}
		
		/**
		 * Handle a drag event by moving the scroll bar. The bar will only move within its track
		 * @param dragged The element that was dragged. It may not be this element!
		 * @param start Where the dragging started. I.e. where the mouse was first pressed down
		 * @param withinStart The point within the element dragging started. Like with the other mouse events.
		 * @param current Where the mouse is currently at. Can be used to find a movement delta or used directly.
		 */
		@Override
		public void dragged(Element dragged, Point start, Point withinStart, Point current)
		{
			if(orientation == VERTICAL)
			{
				// Figure out the destination Y coordinate
				int destY = current.y - withinStart.y;
				destY = destY < trackBegin ? trackBegin : destY;
				destY = destY > trackEnd - getHeight() ? trackEnd - getHeight() : destY;
				
				// Update our location
				setY(destY);
			}
			else {
				// Figure out the destination X coordinate
				int destX = current.x - withinStart.x;
				destX = destX < trackBegin ? trackBegin : destX;
				destX = destX > trackEnd - getWidth() ? trackEnd - getWidth(): destX;
				
				// Update our location
				setX(destX);
			}
		}
		
		/**
		 * Moves the bar to the beginning of its track
		 */
		public void jumpBegin()
		{
			// Depending on the orientation, set the correct position variable to the beginning of the track
			if(orientation == VERTICAL)
			{
				setY(trackBegin);
			}
			else {
				setX(trackBegin);
			}
		}
		
		/**
		 * Moves the bar to the end of its track
		 */
		public void jumpEnd()
		{
			// Depending on the orientation, set the correct position variable to the end of the track
			if(orientation == VERTICAL)
			{
				setY(trackEnd - getHeight());
			}
			else {
				setX(trackEnd - getWidth());
			}
		}
		
		/**
		 * Moves the bar within its track by the given amount. A negative amount will move it towards the beginning, 
		 * while a positive amount will move it towards the end. The bar will stay within its track.
		 * @param delta The amount to move the bar
		 */
		public void move(int delta)
		{
			// Make sure that the movement won't run us off the track
			if(orientation == VERTICAL)
			{
				// Figure out the destination Y coordinate
				int destY = getY() + delta;
				destY = destY < trackBegin ? trackBegin : destY;
				destY = destY > trackEnd - getHeight() ? trackEnd - getHeight() : destY;
				
				// Update our location
				setY(destY);
			}
			else {
				// Figure out the destination X coordinate
				int destX = getX() + delta;
				destX = destX < trackBegin ? trackBegin : destX;
				destX = destX > trackEnd - getWidth() ? trackEnd - getWidth(): destX;
				
				// Update our location
				setX(destX);
			}
		}
		
		/**
		 * Obtain the percentage of the way the scroll bar has been moved along its track. 
		 * This is a value on the range [0, 1] measured from the beginning of the scroll bar
		 * @return How far along the track the bar is
		 */
		public double getScrollPercentage()
		{
			// What's the distance range we can travel? Slightly different from track length
			double scrollLength = getTrackLength() - (orientation == VERTICAL ? getHeight() : getWidth());
			
			return ((orientation == VERTICAL ? getY() : getX()) - trackBegin) / scrollLength;
		}
		
		/**
		 * @return How long the scroll bar is in total
		 */
		public int getLength()
		{
			if(orientation == VERTICAL)
			{
				return getHeight();
			}
			else {
				return getWidth();
			}
		}
		
		/**
		 * Adjust the total length of the scroll bar. This will take into consideration the minimum possible size. 
		 * It cannot be made smaller than that. Nothing will happen if the provided length is already the current length. 
		 * The scroll bar will use its background images to make itself longer, the other images will remain the same size. 
		 * @param length The combined length the scroll bar should be
		 */
		public void setLength(int length)
		{
			// Avoid doing anything if we're the right length
			if(getLength() == length)
			{
				return;
			}
			
			// We need to make our scroll bar the given length, ideally. 
			if(orientation == VERTICAL)
			{
				// So we're going to change the height. The minimum is the size of those that cannot resize, and 1 for the backgrounds
				int minSize = topBorder.getHeight() + 1 + centerImg.getHeight() + 1 + bottomBorder.getHeight();
				
				// Restrict length to no less than the minimum size and no more than the track length
				length = (length < minSize) ? minSize : length;
				length = (length > getTrackLength()) ? getTrackLength() : length;
				
				// So how tall do the backgrounds need to be now? They'll split the remaining length after the other parts
				int topBgHeight = (int) Math.ceil((length - minSize + 2) / 2.0);
				int bottomBgHeight = (int) Math.floor((length - minSize + 2) / 2.0);
				
				// Obtain some newly sized images for the backgrounds
				topBg = SpriteManager.instance().getTiledSprite(topBg, -1, topBgHeight);
				bottomBg = SpriteManager.instance().getTiledSprite(bottomBg, -1, bottomBgHeight);
				
				// Update the height. It's probably different now
				setHeight(topBorder.getHeight() + topBg.getHeight() + centerImg.getHeight() + bottomBg.getHeight() + bottomBorder.getHeight());
			}
			else {
				// So we're going to change the width. The minimum is the size of those that cannot resize, and 1 for the backgrounds
				int minSize = topBorder.getWidth() + 1 + centerImg.getWidth() + 1 + bottomBorder.getWidth();
				
				// Restrict length to no less than the minimum size
				length = (length < minSize) ? minSize : length;
				length = (length > getTrackLength()) ? getTrackLength() : length;
				
				// So how wide do the backgrounds need to be now? They'll split the remaining length after the other parts
				int topBgWidth = (int) Math.ceil((length - minSize + 2) / 2.0);
				int bottomBgWidth = (int) Math.floor((length - minSize + 2) / 2.0);
				
				// Obtain some newly sized images for the backgrounds
				topBg = SpriteManager.instance().getTiledSprite(topBg, topBgWidth, -1);
				bottomBg = SpriteManager.instance().getTiledSprite(bottomBg, bottomBgWidth, -1);
				
				// Update the width, it's probably different now
				super.setWidth(topBorder.getWidth() + topBg.getWidth() + centerImg.getWidth() + bottomBg.getWidth() + bottomBorder.getWidth());
			}
		}
		
		/**
		 * @return How much track the bar has to move within (end - begin)
		 */
		public int getTrackLength()
		{
			return trackEnd - trackBegin;
		}
		
		/**
		 * Set the track for this bar. This defines where the bar may move to. The bar will not move 
		 * outside of this track. These are coordinates relative to the same coordinate space the bar is in. 
		 * The beginning marks where the top of the bar may reach. 
		 * The end marks where the bottom of the bar may reach.
		 * @param begin The top or left-most the bar can be
		 * @param end The bottom or right-most the bar can be
		 */
		public void setTrack(int begin, int end)
		{
			this.trackBegin = begin;
			this.trackEnd = end;
		}
		
	}

}
