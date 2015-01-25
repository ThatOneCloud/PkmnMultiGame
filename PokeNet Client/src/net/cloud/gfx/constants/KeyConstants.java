package net.cloud.gfx.constants;

/**
 * A utility class that deals with keys and their events. For example, keys 
 * for global hotkeys are stored here. There are also some custom re-purposed 
 * characters defined here.  These are characters that could be typed, but are 
 * very unlikely to be useful in this application. So keys that normally do not have 
 * a character can be assigned to a character for homogeneous handling. 
 */
public class KeyConstants {
	
	// BEGIN CUSTOM CHARACTERS //

	/** Where the re-purposed characters begin. The lowest possible value for them. */
	public static final char FIRST_CUSTOM_CHAR = 0xFF00;

	/** The Shift+Tab combination, treated as a character of its own. */
	public static final char SHIFT_TAB = 0xFF00;

	/** Where the re-purposed characters end. The highest possible value for them. */
	public static final char LAST_CUSTOM_CHAR = 0xFFEE;

	// END CUSTOM CHARACTERS //
	
	/** Global hotkey - show stat overlay */
	public static final char STAT_OVERLAY = 'o';
	
	/** Global hotkey - show test interface (set to null to disable) */
	public static final char TEST_INTERFACE = 't';
	
	/** Constant for the character which will traverse to the next Focusable in line */
	public static final char CHANGE_FOCUS_NEXT = '\t';
	
	/** Constant for the character which will traverse to the previous Focusable in line */
	public static final char CHANGE_FOCUS_PREVIOUS = SHIFT_TAB;

}
