package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.GlyphVector;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Colors;
import net.cloud.gfx.constants.Fonts;
import net.cloud.gfx.constants.Priority;

/**
 * A text area is a block of text. Unlike a Text label, a Text Area will utilize line breaks and make 
 * itself fit within a bounding width. The height on the other hand will change based on the space 
 * needs of the text.
 */
public class TextArea extends AbstractElement {
	
	/** Default priority of Text. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** The actual text in the area */
	private String text;
	
	/** Font being used for the text */
	private Font font;
	
	/** The color of the text */
	private Color color;
	
	/** A flag to track when the text has changed and the vectors need to be updated */
	private volatile boolean updateNeeded;
	
	/** A series of glyph vectors that represent each line of text */
	private List<GlyphVector> vectors;
	
	/** The maximum height of a line of text, for spacing out each line */
	private int lineHeight;
	
	/**
	 * Create a TextArea with the given text in it. It will be placed at the given location and be bounded 
	 * to the given width. The height will adjust itself to the needed height for all of the text. 
	 * The font will be the default and the color will be black.
	 * @param text The text in the area
	 * @param x X location
	 * @param y Y location
	 * @param width Bounding width of text
	 */
	public TextArea(String text, int x, int y, int width)
	{
		this(text, x, y, width, Fonts.DEFAULT.get(), Colors.BLACK.get());
	}
	
	/**
	 * Create a TextArea with the given text in it. It will be placed at the given location and be bounded 
	 * to the given width. The height will adjust itself to the needed height for all of the text. 
	 * The font and color will be those provided.
	 * @param text The text in the area
	 * @param x X location
	 * @param y Y location
	 * @param width Bounding width of text
	 * @param font The font to use
	 * @param color The color of the text
	 */
	public TextArea(String text, int x, int y, int width, Font font, Color color)
	{
		super(PRIORITY, x, y);
		super.setWidth(width);
		
		this.text = text;
		this.font = font;
		this.color = color;
		
		// We start off needing an update, can't do it during construction
		this.updateNeeded = true;
		
		// Linked list suits our purposes pretty well
		vectors = new LinkedList<>();
	}
	
	/**
	 * Draw the text to the graphics. If an update is needed, the text area's layout will 
	 * be updated before drawing. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		Graphics2D g2d = (Graphics2D) g;
		
		g.setFont(font);
		g.setColor(color);

		// Update all of the vectors and metrics if need be
		update(g2d);
		
		// Draw all of the vectors in turn
		int drawPosY = offsetY + lineHeight;
		Iterator<GlyphVector> vectorIt = vectors.iterator();
		while(vectorIt.hasNext())
		{
			// Grab the vector and draw it
			GlyphVector vector = vectorIt.next();
			g2d.drawGlyphVector(vector, offsetX, drawPosY);
			
			// Increase the draw position for the next line
			drawPosY += lineHeight;
		}
		
		// Our height becomes where we finished - where we started. Set every time but it's not a big deal
		setHeight(drawPosY - lineHeight + g2d.getFontMetrics().getMaxDescent() - offsetY);
	}
	
	/**
	 * Overridden so that the change will cause an update of the element's text configuration
	 */
	@Override
	public void setRectangle(Rectangle r)
	{
		super.setRectangle(r);
		
		updateNeeded = true;
	}
	
	/**
	 * Overridden so that the change will cause an update of the element's text configuration
	 */
	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		updateNeeded = true;
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
	 * For some constants, see {@link Fonts}
	 * @param font The new Font object
	 */
	public void setFont(Font font)
	{
		this.font = font;
		
		// We'll need to update configuration now
		updateNeeded = true;
	}
	
	/**
	 * Change the size of the font in order to change how the text appears
	 * @param size The new size of the text, in typical point fashion
	 */
	public void setFontSize(int size)
	{
		this.font = font.deriveFont((float) size);
		
		// We'll need to update configuration now
		updateNeeded = true;
	}
	
	/**
	 * Change the style of the font in order to change how the text appears
	 * @param style The style constant - see {@link Font}
	 */
	public void setFontStyle(int style)
	{
		this.font = font.deriveFont(style);
		
		// We'll need to update configuration now
		updateNeeded = true;
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
	 * If an update is needed, update the glyph vectors, and various text metrics. 
	 * The update will clear the updateNeeded flag.
	 * @param g2d The graphics object currently being used to draw with
	 */
	private void update(Graphics2D g2d)
	{
		// Update isn't even needed
		if(!updateNeeded)
		{
			return;
		}
		
		// Re-populate the list of vectors
		updateVectors(g2d);
		
		// Line height also needs to be updated
		updateLineHeight(g2d);
		
		// Clear the flag now
		updateNeeded = false;
	}

	/**
	 * Update the glyph vectors by clearing them and then re-populating the list 
	 * so that each vector is a line of text.
	 * @param g2d The graphics object currently being used to draw with
	 */
	private void updateVectors(Graphics2D g2d)
	{
		// Clear the list, we're beginning anew
		vectors.clear();
		
		// We need the text in the form of an attributed character iterator, for the line break measurer
		AttributedString attrString = new AttributedString(text);
		attrString.addAttribute(TextAttribute.FONT, font);
		AttributedCharacterIterator attrIt = attrString.getIterator();
		
		// LineBreakMeasurer usually provides a TextLayout, we'll just use its line demarcation abilities
		LineBreakMeasurer lbm = new LineBreakMeasurer(attrIt, g2d.getFontRenderContext());
		
		// Now we'll grab lines until there are no more left
		while(lbm.getPosition() < text.length())
		{
			// So the next position is the end of the next line, basically
			int nextPos = lbm.nextOffset(getWidth());
			
			// Now we have some bounds, we'll break off a line of text into a vector
			vectors.add(font.createGlyphVector(g2d.getFontRenderContext(), text.substring(lbm.getPosition(), nextPos)));
			
			// And move the position forward
			lbm.setPosition(nextPos);
		}
	}
	
	/**
	 * Update the line height variable to reflect the current maximum height of a line of text
	 * @param g2d The graphics object currently being used to draw with
	 */
	private void updateLineHeight(Graphics2D g2d)
	{
		// Ascent + Descent make up the full extent from baseline to baseline
		this.lineHeight = g2d.getFontMetrics().getMaxAscent() + g2d.getFontMetrics().getMaxDescent();
	}
	

}
