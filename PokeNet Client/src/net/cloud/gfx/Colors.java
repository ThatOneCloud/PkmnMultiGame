package net.cloud.gfx;

import java.awt.Color;

/**
 * Enumeration of various color objects that can be used throughout the application
 */
public enum Colors {
	
	// BEGIN DEFAULT COLOR CONSTANTS //
	/** Color.BLACK */
	BLACK(Color.BLACK),
	
	/** Color.BLUE */
	BLUE(Color.BLUE),
	
	/** Color.CYAN */
	CYAN(Color.CYAN),
	
	/** Color.DARK_GRAY */
	DARK_GRAY(Color.DARK_GRAY),
	
	/** Color.GRAY */
	GRAY(Color.GRAY),
	
	/** Color.GREEN */
	GREEN(Color.GREEN),
	
	/** Color.LIGHT_GRAY */
	LIGHT_GRAY(Color.LIGHT_GRAY),
	
	/** Color.MAGENTA */
	MAGENTA(Color.MAGENTA),
	
	/** Color.ORANGE */
	ORANGE(Color.ORANGE),
	
	/** Color.PINK */
	PINK(Color.PINK),
	
	/** Color.RED */
	RED(Color.RED),
	
	/** Color.WHITE */
	WHITE(Color.WHITE),
	
	/** Color.YELLOW */
	YELLOW(Color.YELLOW),
	// END DEFAULT COLOR CONSTANTS //
	
	
	// BEGIN CUSTOM COLORS //
	/** Custom purple */
	PURPLE(new Color(160, 32, 240)),
	
	/** Custom brown */
	BROWN(new Color(165, 42, 42)),
	
	/** Custom dark green */
	DARK_GREEN(new Color(0, 100, 0)),
	
	/** Custom navy blue */
	NAVY_BLUE(new Color(35, 35, 142)),
	
	/** Custom slate (one of my favorites) */
	SLATE(new Color(112, 128, 144));
	// END CUSTOM COLORS //
	
	/** The Color object behind the enumerable object */
	private final Color color;
	
	/**
	 * Create a new enumerable value that will hold the given color object
	 * @param color The color the enumerable value represents
	 */
	private Colors(Color color)
	{
		this.color = color;
	}
	
	/**
	 * Obtain the Color object which the enumerable constant is mapped to
	 * @return The associated Color object
	 */
	public Color get()
	{
		return color;
	}

}
