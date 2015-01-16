package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import net.cloud.gfx.Colors;
import net.cloud.gfx.Fonts;
import net.cloud.mmo.util.IteratorException;

/**
 * Analogous to the JLabel. Used to display some text string on the screen. 
 * Supports changing the font and color. The position is the top left, like everything else. 
 */
public class Text extends Element {
	
	/** Default priority of Text. Moderate. */
	public static final int PRIORITY = 5;
	
	/** The text. Yeah. */
	private String text;
	
	/** The font. Yeah. */
	private Font font;
	
	/** The color. Yeah. */
	private Color color;
	
	/** The height of the text - to determine the correct drawing position */
	private int height;
	
	/** Flag indicating if the height variable needs to be updated */
	private volatile boolean updateHeight;
	
	/**
	 * Create a new text label. It will use the default font and color. 
	 * It will use the default priority and be at 0,0
	 * @param text The text to show
	 */
	public Text(String text)
	{
		this(text, PRIORITY, 0, 0, Fonts.DEFAULT.get(), Colors.BLACK.get());
	}
	
	/**
	 * Create a new text label with default color and font. 
	 * It will use the default priority but the top left corner will be at the given position
	 * @param text The text
	 * @param x The x-position. The left edge of the text
	 * @param y The y-position. The top edge of the text
	 */
	public Text(String text, int x, int y)
	{
		this(text, PRIORITY, x, y, Fonts.DEFAULT.get(), Colors.BLACK.get());
	}
	
	/**
	 * Create a new text label with the given attributes
	 * @param text The text
	 * @param priority The z-priority
	 * @param x The x-position. The left edge of the text
	 * @param y The y-position. The top edge of the text
	 * @param font The font. Check the {@link Fonts} enum for values.
	 * @param color The color. Check the {@link Colors} enum for values.
	 */
	public Text(String text, int priority, int x, int y, Font font, Color color)
	{
		super(null, priority, x, y);
		
		this.text = text;
		this.font = font;
		this.color = color;
		
		updateHeight = true;
	}

	/**
	 * Draw the text in the desired font and color at the position. 
	 * The text will be drawn by the top left corner, rather than baseline. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException 
	{
		// Make sure the height we have on record is good
		updateHeight(g);
		
		// Now we need to make drawing happen in our font and color
		g.setFont(font);
		g.setColor(color);
		
		// Ready to draw the text
		g.drawString(text, offsetX, offsetY + height);
	}
	
	/**
	 * @return The text in this Text
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Set this element to show some different text
	 * @param text The new text to show
	 */
	public void setText(String text)
	{
		this.text = text;
		
		// We'll need to update the height, it may have changed
		updateHeight = true;
	}
	
	/**
	 * @return The Font object being used to draw this text
	 */
	public Font getFont()
	{
		return font;
	}
	
	/**
	 * Change the Font in order to change how the text appears. 
	 * For some constants, see {@link Fonts}t The new Font object
	 */
	public void setFont(Font font)
	{
		this.font = font;
		
		// We'll need to update the height, it may have changed
		updateHeight = true;
	}
	
	/**
	 * Change the size of the font in order to change how the text appears
	 * @param size The new size of the text, in typical point fashion
	 */
	public void setFontSize(int size)
	{
		this.font = font.deriveFont((float) size);
		
		// We'll need to update the height, it may have changed
		updateHeight = true;
	}
	
	/**
	 * Change the style of the font in order to change how the text appears
	 * @param style The style constant - see {@link Font}
	 */
	public void setFontStyle(int style)
	{
		this.font = font.deriveFont(style);
		
		// We'll need to update the height, it may have changed
		updateHeight = true;
	}
	
	/**
	 * @return The Color of the text
	 */
	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Set the color the the text will appear. For some values, see {@link Colors}
	 * @param color The color the text will be
	 */
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	/**
	 * Calculate and assign the height of this text label. 
	 * This is necessary to draw in the right position, and must be done if the font or text changes. 
	 * When this is called, action will only be taken if the update flag is set. The flag will be cleared.
	 * @param g The graphics object this text is being drawn to
	 */
	private void updateHeight(Graphics g)
	{
		if(updateHeight)
		{
			// Easiest way is using the graphic's object to obtain a FontMetrics
			this.height = g.getFontMetrics(font).getHeight();
			
			updateHeight = false;
		}
	}

}
