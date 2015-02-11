package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.cloud.client.event.task.TaskEngine;
import net.cloud.client.event.task.voidtasks.CancellableVoidTask;
import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.constants.Fonts;
import net.cloud.gfx.constants.KeyConstants;
import net.cloud.gfx.constants.Priority;
import net.cloud.gfx.sprites.SpriteManager;
import net.cloud.gfx.sprites.SpriteSet;

/**
 * A generic text field. Text input is accepted. Tabs will still move focus. The text is drawn to the screen, 
 * showing as much as possible but keeping a limited view of within the field. A cursor shows the current location 
 * in the field, and if it is clicked the cursor location will change. Pressing Enter will cause a defined 
 * action to be taken. 
 */
public class TextField extends Element {
	
	/** Default priority of a TextField. Moderate. */
	public static final int PRIORITY = Priority.MED;

	/** Delay between blinks of the cursor, in milliseconds */
	private static final int BLINK_INTERVAL = 750;
	
	/** Text that is already in the field before it takes focus. Then it gets wiped away. May just be null. */
	private String hintText;
	
	/** Builder object used just for modifying the text in the field */
	private StringBuilder textBuilder;
	
	/** The string for all of the text in the field. Currently StringBuilder did not offer improvements */
	private String text;
	
	/** Holds the text that should really be drawn. Maybe all of the text doesn't fit. */
	protected DisplayableText displayText;
	
	/** The font currently being used by the field */
	private Font font;
	
	/** Color of the hint text. Usually slightly lighter than the actually text. */
	private Color hintTextColor;
	
	/** Color of the input text. */
	private Color textColor;
	
	/** Has all the information for drawing the text. Supposed to be the fastest method. */
	private GlyphVector glyphVector;
	
	/** A cursor to show where text input will be at. */
	private Cursor cursor;
	
	/** A task to make the cursor blink when the field has focus */
	private CursorBlinkTask blinkTask;
	
	/** A method called when the text field is acted on. Of course optionally provided. */
	private Optional<BiConsumer<TextField, String>> actionMethod;
	
	/** Relative x location of a click, indicating the cursor should be moved. -1 for no outstanding click */
	private int clickX;
	
	/** Background - behind the text area itself */
	private BufferedImage bgImg;
	
	/** Border framing the top of the field. Aligned to width of field. */
	private BufferedImage topBorder;
	
	/** Border framing the right of the field. Stretched to fit over top and bottom borders, too */
	private BufferedImage rightBorder;
	
	/** Border framing the bottom of the field. Aligned to width of field. */
	private BufferedImage bottomBorder;
	
	/** Border framing the left of the field. Stretched to fit over top and bottom borders, too */
	private BufferedImage leftBorder;
	
	/** 
	 * Creates a default text field with almost everything default. It will be at the given location. 
	 * The width and height will be suitable for the default font. (100 by 22). 
	 * There will be no hint text, and the text will be black. 
	 * The sprites will be the default, as in the first set of text field sprites. 
	 * @param x The x location of the text area itself - not counting the borders
	 * @param y The y location of the text area itself - not counting the borders
	 */
	public TextField(int x, int y)
	{
		super(null, PRIORITY, x, y, 100, 22);
		
		// No hint text, so just create plain empty text
		this.textBuilder = new StringBuilder("");
		this.text = textBuilder.toString();
		
		// And the rest of initialization 
		commonInit();
		
		// ... Except the sprite stuff
		defaultSpriteInit();
	}
	
	/** 
	 * Creates a default text field with almost everything default. It will be at the given location. 
	 * The hint text will be gray, the input text will be black. 
	 * The sprites will be the default, as in the first set of text field sprites. 
	 * @param x The x location of the text area itself - not counting the borders
	 * @param y The y location of the text area itself - not counting the borders
	 * @param width The width of the field. 100 is the default. 
	 * @param height The height of the field. 22 is the default. 
	 * @param hintText Text to show in the field as an input hint. May be null for none. 
	 */
	public TextField(int x, int y, int width, int height, String hintText)
	{
		super(null, PRIORITY, x, y, width, height);
		
		this.hintText = hintText;
		
		// Hint text may be null, take care of both situations
		if(hintText == null)
		{
			this.textBuilder = new StringBuilder("");
			this.text = textBuilder.toString();
		} else {
			this.textBuilder = new StringBuilder(hintText);
			this.text = hintText;
		}
		
		// And the rest of initialization
		commonInit();
		
		// ... Except the sprite stuff
		defaultSpriteInit();
	}
	
