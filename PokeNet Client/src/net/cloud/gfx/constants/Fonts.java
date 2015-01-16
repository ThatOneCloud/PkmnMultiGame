package net.cloud.gfx.constants;

import java.awt.Font;

/**
 * Enumeration of various fonts that can be used throughout the application. 
 * Useful for standardizing font usage and overall appearance. 
 */
public enum Fonts {
	
	/** Default font for use wherever. */
	DEFAULT(new Font("Arial", Font.PLAIN, FontConstants.SIZE_MEDIUM));
	
	/** The font this enumerable value is wrapping around */
	private final Font font;
	
	/**
	 * Create a new enumerable value which will be associated with the given font
	 * @param font The font the enumerable value will represent
	 */
	private Fonts(Font font)
	{
		this.font = font;
	}
	
	/**
	 * Obtain the Font object which the enumerable constant is mapped to
	 * @return The associated Font object
	 */
	public Font get()
	{
		return font;
	}

}
