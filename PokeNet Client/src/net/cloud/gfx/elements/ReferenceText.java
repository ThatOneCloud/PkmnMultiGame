package net.cloud.gfx.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.function.Function;

import net.cloud.client.util.IteratorException;

/**
 * For when some text element will need to be updated, but there is no convenient 
 * event to base the text update on.  This element requires a Supplier to determine 
 * what the text should be. The Supplier does not necessarily have to provide a new 
 * String each request - in fact if possible it is safe to return getText() when a new 
 * String is not needed to avoid initialization costs.
 */
public class ReferenceText extends Text {
	
	/** What we'll be getting our new strings from */
	private Function<String, String> reference;
	
	/**
	 * Create a new Reference text object which will initially display the given text, 
	 * and use the given Supplier to obtain updated text strings. The color and font 
	 * are left as default. 
	 * @param text Initial text
	 * @param reference Supplies updated text
	 */
	public ReferenceText(String text, Function<String, String> reference) 
	{
		super(text);

		this.setReference(reference);
	}
	
	/**
	 * Create a new Reference text object which will initially display the given text, 
	 * and use the given Supplier to obtain updated text strings. The color and font 
	 * are left as default. The string's top left edge will be at the given position.
	 * @param text Initial text
	 * @param x The x-position. The left edge of the text
	 * @param y The y-position. The top edge of the text
	 * @param reference Supplies updated text
	 */
	public ReferenceText(String text, int x, int y, Function<String, String> reference) 
	{
		super(text, x, y);

		this.setReference(reference);
	}

	/**
	 * Create a new Reference text object which will initially display the given text, 
	 * and use the given Supplier to obtain updated text strings. The color and font are also specified. 
	 * The position will be the top left edge of the text. 
	 * @param text Initial text
	 * @param x The x-position. The left edge of the text
	 * @param y The y-position. The top edge of the text
	 * @param font Initial font
	 * @param color Initial color
	 * @param reference Supplies updated text
	 */
	public ReferenceText(String text, int priority, int x, int y, Font font, Color color, Function<String, String> reference) 
	{
		super(text, Text.PRIORITY, x, y, font, color);
		
		this.setReference(reference);
	}
	
	/**
	 * Draw the text in the desired font and color at the position. 
	 * The text will be drawn by the top left corner, rather than baseline. 
	 * Before drawing, the supplier will be queried for an updated text string. 
	 */
	@Override
	public void drawElement(Graphics g, int offsetX, int offsetY) throws IteratorException
	{
		// Renew the text. Function is given the current text as a convenience
		super.setText(reference.apply(getText()));
		
		// Superclass can handle drawing
		super.drawElement(g, offsetX, offsetY);
	}

	/**
	 * @return The Function that is providing new strings
	 */
	public Function<String, String> getReference() {
		return reference;
	}

	/**
	 * @param reference A new Function to provide text strings
	 */
	public void setReference(Function<String, String> reference) {
		this.reference = reference;
	}

}