	/** 
	 * Creates a default text field with the a good amount of constructor customization. 
	 * What is not available here is available through setter methods. 
	 * The hint text will be gray, the input text will be black.  
	 * @param x The x location of the text area itself - not counting the borders
	 * @param y The y location of the text area itself - not counting the borders
	 * @param width The width of the field. 100 is the default. 
	 * @param height The height of the field. 22 is the default. 
	 * @param hintText Text to show in the field as an input hint. May be null for none. 
	 * @param firstSpriteID The ID of the first sprite in the set of text field sprites
	 */
	public TextField(int x, int y, int width, int height, String hintText, int firstSpriteID)
	{
		super(null, PRIORITY, x, y, width, height);
		
		this.hintText = hintText;
		
		// Hint text may be null, take care of both situations
		if(hintText == null)
		{
			this.textBuilder = new StringBuilder("");
			this.text = textBuilder.toString();
		} else {
			this.textBuilder = new StringBuilder(hintText);
			this.text = hintText;
		}
		
		// And the rest of initialization
		commonInit();
		
		// ... Except sprite stuff.
		this.bgImg = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstSpriteID, getWidth(), getHeight());
		this.topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstSpriteID+1, getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstSpriteID+3, getWidth(), -1);
		this.rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstSpriteID+2, -1, 
				getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
		this.leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstSpriteID+4, -1, 
				getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}
	
	/**
	 * Common initialization. Takes care of a lot of things, to avoid repeating them in constructors. 
	 */
	private void commonInit()
	{
		// Start with the default font - can be set via setFont() later
		this.font = Fonts.DEFAULT.get();
		
		// Both default text colors - again can set through setter methods later
		this.textColor = Colors.BLACK.get();
		this.hintTextColor = Colors.GRAY.get();
		
		// Null to mark it as dirty. Must be recreated on text changes and via a Graphics2D
		this.glyphVector = null;
		
		// The cursor starts off at the very beginning not blinking or showing
		this.cursor = new Cursor();
		this.blinkTask = null;
		
		// The displayed text to begin with is nothing
		this.displayText = new DisplayableText();
		
		// Start off with no handler for input action
		actionMethod = Optional.empty();
		
		// No pending click to move the cursor
		this.clickX = -1;
	}
	
	/**
	 * Initialize the text field sprites to the default set (ID 1 - 5)
	 */
	private void defaultSpriteInit()
	{
		// All of the default sprites. May be set individually or as a group later. 
		this.bgImg = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 1, getWidth(), getHeight());
		this.topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 2, getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 4, getWidth(), -1);
		this.rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 3, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
		this.leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 5, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}
	
	/**
	 * Re-initialize the sprites, using the current sprite set
	 */
	private void reInitSprites()
	{
		// Use the existing sprite instead of getting from a sprite set
		this.bgImg = SpriteManager.instance().getScaledSprite(bgImg, getWidth(), getHeight());
		this.topBorder = SpriteManager.instance().getScaledSprite(topBorder, getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(bottomBorder, getWidth(), -1);
		this.rightBorder = SpriteManager.instance().getScaledSprite(rightBorder, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
		this.leftBorder = SpriteManager.instance().getScaledSprite(leftBorder, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}

	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException 
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setFont(font);
		
		// Check if we need to recreate a glyph vector
		updateGlyphVector(g2d);
		
		// Check if we need to move the cursor as per a click request
		if(clickX != -1)
		{
			moveCursorForClick(g2d);
		}
		
		// Draw all of the image assets that make up the background
		g2d.drawImage(bgImg, offsetX, offsetY, null);
		g2d.drawImage(topBorder, offsetX, offsetY - topBorder.getHeight(), null);
		g2d.drawImage(rightBorder, offsetX + getWidth(), offsetY - topBorder.getHeight(), null);
		g2d.drawImage(bottomBorder, offsetX, offsetY + getHeight(), null);
		g2d.drawImage(leftBorder, offsetX - leftBorder.getWidth(), offsetY - topBorder.getHeight(), null);
		
		// Make sure we've got the right text color
		g2d.setColor(hintText == null ? textColor : hintTextColor);
		
		// Draw text to the screen
		FontMetrics metrics = g2d.getFontMetrics();
		int maxTextHeight = metrics.getAscent() + metrics.getDescent();
		g2d.drawGlyphVector(glyphVector, offsetX + 3, offsetY + (maxTextHeight / 2) + (getHeight() / 2) - 3);
		
		// Draw the cursor on top of the text (when this field has focus)
		if(getFocusHandler().hasFocus())
		{
			cursor.draw(g2d, offsetX, offsetY);
		}
		
	}
	
	@Override
	public void clicked(Point relPoint, boolean isRightClick)
	{
		// Superclass takes care of focus handling
		super.clicked(relPoint, isRightClick);
		
		// A click happened so we'll update the variable to indicate interest in moving the cursor. Happens on re-draw.
		clickX = relPoint.x;
	}
	
	@Override
	public void keyTyped(char key) 
	{
		// Take care of the characters that have special effects
		switch(key)
		{
		
		case KeyConstants.CHANGE_FOCUS_PREVIOUS:
			super.getFocusHandler().traversePrevious();
			return;
		case KeyConstants.CHANGE_FOCUS_NEXT:
			super.getFocusHandler().traverseNext();
			return;
			
		case KeyConstants.BACKSPACE:
			// Remove one character back
			backspace();
			return;
		case KeyConstants.DELETE:
			// Remove one character forward
			delete();
			return;
			
		case KeyConstants.KILL_TEXT:
			// Delete everything from start until cursor
			clearBeforeCursor();
			return;
			
		case KeyConstants.ACTION_KEY:
			// Call the "action handler" with the text field instance and current text
			actionMethod.ifPresent((m) -> m.accept(this, text));
			return;
			
		case KeyConstants.LEFT_ARROW:
			// Try to move the cursor left
			tryCursorLeft();
			return;
		case KeyConstants.RIGHT_ARROW:
			// Try to move the cursor right
			tryCursorRight();
			return;
		}
		
		// Treat the rest as plain characters getting typed into the field
		addChar(key);
		
		// No call to super - a text field intentionally consumes all key events
	}
	
	/**
	 * Calls <code>super.focusGained()</code>. 
	 * Also creates and starts a task to make the cursor blink. 
	 */
	@Override
	public void focusGained()
	{
		super.focusGained();
		
		// Create a new task and submit it
		blinkTask = new CursorBlinkTask();
		TaskEngine.getInstance().scheduleImmediate(blinkTask, BLINK_INTERVAL);
		
		// If there is still hint text, clear that out
		if(hintText != null)
		{
			setText("");
			hintText = null;
		}
	}
	
	/**
	 * Calls <code>super.focusLost()</code>. 
	 * Also cancels the task to make the cursor blink. 
	 */
	@Override
	public void focusLost()
	{
		super.focusLost();
		
		// Cancel and remove reference to the task
		blinkTask.cancel();
		blinkTask = null;
	}
	
	/**
	 * Check to see if the glyph vector is invalid, and if so, update it. 
	 * The display text is updated as well so that the most current text is shown
	 * @param g2d Graphics object currently being used to draw with
	 */
	private void updateGlyphVector(Graphics2D g2d)
	{
		if(glyphVector == null)
		{
			// Figure out what text should be displayed now
			displayText.updateDisplay(g2d);
			
			// The font, current graphics, and text determine the glyphs
			glyphVector = font.createGlyphVector(g2d.getFontRenderContext(), displayText.displayed);
		}
	}

	/**
	 * Set the width of the field. This will cause resizing of the sprites and invalidation of the display. 
	 * Suggested to set this at construction time and not change it. 
	 * @param width The new width of the text area itself
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		// Scale the sprites again
		reInitSprites();
		
		glyphVector = null;
	}
	
	/**
	 * Set the height of the field. This will cause resizing of sprites. 
	 * Suggested to set this at construction time and not change it. 
	 * @param height The new height of the text area itself
	 */
	@Override
	public void setHeight(int height)
	{
		super.setHeight(height);
		
		// Scale the sprites again
		reInitSprites();
		
		glyphVector = null;
	}
	
	/**
	 * @return All of the text in the field
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Assign new text to the field. Resets the display and cursor position. 
	 * @param text The new text that will be in the field
	 */
	public void setText(String text)
	{
		this.text = text;
		this.textBuilder = new StringBuilder(text);
		
		// Have to also put the displayed text and cursor back to default
		displayText.reset();
		cursor.move(0);
		
		// And invalidate the glyph vector so new text is drawn
		glyphVector = null;
	}
	
	/**
	 * Remove all text from the field. A shortcut to <code>setText("");</code>
	 */
	public void clearText()
	{
		setText("");
	}
	
	/** @return The font being used to draw the text */
	public Font getFont()
	{
		return font;
	}
	
	/**
	 * @param font The new font to use for the input text
	 */
	public void setFont(Font font)
	{
		this.font = font;
		
		// Invalidate glyph vector so text will be drawn in new font next time
		glyphVector = null;
	}
	
	/** @return The current color of the input text */
	public Color getTextColor()
	{
		return textColor;
	}
	
	/** @param textColor The new color for the input text */
	public void setTextColor(Color textColor)
	{
		this.textColor = textColor;
	}
	
	/** @return The current color of the hint text */
	public Color getHintTextColor()
	{
		return hintTextColor;
	}
	
	/** @param hintTextColor The new color for the hint text */
	public void setHintTextColor(Color hintTextColor)
	{
		this.hintTextColor = hintTextColor;
	}
	
	/**
	 * Assign an action handler to this TextField. It will be called whenever 'action' is taken on the text field 
	 * (such as pressing enter.) This will overwrite an existing handler, and may be null to remove an existing handler. 
	 * @param action The method to call when action is taken on the field
	 */
	public void setActionHandler(BiConsumer<TextField, String> action)
	{
		this.actionMethod = Optional.ofNullable(action);
	}
	
	/**
	 * Sets all of the text field sprites to the given grouping. In the sprites resources, the text field sprites are stored 
	 * in their own folder. Sprite 0 is the cursor. After that, the sprites will repeat background, top, right, bottom, left border. 
	 * The provided ID should correspond to the background image. 
	 * @param firstId The ID of the background image in the text field set
	 */
	public void setSpriteGroup(int firstId)
	{
		// Set each of the sprites anew
		this.bgImg = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstId, getWidth(), getHeight());
		this.topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstId+1, getWidth(), -1);
		this.bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstId+3, getWidth(), -1);
		this.rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstId+2, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
		this.leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, firstId+4, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}
	
	/**
	 * Set the background image used for the text field. It is scaled to the correct size. 
	 * @param id The sprite ID of the background, in the text field set
	 */
	public void setBgSprite(int id)
	{
		this.bgImg = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 1, getWidth(), getHeight());
	}
	
	/**
	 * Set the top border image used around the text field. Scaled to the correct size.
	 * @param id The sprite ID of the top border, in the text field set
	 */
	public void setTopSprite(int id)
	{
		this.topBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 2, getWidth(), -1);
	}
	
	/**
	 * Set the bottom border image used around the text field. Scaled to the correct size.
	 * @param id The sprite ID of the bottom border, in the text field set
	 */
	public void setBottomSprite(int id)
	{
		this.bottomBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 4, getWidth(), -1);
	}
	
	/**
	 * Set the right side border image used around the text field. Scaled to the correct size. 
	 * The scaling depends on the top and bottom borders, so they should be set before the sides if setting both. 
	 * @param id The sprite ID of the right side border, in the text field set
	 */
	public void setRightSprite(int id)
	{
		this.rightBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 3, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}
	
	/**
	 * Set the left side border image used around the text field. Scaled to the correct size. 
	 * The scaling depends on the top and bottom borders, so they should be set before the sides if setting both. 
	 * @param id The sprite ID of the left side border, in the text field set
	 */
	public void setLeftSprite(int id)
	{
		this.leftBorder = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 5, -1, getHeight() + topBorder.getHeight() + bottomBorder.getHeight());
	}
	
	/**
	 * Check to see if the field is currently showing hint text
	 * @return True if the hint text is up
	 */
	public boolean showingHint()
	{
		// Roughly, when removed it is nullified
		return hintText != null;
	}
	
	/**
	 * Move the cursor so it reflects the location of a click request. 
	 * Should only be called when their is a click coordinate and the glyph vector is valid
	 */
	private void moveCursorForClick(Graphics2D g2d)
	{
		FontMetrics metrics = g2d.getFontMetrics();
		
		// Start point of the click range for a character
		int startPoint = 0;
		
		// Check all proper substrings (including empty string)
		for(int i = 0; i < displayText.displayed.length(); ++i)
		{
			// Measure width of substring to start
			int substringWidth = metrics.stringWidth(displayText.displayed.substring(0, i));
			
			// Add half the width of the next character, to give bias
			substringWidth += metrics.charWidth(displayText.displayed.charAt(i)) / 2;
			
			// Now see if the click point falls within the current range
			if(startPoint <= clickX && clickX <= substringWidth)
			{
				// Yay, found the index. Move the cursor there
				cursor.move(displayText.leftIdx + i);
				
				// Don't forget to reset the click point before calling it done
				clickX = -1;
				
				return;
			}
			
			// Well, update the variables for the next go-round
			startPoint = substringWidth;
		}
		
		// Not finding it means it'll be at the very end
		cursor.move(displayText.leftIdx + displayText.displayed.length());
		
		// Reset the click point before returning
		clickX = -1;
	}
	
	/**
	 * Add the character to the field, at the current cursor position. 
	 * May update the displayed text and invalidates the glyph vector
	 * @param c The character to add
	 */
	private void addChar(char c)
	{
		// Insert right at the cursor index
		textBuilder.insert(cursor.cursorIdx, c);
		text = textBuilder.toString();

		// Cursor at left edge of visible
		if(cursor.cursorIdx <= displayText.leftIdx)
		{
			displayText.hintLeft = true;
		} 
		else {
			displayText.rightIdx++;
			displayText.hintLeft = false;
		}
		
		// The change invalidates the glyphs
		glyphVector = null;
		
		// As a result, the cursor moves forward
		cursor.moveRight();
	}
	
	/**
	 * Removes a single character from the field. It is removed from behind the 
	 * cursor. May update the displayed text and invalidates the glyph vector
	 */
	private void backspace()
	{
		// Make sure there is even a character to delete
		if(cursor.cursorIdx <= 0)
		{
			return;
		}
		
		// Remove behind the cursor, because backspace
		textBuilder.deleteCharAt(cursor.cursorIdx - 1);
		text = textBuilder.toString();
		
		// Update the amount to display. Same either way
		displayText.rightIdx--;
		displayText.hintLeft = false;
		
		// The change invalidates the glyphs
		glyphVector = null;
		
		// As a result, the cursor moves backward
		cursor.moveLeft();
	}
	
	/**
	 * Removes a single character from the field. It is removed from in front of the 
	 * cursor. May update the displayed text and invalidates the glyph vector
	 */
	private void delete()
	{
		// Make sure there is even a character to delete
		if(cursor.cursorIdx >= text.length())
		{
			return;
		}

		// Remove in front of the cursor, because delete
		textBuilder.deleteCharAt(cursor.cursorIdx);
		text = textBuilder.toString();

		// Always work from the left, but only change bounds when removing from the end
		displayText.hintLeft = true;
		if(cursor.cursorIdx <= displayText.rightIdx)
		{
			displayText.rightIdx--;
		}
		
		// The change invalidates the glyphs
		glyphVector = null;
	}
	
	/**
	 * Removes all of the text before the cursor. 
	 * Due to the bulk nature of this, the display will be totally reset and allowed to determine 
	 * what to show from the start again. 
	 */
	private void clearBeforeCursor()
	{
		// Remove everything before the cursor, doesn't matter where cursor is
		textBuilder.delete(0, cursor.cursorIdx);
		text = textBuilder.toString();
		
		cursor.move(0);
		
		// Reset the display, back to starting at 0 from the left
		displayText.reset();
		
		// Of course, invalidates the glyph vector
		glyphVector = null;
	}
	
	/**
	 * Try to move the cursor left if possible. 
	 * Then, if necessary, the display is modified so that the cursor remains in view. 
	 */
	private void tryCursorLeft() {
		if(cursor.moveLeft())
		{
			// Cursor is now beyond left edge of visible
			if(cursor.cursorIdx < displayText.leftIdx)
			{
				// Move the display left, and draw from the left
				displayText.leftIdx--;
				displayText.hintLeft = true;
				
				glyphVector = null;
			}
		}
	}

	/**
	 * Try to move the cursor right if possible. 
	 * Then, if necessary, the display is modified so that the cursor remains in view. 
	 */
	private void tryCursorRight() {
		if(cursor.moveRight())
		{
			// Cursor at right edge of visible
			if(cursor.cursorIdx > displayText.rightIdx)
			{
				// Move the display right, and draw from the right
				displayText.rightIdx++;
				displayText.hintLeft = false;
				
				glyphVector = null;
			}
		}
	}
	
	/**
	 * A special lightweight class to take care of cursor-related operations, 
	 * such as tracking its location and drawing it. Not static - maintaining a reference 
	 * to the parent TextField is just fine if not ideal. 
	 */
	class Cursor {
		
		/** Index of the cursor in the text. Starts at 0, for behind the first character. Max = text length */
		private int cursorIdx;
		
		/** Cursor sprite */
		private BufferedImage cursorImg;
		
		/** Indicates if the cursor should draw itself. Unrelated to having focus - for the blinking instead */
		private volatile boolean blink = false;
		
		/**
		 * Create a cursor placed at the beginning of the text field. 
		 */
		public Cursor()
		{
			cursorIdx = 0;
			
			// Obtain the image to use, and size it to 90% of the text area
			cursorImg = SpriteManager.instance().getScaledSprite(SpriteSet.TEXT_FIELD, 0, -1, (int) (getHeight() * 0.9));
		}
		
		public void draw(Graphics2D g2d, int offsetX, int offsetY)
		{
			// Don't bother drawing if the blink is currently 'off'
			if(!blink)
			{
				return;
			}
			
			// Figure out how far in the cursor needs to be drawn
			String visibleTextBeforeCursor = displayText.getDisplayed().substring(0, cursorIdx - displayText.leftIdx);
			int widthBeforeCursor = g2d.getFontMetrics().stringWidth(visibleTextBeforeCursor);
			
			// Draw the sprite. Vertically centered. 
			g2d.drawImage(cursorImg, offsetX + widthBeforeCursor + 3, offsetY + ((getHeight() - cursorImg.getHeight()) / 2), null);
		}
		
		/**
		 * Move the cursor to the given index. Not bound checked, must be within display bounds
		 * @param index Absolute index of the cursor
		 */
		public void move(int index)
		{
			cursorIdx = index;
		}
		
		/**
		 * Move the cursor to the left, if possible. If the movement means the text in view 
		 * needs to be adjusted then this method will re-figure the text in view and invalidate the 
		 * field's glyph vector. 
		 */
		public boolean moveLeft()
		{
			// First see if it's even possible to move left
			if(cursorIdx <= 0)
			{
				return false;
			}
			
			cursorIdx--;
			
			return true;
			
		}
		
		/**
		 * Move the cursor to the right, if possible. If the movement means the text in view 
		 * needs to be adjusted then this method will re-figure the text in view and invalidate the 
		 * field's glyph vector. 
		 */
		public boolean moveRight()
		{
			// See if it's even possible to move right
			if(cursorIdx >= text.length())
			{
				return false;
			}
			
			cursorIdx++;

			return true;
		}
		
		/**
		 * Makes the cursor appear if it currently isn't, or disappear if it is being drawn. 
		 */
		public void toggleBlink()
		{
			blink = !blink;
		}
		
	}
	
	/**
	 * String wrapper with the added functionality of keeping track of which substring indices 
	 * are bounding the displayable text. For keeping track of how much and what to show. 
	 */
	class DisplayableText {
		
		/** The String containing text that should be in view */
		protected String displayed;
		
		/** The left inclusive bound */
		protected int leftIdx;
		
		/** The right exclusive bound */
		protected int rightIdx;
		
		/** True if the text should be sized left to right (false for right-to-left) */
		protected boolean hintLeft;
		
		/** Create new displayable text on the premise the text field starts blank */
		public DisplayableText()
		{
			// Puts all fields to default
			reset();
		}
		
		/**
		 * Figure out what text should be displayed. 
		 * Upon completion, the displayText will have its text and indices changed to reflect what 
		 * should currently be drawn into the field. 
		 * @param g2d The graphics object currently being used for drawing
		 */
		protected void updateDisplay(Graphics2D g2d) 
		{
			FontMetrics metrics = g2d.getFontMetrics();
			
			// See if all of the text will fit. Then we'll just use all of the text. 
			int allTextWidth = metrics.stringWidth(text);
			if(allTextWidth < getWidth())
			{
				// It'll all fit. Display all of it.
				displayAll();
				
				return;
			}
			
			// See if we're checking size from the left or right
			if(hintLeft)
			{
				determineLeftwise(metrics);
			}
			else {
				determineRightwise(metrics);
			}
		}

		/**
		 * Update the display by maintaining the right index and showing as much text as possible 
		 * moving right to left. The display field members are changed, the glyph vector is not invalidated.
		 * @param metrics A FontMetrics object to measure text with
		 */
		protected void determineRightwise(FontMetrics metrics) 
		{
			// Since right - start at the end
			int startIdx = rightIdx;
			int endIdx = startIdx;
			
			// Still go until we've checked if everything will fit...
			while(endIdx > 0)
			{
				int trialWidth = metrics.stringWidth(text.substring(endIdx, startIdx));
				
				// ... or until no more will fit. 
				if(trialWidth + 5 > getWidth())
				{
					endIdx++;
					break;
				} else {
					endIdx--;
				}
			}
			
			// And update the display similarly
			setFields(text.substring(endIdx, startIdx), endIdx, startIdx);
		}

		/**
		 * Update the display by maintaining the left index and showing as much text as possible 
		 * moving left to right. The display field members are changed, the glyph vector is not invalidated.
		 * @param metrics A FontMetrics object to measure text with
		 */
		protected void determineLeftwise(FontMetrics metrics) 
		{
			// Since left - start at the left end
			int startIdx = leftIdx;
			int endIdx = startIdx;
			
			// Go until we've checked if everything will fit...
			while(endIdx < text.length())
			{
				// Width of current subsection of text
				int trialWidth = metrics.stringWidth(text.substring(leftIdx, endIdx));
				
				// ... or until no more will fit. 
				if(trialWidth + 5 > getWidth())
				{
					endIdx--;
					break;
				} else {
					endIdx++;
				}
			}
			
			// Now we know how much to show. Update the display appropriately. 
			setFields(text.substring(startIdx, endIdx), startIdx, endIdx);
		}

		/**
		 * Set the field members to display all of the text currently in the field
		 */
		protected void displayAll() 
		{
			// Display all of the text, from start to end. 
			setFields(text, 0, text.length());
		}
		
		/**
		 * Reset all field members back to default, as if there is no text to display. 
		 */
		protected void reset()
		{
			// By default, no text showing and both indices are 0
			setFields("", 0, 0);
			
			// Drawing is also from the left by default
			this.hintLeft = true;
		}
		
		/** @return The text currently in view */
		protected String getDisplayed()
		{
			return displayed;
		}
		
		/**
		 * Set all of the fields for displayed text. 
		 * @param text The text being shown
		 * @param leftIdx The left index, inclusive
		 * @param rightIdx The right index, exclusive
		 */
		protected void setFields(String text, int leftIdx, int rightIdx)
		{
			this.displayed = text;
			this.leftIdx = leftIdx;
			this.rightIdx = rightIdx;
		}
		
	}
	
	/**
	 * A task to take care of making the cursor blink, rather than always being drawn.
	 */
	class CursorBlinkTask extends CancellableVoidTask {

		/** This task will make the cursor toggle between showing and not showing */
		@Override
		public void execute() 
		{
			cursor.toggleBlink();
		}
		
	}

}