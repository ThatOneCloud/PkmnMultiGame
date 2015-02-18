package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.GlyphVector;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.constants.Fonts;
import net.cloud.gfx.constants.Priority;

/**
 * Analogous to the JLabel. Used to display some text string on the screen. 
 * Supports changing the font and color. The position is the top left, like everything else. 
 */
public class Text extends AbstractElement {
	
	/** Default priority of Text. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** The text. Yeah. */
	private String text;
	
	/** The font. Yeah. */
	private Font font;
	
	/** The color. Yeah. */
	private Color color;
	
	/** The height of the text - to determine the correct drawing position */
	protected int textHeight;
	
	/** A GlyphVector which will actually be used to draw the text */
	protected GlyphVector glyphVector;
	
	/** Flag indicating if the metrics variables needs to be updated */
	protected volatile boolean updateMetrics;
	
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
		
		this.glyphVector = null;
		
		updateMetrics = true;
	}

	/**
	 * Draw the text in the desired font and color at the position. 
	 * The text will be drawn by the top left corner, rather than baseline. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException 
	{
		Graphics2D g2d = (Graphics2D) g;
		
		// Set font and color before obtaining any metrics
		g.setFont(font);
		g.setColor(color);
				
		// Make sure the drawing vector is up to date
		updateGlyphVector(g2d);
		
		// Make sure the height we have on record is good
		updateTextMetrics(g2d);
		
		// Ready to draw the text
		g2d.drawGlyphVector(glyphVector, offsetX, offsetY + textHeight);
	}
	
	public void clicked(Element clicked, Point relPoint, boolean isRightClick)
	{
		System.err.println("text " + getText() + " clicked");
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
		
		// We'll need to update some things that have maybe now changed
		glyphVector = null;
		updateMetrics = true;
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
		
		// We'll need to update the height and vector. They've likely both changed.
		glyphVector = null;
		updateMetrics = true;
	}
	
	/**
	 * Change the size of the font in order to change how the text appears
	 * @param size The new size of the text, in typical point fashion
	 */
	public void setFontSize(int size)
	{
		this.font = font.deriveFont((float) size);
		
		// We'll need to update the height and vector. They've likely both changed.
		glyphVector = null;
		updateMetrics = true;
	}
	
	/**
	 * Change the style of the font in order to change how the text appears
	 * @param style The style constant - see {@link Font}
	 */
	public void setFontStyle(int style)
	{
		this.font = font.deriveFont(style);
		
		// We'll need to update the height and vector. They've likely both changed.
		glyphVector = null;
		updateMetrics = true;
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
	 * Update the glyph vector if need be, so the text is updated. 
	 * @param g2d The graphics object currently being used to draw with
	 */
	protected void updateGlyphVector(Graphics2D g2d)
	{
		if(glyphVector == null)
		{
			// The font, current graphics, and text determine the glyphs
			glyphVector = font.createGlyphVector(g2d.getFontRenderContext(), text);
		}
	}
	
	/**
	 * Calculate and assign the text height and element height of this text label. 
	 * The element's width is also determined. 
	 * This is necessary to draw in the right position, and must be done if the font or text changes. 
	 * When this is called, action will only be taken if the update flag is set. The flag will be cleared.
	 * @param g The graphics object this text is being drawn to
	 */
	protected void updateTextMetrics(Graphics g)
	{
		if(updateMetrics)
		{
			// Easiest way is using the graphic's object to obtain a FontMetrics
			this.textHeight = g.getFontMetrics(font).getAscent();
			
			// Set the height of the element as well, but to a different more maximum height
			setHeight(g.getFontMetrics(font).getMaxAscent() + g.getFontMetrics(font).getMaxDescent());
			
			// Set the width of the element
			setWidth((int) glyphVector.getLogicalBounds().getWidth());
			
			updateMetrics = false;
		}
	}

}
