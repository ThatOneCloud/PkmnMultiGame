package net.cloud.gfx.elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.cloud.client.util.IteratorException;
import net.cloud.gfx.constants.Priority;

/**
 * Simple text element which extends on the abilities of the plain Text label by 
 * centering the text within the drawing area of this element. The plain Text element 
 * normally draws with the top left of the text at the x,y coordinate. This places the text 
 * in the center both vertically and horizontally. 
 */
public class CenteredText extends Text {
	
	/** Default priority of Text. Moderate. */
	public static final int PRIORITY = Priority.MED;
	
	/** Bounding rectangle around the displayed text */
	private Rectangle2D textBounds;
	
	/**
	 * Create a most basic centered text element. Default font and color, located at 0,0
	 * @param text The text to show
	 * @param width The width of the bounding area
	 * @param height The height of the bounding area
	 */
	public CenteredText(String text, int width, int height)
	{
		super(text);
		
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * Create a centered text element. Default font and color, located at the given position.
	 * @param text The text to show
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the bounding area
	 * @param height The height of the bounding area
	 */
	public CenteredText(String text, int x, int y, int width, int height)
	{
		super(text, x, y);
		
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * Create a centered text element. Default font and color, located at the given position. 
	 * The font and color can be changed after construction via setFont() and setColor()
	 * @param text The text to show
	 * @param priority The Z-priority
	 * @param x X-location
	 * @param y Y-location
	 * @param width The width of the bounding area
	 * @param height The height of the bounding area
	 */
	public CenteredText(String text, int priority, int x, int y, int width, int height)
	{
		super(text, x, y);
		
		setPriority(priority);
		
		setWidth(width);
		setHeight(height);
	}

	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException 
	{
		Graphics2D g2d = (Graphics2D) g;
		
		// Set font and color early
		g.setFont(getFont());
		g.setColor(getColor());
		
		// Still run through the vector and height checks
		updateGlyphVector(g2d);
		updateHeight(g2d);
		
		// Now draw the text smack in the middle
		g2d.drawGlyphVector(glyphVector, 
				((float) (offsetX + ((getWidth() - textBounds.getWidth()) / 2.0))), 
				((float) (offsetY + (getHeight() / 2.0) + (textBounds.getHeight() / 2.0) - 3)));
	}
	
	/**
	 * Update the glyph vector if need be, so the text is updated - via the superclass. 
	 * This will also refresh the logical bounds of the text. 
	 * @param g2d The graphics object currently being used to draw with
	 */
	@Override
	protected void updateGlyphVector(Graphics2D g2d)
	{
		if(glyphVector == null)
		{
			// The font, current graphics, and text determine the glyphs
			glyphVector = getFont().createGlyphVector(g2d.getFontRenderContext(), getText());
			
			// The logical bounds may now be different
			textBounds = glyphVector.getLogicalBounds();
		}
	}

}
